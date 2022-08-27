package ch.dcreations.cncsimulator.cncControl.Canal;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCCodeExecuter.CNCCodeDecoder;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCCodeExecuter.CNCCodeExecuter;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.CNCAxis;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.CNCSpindle;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.SpindelNames;
import ch.dcreations.cncsimulator.cncControl.Exceptions.AxisOrSpindleDoesNotExistExeption;
import ch.dcreations.cncsimulator.cncControl.GCodes.*;
import ch.dcreations.cncsimulator.config.Config;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
public class Canal implements Callable {

    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private List<Future<Boolean>> futures = new ArrayList<>();

    private List<Callable<Boolean>> runningNCComand = new ArrayList<>();
    CanalDataModel canalDataModel;

    private int countOfProgramLines = 0;
    private String cncProgramText = "";
    private SimpleIntegerProperty programLinePosition = new SimpleIntegerProperty(0);
    boolean RunLineIsCompleted = false;
    final Lock lock = new ReentrantLock();

    public Canal(Map<AxisName, CNCAxis> cncAxes, Map<SpindelNames, CNCSpindle> cncSpindles) {
        super();
        try {
            canalDataModel = new CanalDataModel(cncAxes,cncSpindles);
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
        cncProgramText = programText + ";\n%";// ADD a END OF FILE
    }

    public String getProgramText() {
        return cncProgramText;
    }


    private void runAllLines(String[] lines) throws Exception {
        programLinePosition.set(0);
        if (areAllCallesFinished()) {
            RunLineIsCompleted = true;
            while (programLinePosition.get() <= countOfProgramLines - 2) {
                    RunLineIsCompleted = false;
                    runNextLine(lines[programLinePosition.get()]);
            }
        }
    }

     private void runNextLine(String line) throws Exception {
        CNCCodeDecoder cncCodeDecoder = new CNCCodeDecoder(canalDataModel);
        CNCProgramCommand cncProgramCommand = cncCodeDecoder.splitCommands(line);
            if(futures.stream().anyMatch((x) -> !x.isDone()) ){
                Thread.sleep(Config.POSITION_CALCULATION_RESOLUTION);
            }else {
                goToNextLine();
                executeNCCommand(cncProgramCommand);
            }
    }

    private boolean areAllCallesFinished() {
        boolean allCallesFinished = true;
        for (Future<Boolean> call : futures) {
            if (!call.isDone()) {
                logger.log(Level.INFO, "CALL IS NOT FINISHED");
                allCallesFinished = false;
            }
        }
        return allCallesFinished;
    }

    synchronized private void executeNCCommand(CNCProgramCommand cncProgramCommand) {
        canalDataModel.setCanalRunState(true);
        Callable callable = new CNCCodeExecuter(cncProgramCommand,canalDataModel,lock);
        runningNCComand.add(callable);
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
        Thread.sleep(Config.POSITION_CALCULATION_RESOLUTION * 2);
        if (!areAllCallesFinished()) {
            stop();
        }
        unbindAxis();
    }

    public void stopNow() {
        executorService.shutdownNow();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void stop() throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
    }

    public ObservableIntegerValue programLinePositionProperty() {
    return programLinePosition;
    }


    /**
     * is used to register if the current Program Line in while the Program runs have changed
     *
     * @return linenumber
     */



}
