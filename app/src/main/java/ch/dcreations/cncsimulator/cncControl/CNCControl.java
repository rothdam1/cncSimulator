package ch.dcreations.cncsimulator.cncControl;

import ch.dcreations.cncsimulator.animation.AnimationModel;
import ch.dcreations.cncsimulator.animation.CNCAnimation;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.CNCAxis;
import ch.dcreations.cncsimulator.cncControl.Canal.Canal;
import ch.dcreations.cncsimulator.cncControl.Canal.CanalState;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.value.ObservableIntegerValue;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * <p>
 * The CNC control contains axis and Program
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */
public class CNCControl {

    private CNCState cncRunState = CNCState.STOP;
    private ExecutorService CNCCanalExecutorService;
    private final List<Future<Boolean>> CNCCanalExecuteFuture = new LinkedList<>();
    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());
    private final List<Canal> canals;


    public CNCControl(List<Canal> canals) {
        this.canals = canals;
        CNCCanalExecutorService = canals.size() < 1 ? Executors.newFixedThreadPool(1) : Executors.newFixedThreadPool(canals.size());
    }

    public List<Map<AxisName, CNCAxis>> getCncAxes() {
        List<Map<AxisName, CNCAxis>> listOfAllAxis = new LinkedList<>();
        for (Canal canal : canals) {
            listOfAllAxis.add(canal.getCncAxes());
        }
        return (listOfAllAxis);
    }

    public void setCanal1CNCProgramText(String programText) throws IOException {
        setCNCProgramToCanal(1, programText);
    }

    public void setCanal2CNCProgramText(String programText) throws IOException {
        setCNCProgramToCanal(2, programText);
    }

    private void setCNCProgramToCanal(int canalNumber, String programText) throws IOException {
        if (canals.size() < canalNumber) {
            throw new IOException("Canal does not Exist");
        } else {
            canals.get(canalNumber - 1).setProgram(programText);
        }
    }

    public String getCanal1CNCProgramText() throws IOException {
        return getCNCProgramTextFromCanal(1);
    }

    public String getCanal2CNCProgramText() throws IOException {
        return getCNCProgramTextFromCanal(2);
    }

    private String getCNCProgramTextFromCanal(int canalNumber) throws IOException {
        if (canals.size() < canalNumber) {
            throw new IOException("Canal does not Exist");
        } else {
            return canals.get(canalNumber - 1).getProgramText();
        }
    }

    public void stopAndResetCNCControl() {
        try {
            for (Canal canal : canals) {
                canal.stopRunning();
            }
            waitUntilAllCanalsFinished();
        } catch (Exception e) {
            logger.log(Level.WARNING, "WHILE RESET THE CONTROL " + e.getMessage());
        } finally {
            this.cncRunState = CNCState.STOP;
        }
    }

    public void runCNCProgram() throws Exception {
        if (CNCCanalExecutorService.isShutdown()) {
            CNCCanalExecutorService = Executors.newFixedThreadPool(canals.size());
        }
        for (Canal canal : canals) {
            canal.setCanalState(CanalState.RUN);
        }
        this.cncRunState = CNCState.RUN;
        runCanals();
    }

    private void runCanals() throws Exception {
        if (areAllCanalsFinished()) {
            for (Canal canal : canals) {
                CNCCanalExecuteFuture.add(CNCCanalExecutorService.submit(canal));
            }
        } else {
            throw new Exception("NC IS STILL RUNNING ");
        }
    }

    public void goToNextStepCNCProgram() throws Exception {
        this.cncRunState = CNCState.SINGLE_STEP;
        for (Canal canal : canals) {
            canal.setCanalState(CanalState.SINGLE_STEP);
        }
        runCanals();
    }


    public void terminateCNCControl() throws InterruptedException {
        for (Canal canal : canals) {
            try {
                canal.stop();
            } catch (Exception e) {
                logger.log(Level.WARNING, "CANAL DOES NOT TERMINATE RIGHT");
                canal.stopNow();
            }
        }
        CNCCanalExecutorService.shutdown();
        if (!CNCCanalExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
            CNCCanalExecutorService.shutdownNow();
        }
    }

    /**
     * Method can be used to register if the CNC Unit finished the current line and goes to the next line
     *
     * @param canalNr Current Number
     * @return a Observable int Value
     */
    public ObservableIntegerValue getCanalLinePositionAsObservables(int canalNr) {
        if (canalNr >= canals.size()) throw new ArrayIndexOutOfBoundsException("Canal does not exist");
        return canals.get(canalNr).programLinePositionProperty();
    }

    public CNCState getCncRunState() {
        return cncRunState;
    }


    private boolean areAllCanalsFinished() {
        boolean allCallsFinished = true;
        for (Future<Boolean> call : CNCCanalExecuteFuture) {
            if (!call.isDone()) {
                logger.log(Level.INFO, "CALL IS NOT FINISHED");
                allCallsFinished = false;
            }
        }
        return allCallsFinished;
    }

    public boolean isTheNCRunning() {
        boolean allCallsStillRunning = false;
        for (Canal canal : canals) {
            if (canal.isCanalRunning()) allCallsStillRunning = true;
        }
        return allCallsStillRunning;
    }

    public void waitUntilAllCanalsFinished() throws ExecutionException, InterruptedException {
        for (Future<Boolean> call : CNCCanalExecuteFuture) {
            call.get();
        }
    }

    public void runStoppedCNCControl() {
        canals.forEach(Canal::runBrakedCode);
    }

    public void breakCNCControl() {
        canals.forEach(Canal::breakRunningCode);
    }

    public void setAnimationView(List<CNCAnimation> cncAnimationView) {
        for (int i = 0; i < canals.size(); i++) {
            canals.get(i).addAnimationModel(cncAnimationView.get(i));
        }

    }

    public void resetAxis() {
        for (Canal canal : canals) {
            for (AxisName axis : canal.getCncAxes().keySet()) {
                canal.getCncAxes().get(axis).resetPosition();
            }
        }
    }

    public int countOfCanals() {
        return canals.size();
    }
}
