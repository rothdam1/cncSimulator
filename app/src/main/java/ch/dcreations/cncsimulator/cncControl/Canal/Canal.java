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
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
    //private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final List<Future<Boolean>> futures = new LinkedList<>();
    private CanalDataModel canalDataModel;
    private int countOfProgramLines = 0;
    private CNCProgram cncProgramText = new CNCProgram("");


    private Position startPosition = new Position(0,0,0,0,0,0);
    private final AtomicBoolean brakeRunningCode = new AtomicBoolean(false);

    private final AtomicBoolean canalRunningGCode = new AtomicBoolean(false);
    private final SimpleIntegerProperty programLinePosition = new SimpleIntegerProperty(0);

    private final SimpleIntegerProperty executionGodeLinePosition = new SimpleIntegerProperty(0);
    private List<Vector> linesToDraw = new LinkedList<>();
    private List<List<Map<AxisName, Double>>> ProgramLinesPaths = new LinkedList<>();


    public Canal(Map<AxisName, CNCAxis> cncAxes, Map<SpindelNames, CNCSpindle> cncSpindles ) {
        super();

        try {
            canalDataModel = new CanalDataModel(cncAxes, cncSpindles, Plane.G18, Config.CALCULATION_ERROR_MAX_FOR_CIRCLE_END_POINT);
            canalDataModel.setCurrentSelectedSpindle(SpindelNames.S1);
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    @Override
    public Boolean call() {
        try {
            if (cncProgramText != null) {

                countOfProgramLines = cncProgramText.countOfLines();
                canalDataModel.setCanalRunState(true);
                switch (canalDataModel.getCanalState()) {
                    case SINGLE_STEP -> runNextLine();
                    case RUN -> runAllLines();
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "NC RUN Exception" + e);
        }
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


    private void runAllLines() throws Exception {
        programLinePosition.set(0);
        caculatePath(startPosition);
            while (programLinePosition.get() <= countOfProgramLines - 2) {
                runNextLine();
            }
    }


    private void runNextLine() throws Exception {
        runProgramCode(programLinePosition.get());
        goToNextLine();
    }

    private void runProgramCode(int lineNumber) {
        canalRunningGCode.set(true);
        double x =0 ;
        double y =0;
        double z = 0;
        for( Map<AxisName, Double> lines :  ProgramLinesPaths.get(lineNumber)){
            linesToDraw.add(new Vector(Color.BLACK,x,y,z, lines.get(AxisName.X),lines.get(AxisName.Y),lines.get(AxisName.Z)));
            x= (lines.get(AxisName.X));
            y = (lines.get(AxisName.Y));
           z = (lines.get(AxisName.Z));
        }
        int i = 0;
        int j = 0;
        try {
            while (i < ProgramLinesPaths.get(lineNumber).size()&& getCanalRunState()) {
                long milisec = (long) (Config.POSITION_CALCULATION_RESOLUTION * 100);
                Thread.sleep(VIEW_ACTUALISATION_MULTIPLICATION * milisec);
                x = ProgramLinesPaths.get(lineNumber).get(i).get(AxisName.X);
                y = ProgramLinesPaths.get(lineNumber).get(i).get(AxisName.Y);
                z = ProgramLinesPaths.get(lineNumber).get(i).get(AxisName.Z);

                canalDataModel.getCncAxes().get(AxisName.X).setValue(x);
                canalDataModel.getCncAxes().get(AxisName.Y).setValue(y);
                canalDataModel.getCncAxes().get(AxisName.Z).setValue(z);
                if (!brakeRunningCode.get()){
                    executionGodeLinePosition.set(executionGodeLinePosition.get() + i - j);
                j = i;
                i = i + VIEW_ACTUALISATION_MULTIPLICATION;
            }
            }
            if (ProgramLinesPaths.get(lineNumber).size()>1) {
                x = ProgramLinesPaths.get(lineNumber).get(ProgramLinesPaths.get(lineNumber).size() - 1).get(AxisName.X);
                y = ProgramLinesPaths.get(lineNumber).get(ProgramLinesPaths.get(lineNumber).size() - 1).get(AxisName.Y);
                z = ProgramLinesPaths.get(lineNumber).get(ProgramLinesPaths.get(lineNumber).size() - 1).get(AxisName.Z);
                canalDataModel.getCncAxes().get(AxisName.X).setValue(x);
                canalDataModel.getCncAxes().get(AxisName.Y).setValue(y);
                canalDataModel.getCncAxes().get(AxisName.Z).setValue(z);
            }
        }catch (Exception e){
            logger.log(Level.WARNING,"caculate PATH"+e.getMessage());
        }finally {
            canalRunningGCode.set(false);
        }
    }

    private void waitUntilCallsAreFinished() {
        futures.forEach((x) -> {
            try {
                x.get(5000, TimeUnit.SECONDS);
            } catch (Exception e) {
                x.cancel(false);
            }
        });
    }

    private boolean areAllCallsFinished() {
        boolean allCallsFinished = true;
        return allCallsFinished;
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

    public void stopRunning() throws InterruptedException {
        canalDataModel.setCanalRunState(false);
    }

    public void stopNow() {
    }

    public void stop() throws InterruptedException {

    }

    public ObservableIntegerValue programLinePositionProperty() {
        return programLinePosition;
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

    private void caculatePath(Position startPosition){
        List<List<Map<AxisName, Double>>> programLinesPaths = new LinkedList<>();
        for (String line : cncProgramText.getProgramText()) {
            try {
                List<Map<AxisName, Double>> positionList = new LinkedList<>();
                CNCCommandGenerator cncCommandDecoder = new CNCCommandGenerator(canalDataModel,startPosition);
                CNCCommand cncProgramCommand = cncCommandDecoder.splitCommands(line);
                positionList.addAll(cncProgramCommand.getPath(canalDataModel));
                startPosition = cncProgramCommand.getEndposition();
                programLinesPaths.add(positionList);
            }catch (Exception e){
                logger.log(Level.WARNING,"Error"+e.getMessage());
            }
        }
        this.ProgramLinesPaths= programLinesPaths;
    }

    public List<Vector> getLinesToDraw() {
        return Collections.unmodifiableList(linesToDraw);
    }

    public IntegerProperty executionPosition() {
        return executionGodeLinePosition;
    }
}
