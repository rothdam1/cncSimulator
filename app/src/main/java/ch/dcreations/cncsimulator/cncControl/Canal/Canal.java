package ch.dcreations.cncsimulator.cncControl.Canal;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCCodeExecuter.CNCCodeDecoder;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCCodeExecuter.CNCCodeExecutes;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCCodeExecuter.CNCProgramCommand;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.*;
import ch.dcreations.cncsimulator.config.Config;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final List<Future<Boolean>> futures = new ArrayList<>();
    private CanalDataModel canalDataModel;
    private int countOfProgramLines = 0;
    private String cncProgramText = "";
    private final AtomicBoolean brakeRunningCode = new AtomicBoolean(false);
    private final SimpleIntegerProperty programLinePosition = new SimpleIntegerProperty(0);
    boolean RunLineIsCompleted = false;

    public Canal(Map<AxisName, CNCAxis> cncAxes, Map<SpindelNames, CNCSpindle> cncSpindles) {
        super();
        try {
            canalDataModel = new CanalDataModel(cncAxes,cncSpindles,Plane.G18);
            canalDataModel.setCurrentSelectedSpindle(SpindelNames.S1);
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    @Override
    public Boolean call() {
        try {
            if (cncProgramText != null) {
                String[] lines = cncProgramText.replace("\n", "").split(";");
                countOfProgramLines = lines.length;
                canalDataModel.setCanalRunState(true);
                switch (canalDataModel.getCanalState()) {
                    case SINGLE_STEP -> runNextLine(lines[programLinePosition.get()]);
                    case RUN -> runAllLines(lines);
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
        cncProgramText = programText + ";\n%";// ADD an END OF FILE
    }

    public String getProgramText() {
        return cncProgramText;
    }


    private void runAllLines(String[] lines) throws Exception {
        programLinePosition.set(0);
        if (areAllCallsFinished()) {
            RunLineIsCompleted = true;
            while (programLinePosition.get() <= countOfProgramLines - 2) {
                    RunLineIsCompleted = false;
                    for(Future<Boolean> future : futures){
                        future.get();
                    }
                    runNextLine(lines[programLinePosition.get()]);
            }
        }
    }

     private void runNextLine(String line) throws Exception {
        CNCCodeDecoder cncCodeDecoder = new CNCCodeDecoder(canalDataModel);
        CNCProgramCommand cncProgramCommand = cncCodeDecoder.splitCommands(line);
            if(areAllCallsFinished() ){
                goToNextLine();
                executeNCCommand(cncProgramCommand);
            }
    }

    private boolean areAllCallsFinished() {
        boolean allCallsFinished = true;
        for (Future<Boolean> call : futures) {
            if (!call.isDone()) {
                logger.log(Level.INFO, "CALL IS NOT FINISHED");
                allCallsFinished = false;
            }
        }
        return allCallsFinished;
    }

    synchronized private void executeNCCommand(CNCProgramCommand cncProgramCommand) {
        Callable callable = new CNCCodeExecutes(cncProgramCommand,canalDataModel,brakeRunningCode);
        futures.add(executorService.submit(callable));

    }

    private void unbindAxis() {
        for (AxisName axisName : canalDataModel.getCncAxes().keySet()) {
            canalDataModel.getCncAxes().get(axisName).axisPositionProperty().unbind();
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

    public void stopRunning() throws InterruptedException {
        programLinePosition.set(0);
        canalDataModel.setCanalRunState(false);
        futures.forEach((x) -> {
            try {
                x.get(Config.POSITION_CALCULATION_RESOLUTION,TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                x.cancel(false);
            }
        });
        if (!areAllCallsFinished()) {
            stop();
        }
        unbindAxis();
    }

    public void stopNow() {
        executorService.shutdownNow();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void stop() throws InterruptedException {
        futures.forEach((future)-> future.cancel(false));
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
    }

    public ObservableIntegerValue programLinePositionProperty() {
    return programLinePosition;
    }


    public void brakerunningCode() {
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
        boolean allCallsFinished = false;
        for (Future<Boolean> call : futures) {
            if (!call.isDone()) {
              //  logger.log(Level.INFO, "CALL IS NOT FINISHED");
                allCallsFinished = true;
            }
        }
        return allCallsFinished;
    }
}
