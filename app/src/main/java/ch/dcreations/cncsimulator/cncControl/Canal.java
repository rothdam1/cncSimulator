package ch.dcreations.cncsimulator.cncControl;

import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Canal extends Thread {
    private final List<CNCAxis> cncAxes ;
    private CanalState canalState = CanalState.STOP;
    private SimpleIntegerProperty programLinePosition = new SimpleIntegerProperty(0);
    private int countOfProgramLines = 0;
    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());
    private String cncProgramText;

    public Canal(List<CNCAxis> cncAxes) {
        this.cncAxes = cncAxes;
    }

    public List<CNCAxis> getCncAxes() {
        return Collections.unmodifiableList(cncAxes);
    }

    public void setProgram(String programText) {
        cncProgramText = programText;
    }

    public String getProgramText() {
        return this.cncProgramText;
    }

    @Override
    public void run() {
        logger.log(Level.INFO,"CANAL "+ Thread.currentThread().getName() + "runs");
        if (cncProgramText != null) {
            String[] lines = cncProgramText.replace("\n","").split(";");
            countOfProgramLines = lines.length;
            switch (canalState) {
                case SINGLE_STEP -> runNextLine(lines[programLinePosition.get()]);
                case RUN -> runAllLines(lines);
            }
        }

    }

    private void runAllLines(String[] lines) {
        programLinePosition.set(0);
        while (programLinePosition.get() <= countOfProgramLines-2){
            runNextLine(lines[programLinePosition.get()]);
        }
        //run last Line
        runNextLine(lines[programLinePosition.get()]);
    }

    private void runNextLine(String line) {
        logger.log(Level.INFO,"Execute Line = "+ line);
        goToNextLine();
    }

    private void goToNextLine() {
        if (programLinePosition.get()>= countOfProgramLines-1){
            programLinePosition.get();
        }else {
            programLinePosition.set(programLinePosition.get()+1);
        }
    }

    public void setCanalState(CanalState canalState) {
        this.canalState = canalState;
    }

    public void stopRunning() {
    }


    public ObservableIntegerValue programLinePositionProperty() {
        return programLinePosition;
    }
}
