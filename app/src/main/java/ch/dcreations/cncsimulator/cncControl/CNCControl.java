package ch.dcreations.cncsimulator.cncControl;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.CNCAxis;
import ch.dcreations.cncsimulator.cncControl.Canal.Canal;
import ch.dcreations.cncsimulator.cncControl.Canal.CanalState;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * <p>
 *  The CNC control contains axis and Program
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */
public class CNCControl {

    private  CNCState cncRunState = CNCState.STOP;

    private ExecutorService CNCCanalExecutorService;
    private List<Future<Boolean>> CNCCanalExecuterFuture = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());
    private final List<Canal> canals;

    public CNCControl(List<Canal> canals) {
        this.canals = canals;
        CNCCanalExecutorService = canals.size()<1 ?  Executors.newFixedThreadPool(1) : Executors.newFixedThreadPool(canals.size());
    }

    public List<Map<AxisName, CNCAxis>> getCncAxes() {
        List<Map<AxisName,CNCAxis>> listOfAllAxis = new ArrayList<>();
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
        if (canals.size()< canalNumber){
            throw new IOException("Canal does not Exist");
        }
        else {
            canals.get(canalNumber-1).setProgram(programText);
        }
    }

    public String getCanal1CNCProgramText() throws IOException {
        return getCNCProgramTextFromCanal(1);
    }

    public String getCanal2CNCProgramText() throws IOException {
        return getCNCProgramTextFromCanal(2);
    }
    private String getCNCProgramTextFromCanal(int canalNumber) throws IOException {
        if (canals.size()< canalNumber){
            throw new IOException("Canal does not Exist");
        }
        else {
            return canals.get(canalNumber-1).getProgramText();
        }
    }

    public void stopCNCProgram() throws InterruptedException {
        for (Canal canal : canals) {
            canal.stopRunning();
        }
        if (CNCCanalExecutorService.isShutdown())logger.log(Level.WARNING,"SHOT DOWN ");
        this.cncRunState = CNCState.STOP;
    }

    public void runCNCProgram() throws Exception {
        if (CNCCanalExecutorService.isShutdown()){
            CNCCanalExecutorService = Executors.newFixedThreadPool(canals.size());
        }
        for (Canal canal : canals) {
            canal.setCanalState(CanalState.RUN);
        }
        this.cncRunState = CNCState.RUN;
        runCanals();
    }

    private void runCanals() throws Exception {
        List<Callable<Canal>> callables = new ArrayList<>();
        if (areAllCallesFinished()) {
            for (Canal canal : canals) {
                callables.add(canal);
                CNCCanalExecuterFuture.add(CNCCanalExecutorService.submit(canal));
            }
        }else {
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
        for (Canal canal : canals){
            try {
                canal.stop();
            }catch (Exception e){
                logger.log(Level.WARNING,"CANAL DOES NOT TERMINATE RIGHT");
                canal.stopNow();
            }
        }
        CNCCanalExecutorService.shutdown();
        CNCCanalExecutorService.awaitTermination(5,TimeUnit.SECONDS);
        CNCCanalExecutorService.shutdownNow();
    }

    /**
     * Method can be used to register if the CNC Unit finished the current line and goes to the next line
     * @param canalNr Current Number
     * @return a Observable int Value
     */
    public ObservableIntegerValue getCanalLinePositionAsObservables(int canalNr){
        if (canalNr>=canals.size())throw new ArrayIndexOutOfBoundsException("Canal does not exist");
        return canals.get(canalNr).programLinePositionProperty();
    }

    public CNCState getCncRunState() {
        return cncRunState;
    }


    private boolean areAllCallesFinished() {
        boolean allCallesFinished = true;
        for (Future<Boolean> call :  CNCCanalExecuterFuture){
            if (call.isDone()) {
            }else {
                logger.log(Level.INFO,"CALL IS NOT FINISHED");
                allCallesFinished = false;
            }
        }
        return allCallesFinished;
    }
}
