package ch.dcreations.cncsimulator.cncControl.Canal;

import ch.dcreations.cncsimulator.animation.AnimationModel;
import ch.dcreations.cncsimulator.animation.CNCAnimation;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCCodeExecuter.CNCCommandGenerator;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCCodeExecuter.CNCCommandExecutor;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCCodeExecuter.CNCCommand;
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

    private final List<Future<Boolean>> futures = new LinkedList<>();
    private CanalDataModel canalDataModel;
    private int countOfProgramLines = 0;
    private String cncProgramText = "";
    private final AtomicBoolean brakeRunningCode = new AtomicBoolean(false);
    private final SimpleIntegerProperty programLinePosition = new SimpleIntegerProperty(0);
    private Optional<CNCAnimation> animationModelOptional = Optional.empty();


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
            while (programLinePosition.get() <= countOfProgramLines - 2) {

                runNextLine(lines[programLinePosition.get()]);
            }
        }
    }

    private void runNextLine(String line) throws Exception {
        waitUntilCallsAreFinished();
        CNCCommandGenerator cncCommandDecoder = new CNCCommandGenerator(canalDataModel);
        CNCCommand cncProgramCommand = cncCommandDecoder.splitCommands(line);
        goToNextLine();
        executeNCCommand(cncProgramCommand);
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
        for (Future<Boolean> call : futures) {
            if (!call.isDone()) {
                logger.log(Level.INFO, "CALL IS NOT FINISHED");
                allCallsFinished = false;
            }
        }
        return allCallsFinished;
    }

    private void executeNCCommand(CNCCommand cncProgramCommand) {
        Callable<Boolean> callable = new CNCCommandExecutor(cncProgramCommand, canalDataModel, brakeRunningCode, animationModelOptional);
        futures.add(executorService.submit(callable));
    }

    private void unbindAxis() {
        canalDataModel.getCncAxes().values().forEach(x -> x.axisPositionProperty().unbind());
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
        waitUntilCallsAreFinished();
        if (!areAllCallsFinished()) {
            stop();
        }
        unbindAxis();
        programLinePosition.set(0);
    }

    public void stopNow() {
        executorService.shutdownNow();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void stop() throws InterruptedException {
        futures.forEach((future) -> future.cancel(false));
        executorService.shutdown();
        if (executorService.awaitTermination(1, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }
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
        boolean allCallsFinished = false;
        for (Future<Boolean> call : futures) {
            if (!call.isDone()) {
                allCallsFinished = true;
            }
        }
        return allCallsFinished;
    }

    public void addAnimationModel(CNCAnimation animationModel) {
        animationModelOptional = Optional.of(animationModel);
    }
}
