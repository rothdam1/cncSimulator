package ch.dcreations.cncsimulator.cncControl.Canal;

import ch.dcreations.cncsimulator.animation.Vector;
import ch.dcreations.cncsimulator.cncControl.CNCProgram;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCCodeExecuter.CNCCommandGenerator;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCCodeExecuter.CNCCommand;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.*;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.Config;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.scene.paint.Color;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ch.dcreations.cncsimulator.config.Config.VIEW_ACTUALISATION_MULTIPLICATION;

/**
 * <p>
 * <p>
 * is a Canal of a CNC Control A Canal contains Axis And Spindles
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */
public class Canal implements Callable<Boolean> {

    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());

    private CanalDataModel canalDataModel;
    private int countOfProgramLines = 0;
    private final CNCProgram cncProgramText = new CNCProgram("");
    private final Lock lockFinishRunProgramCode = new ReentrantLock();
    private final Position startPosition = new Position(0, 0, 0, 0, 0, 0);
    private final AtomicBoolean brakeRunningCode = new AtomicBoolean(false);
    private final AtomicBoolean canalRunningGCode = new AtomicBoolean(false);
    private final SimpleIntegerProperty programLinePosition = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty executionGCodeLinePosition = new SimpleIntegerProperty(0);
    private final List<Vector> linesToDraw = new LinkedList<>();
    private List<List<Map<AxisName, Double>>> ProgramLinesPaths = new LinkedList<>();


    public Canal(Map<AxisName, CNCAxis> cncAxes, Map<SpindelNames, CNCSpindle> cncSpindles) {
        super();
        programLinePosition.set(0);
        calculatePath(startPosition);
        try {
            canalDataModel = new CanalDataModel(cncAxes, cncSpindles, Plane.G18, Config.CALCULATION_ERROR_MAX_FOR_CIRCLE_END_POINT);
            canalDataModel.setCurrentSelectedSpindle(SpindelNames.S1);
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    @Override
    public Boolean call() {
            countOfProgramLines = cncProgramText.countOfLines();
            canalDataModel.setCanalRunState(true);
            calculatePath(startPosition);
            canalRunningGCode.set(true);
            switch (canalDataModel.getCanalState()) {
                case SINGLE_STEP -> runNextLine();
                case RUN -> runAllLines();
            }
            canalRunningGCode.set(false);
        return true;
    }


    public Map<AxisName, CNCAxis> getCncAxes() {
        return Map.copyOf(canalDataModel.getCncAxes());
    }

    public void setProgram(String programText) {
        cncProgramText.setProgramText(programText);
        cncProgramText.addProgramText(";\n%");// ADD an END OF FILE
    }

    public String getProgramText() {
        return cncProgramText.getProgramTextAsText();
    }


    private void runAllLines() {
        programLinePosition.set(0);
        calculatePath(startPosition);
        while (programLinePosition.get() <= countOfProgramLines - 2) {
            runNextLine();
        }
    }


    private void runNextLine() {
        runProgramCode(programLinePosition.get());
        goToNextLine();
    }

    private void runProgramCode(int lineNumber) {
        lockFinishRunProgramCode.lock();
        try {
            //write PATH in to Map, and stores the last end position and set it to the next start Position
            double xPos, yPos, zPos = xPos = yPos = 0;
            for (Map<AxisName, Double> lines : ProgramLinesPaths.get(lineNumber)) {
                linesToDraw.add(new Vector(Color.BLACK, xPos, yPos, zPos, lines.get(AxisName.X), lines.get(AxisName.Y), lines.get(AxisName.Z)));
                xPos = (lines.get(AxisName.X));
                yPos = (lines.get(AxisName.Y));
                zPos = (lines.get(AxisName.Z));
            }
            int positionPasstPointToDraw, lastPositionPasstPointToDraw = positionPasstPointToDraw = 0;
            //Wile Time is not over run program
            while (positionPasstPointToDraw < ProgramLinesPaths.get(lineNumber).size() && getCanalRunState()) {
                long millisecond = (long) (Config.POSITION_CALCULATION_RESOLUTION * 100);
                writeNewPositionToProgramLinesPath(lineNumber, positionPasstPointToDraw);
                if (!brakeRunningCode.get()) {
                    executionGCodeLinePosition.set(executionGCodeLinePosition.get() + positionPasstPointToDraw - lastPositionPasstPointToDraw);
                    lastPositionPasstPointToDraw = positionPasstPointToDraw;
                    positionPasstPointToDraw = positionPasstPointToDraw + VIEW_ACTUALISATION_MULTIPLICATION;
                }
                Thread.sleep(VIEW_ACTUALISATION_MULTIPLICATION * millisecond);
            }
            if (ProgramLinesPaths.get(lineNumber).size() > 1 && getCanalRunState()) {
                writeNewPositionToProgramLinesPath(lineNumber, ProgramLinesPaths.get(lineNumber).size() - 1);
            }
        } catch (Exception e) {
            canalDataModel.getCanalRunState().set(false);
            logger.log(Level.WARNING, "calculate PATH Error" + e.getMessage());
        } finally {
            lockFinishRunProgramCode.unlock();
        }
    }

    private void writeNewPositionToProgramLinesPath(int lineNumber, int linePosition) {
        Set<AxisName> axisSet = new HashSet<>();
        axisSet.add(AxisName.X);
        axisSet.add(AxisName.Y);
        axisSet.add(AxisName.Z);
        axisSet.
                forEach((axisKey) -> canalDataModel.getCncAxes().get(axisKey)
                        .setValue(ProgramLinesPaths.get(lineNumber).get(linePosition).get(axisKey)));
    }


    private void goToNextLine() {
        if (programLinePosition.get() >= countOfProgramLines - 1) {
            programLinePosition.set(0);
        } else {
            programLinePosition.set(programLinePosition.get() + 1);
        }
    }

    public void setCanalState(CanalState canalState) {
        canalDataModel.setCanalState(canalState);
    }


    public void stopAndReset() {
        canalDataModel.setCanalRunState(false);
        try {
            lockFinishRunProgramCode.lock();
            getCncAxes().values().forEach((x) -> x.setValue(0));
            executionGCodeLinePosition.set(0);

        } catch (Exception e) {
            logger.log(Level.WARNING, "THREAD wait for Stop failed");
        } finally {
            canalDataModel.getCanalRunState().set(false);
            lockFinishRunProgramCode.unlock();
        }
    }

    public ObservableIntegerValue programLinePositionProperty() {
        lockFinishRunProgramCode.lock();
        try {
            return programLinePosition;
        } catch (Exception e) {
            return programLinePosition;
        } finally {
            lockFinishRunProgramCode.unlock();
        }
    }


    public void breakRunningCode() {
        brakeRunningCode.set(true);
    }

    public void runBrakedCode() {
        brakeRunningCode.set(false);
    }

    public CanalState getCanalState() {
        return canalDataModel.getCanalState();
    }

    public boolean getCanalRunState() {
        return canalDataModel.getCanalRunState().get();
    }

    public boolean isCanalRunning() {
        return canalRunningGCode.get();
    }

    private void calculatePath(Position startPosition) {
        List<List<Map<AxisName, Double>>> programLinesPaths = new LinkedList<>();
        for (String line : cncProgramText.getProgramText()) {
            try {
                List<Map<AxisName, Double>> positionList = new LinkedList<>();
                CNCCommandGenerator cncCommandDecoder = new CNCCommandGenerator(canalDataModel, startPosition);
                CNCCommand cncProgramCommand = cncCommandDecoder.splitCommands(line);
                positionList.addAll(cncProgramCommand.getPath(canalDataModel));
                startPosition = cncProgramCommand.getEndposition();
                programLinesPaths.add(positionList);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error when calculation Tool Path \n" + e.getMessage());
                canalDataModel.getCanalRunState().set(false);
                return;
            }
        }
        this.ProgramLinesPaths = programLinesPaths;
    }

    public List<Vector> getLinesToDraw() {
        return Collections.unmodifiableList(linesToDraw);
    }

    public IntegerProperty executionPosition() {
        return executionGCodeLinePosition;
    }
}
