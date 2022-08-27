package ch.dcreations.cncsimulator.cncControl;

import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.value.ObservableIntegerValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    ExecutorService CNCCanalExecutorService;

    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());
    private final List<Canal> canals;

    public CNCControl(List<Canal> canals) {
        this.canals = canals;
        CNCCanalExecutorService = canals.size()<1 ?  Executors.newFixedThreadPool(1) : Executors.newFixedThreadPool(canals.size());
    }

    public List<Map<AxisName,CNCAxis>> getCncAxes() {
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
        List<Callable<Canal>> callables = new ArrayList<>();
        for (Canal canal : canals) {
            callables.add(canal);
        }
        try {
            CNCCanalExecutorService.invokeAll(callables);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
}
