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
    private CNCProgram cncProgramText = new CNCProgram("");


    private Position startPosition = new Position(0,0,0,0,0,0);
    private final AtomicBoolean brakeRunningCode = new AtomicBoolean(false);

    private final AtomicBoolean canalRunningGCode = new AtomicBoolean(false);
    private final SimpleIntegerProperty programLinePosition = new SimpleIntegerProperty(0);

    private final SimpleIntegerProperty executionGodeLinePosition = new SimpleIntegerProperty(0);
    private  List<Vector> linesToDraw = new LinkedList<>();
    private  List<List<Map<AxisName, Double>>> ProgramLinesPaths = new LinkedList<>();


    public Canal(Map<AxisName, CNCAxis> cncAxes, Map<SpindelNames, CNCSpindle> cncSpindles ) {
        super();
        programLinePosition.set(0);
        caculatePath(startPosition);
        try {
            canalDataModel = new CanalDataModel(cncAxes, cncSpindles, Plane.G18, Config.CALCULATION_ERROR_MAX_FOR_CIRCLE_END_POINT);
            canalDataModel.setCurrentSelectedSpindle(SpindelNames.S1);
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    @Override
    public Boolean call() {

            if (cncProgramText != null) {
                countOfProgramLines = cncProgramText.countOfLines();
                canalDataModel.setCanalRunState(true);

                caculatePath(startPosition);
                switch (canalDataModel.getCanalState()) {
                    case SINGLE_STEP -> runNextLine();
                    case RUN -> runAllLines();
                }
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


    private void runAllLines()  {
        programLinePosition.set(0);
        caculatePath(startPosition);
            while (programLinePosition.get() <= countOfProgramLines - 2) {
                runNextLine();
            }
    }


    private void runNextLine()  {
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
                Thread.sleep(VIEW_ACTUALISATION_MULTIPLICATION * milisec);
            }
            if (ProgramLinesPaths.get(lineNumber).size()>1 && getCanalRunState()) {
                x = ProgramLinesPaths.get(lineNumber).get(ProgramLinesPaths.get(lineNumber).size() - 1).get(AxisName.X);
                y = ProgramLinesPaths.get(lineNumber).get(ProgramLinesPaths.get(lineNumber).size() - 1).get(AxisName.Y);
                z = ProgramLinesPaths.get(lineNumber).get(ProgramLinesPaths.get(lineNumber).size() - 1).get(AxisName.Z);
                canalDataModel.getCncAxes().get(AxisName.X).setValue(x);
                canalDataModel.getCncAxes().get(AxisName.Y).setValue(y);
                canalDataModel.getCncAxes().get(AxisName.Z).setValue(z);
            }
        }catch (Exception e){
            logger.log(Level.WARNING,"caculate PATH Error"+e.getMessage());
        }finally {
            canalRunningGCode.set(false);
        }
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

    public void stopRunning() {
        canalDataModel.setCanalRunState(false);
    }

    public void stopAndReset()  {
        stopRunning();
        try {
            Thread.sleep(100);
        }catch (Exception e){
            logger.log(Level.WARNING,"THREAD wait for Stop failed");

        }
        getCncAxes().values().forEach((x) -> x.setValue(0));
        executionGodeLinePosition.set(0);
        linesToDraw.clear();
        ProgramLinesPaths.clear();
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
