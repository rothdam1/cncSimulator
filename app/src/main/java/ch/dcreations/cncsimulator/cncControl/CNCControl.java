package ch.dcreations.cncsimulator.cncControl;

import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.value.ObservableIntegerValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

    ExecutorService CNCCanalExecutorService;

    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());
    private final List<Canal> canals;

    public CNCControl(List<Canal> canals) {
        this.canals = canals;
        CNCCanalExecutorService = canals.size()<1 ?  Executors.newFixedThreadPool(1) : Executors.newFixedThreadPool(canals.size());
    }

    public List<CNCAxis> getCncAxes() {
        List<CNCAxis> cncAxes = new ArrayList<>();
        for (Canal canal : canals) {
            cncAxes.addAll(canal.getCncAxes());
        }
    return Collections.unmodifiableList(cncAxes);
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
            if (canal.isAlive())logger.log(Level.WARNING,"SHOT DOWN ");
        }
        if (CNCCanalExecutorService.isShutdown())logger.log(Level.WARNING,"SHOT DOWN ");
        this.cncRunState = CNCState.STOP;
    }

    public void runCNCProgram(){
        if (CNCCanalExecutorService.isShutdown()){
            CNCCanalExecutorService = Executors.newFixedThreadPool(canals.size());
        }
        for (Canal canal : canals) {
            canal.setCanalState(CanalState.RUN);
        }
        this.cncRunState = CNCState.RUN;
        runCanals();
    }

    private void runCanals() {
        for (Canal canal : canals) {
            CNCCanalExecutorService.execute(canal);
        }
    }

    public void goToNextStepCNCProgram(){
        this.cncRunState = CNCState.SINGLE_STEP;
        for (Canal canal : canals) {
            canal.setCanalState(CanalState.SINGLE_STEP);
        }
        runCanals();
    }



    public void terminateCNCControl() throws InterruptedException {
        CNCCanalExecutorService.shutdown();
        CNCCanalExecutorService.awaitTermination(10,TimeUnit.SECONDS);
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
}
