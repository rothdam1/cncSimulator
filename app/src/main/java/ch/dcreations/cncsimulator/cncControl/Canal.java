package ch.dcreations.cncsimulator.cncControl;

import ch.dcreations.cncsimulator.cncControl.GCodes.GCode;
import ch.dcreations.cncsimulator.cncControl.PLC.MCodes;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * <p>
 * <p>
 *  is a Canal of a CNC Control A Canal contains Axis And Spindles
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */
public class Canal extends Thread {
    private final List<CNCAxis> cncAxes ;

    private final AtomicBoolean canalRunState = new AtomicBoolean(false);
    private CanalState canalState = CanalState.STOP;
    private final SimpleIntegerProperty programLinePosition = new SimpleIntegerProperty(0);
    private int countOfProgramLines = 0;
    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());
    private String cncProgramText;

    public Canal(List<CNCAxis> cncAxes) {
        this.cncAxes = cncAxes;
    }

    @Override
    public void run(){
        try {
            if (cncProgramText != null) {
                String[] lines = cncProgramText.replace("\n", "").split(";");
                countOfProgramLines = lines.length;
                switch (canalState) {
                    case SINGLE_STEP -> runNextLine(lines[programLinePosition.get()]);
                    case RUN -> runAllLines(lines);
                }
            }
        }catch (Exception e){
            logger.log(Level.WARNING,"NC RUN Exception"+e);
        }

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



    private void runAllLines(String[] lines) throws Exception {
        programLinePosition.set(0);
        while (programLinePosition.get() <= countOfProgramLines-2){
            runNextLine(lines[programLinePosition.get()]);
        }
    }

    private void runNextLine(String line) throws Exception{
        CNCProgramCommand cncProgramCommand = splitCommands(line);
        executeNCCommand(cncProgramCommand);
        goToNextLine();
    }

    //@todo  execution of cnc Code
    private void executeNCCommand(CNCProgramCommand cncProgramCommand) {

    }

    private CNCProgramCommand splitCommands(String line) throws Exception{
        String[] codeWords = line.replace(" ","").split("(?=[A-Z])");
        List<GCode> gCodes  = new ArrayList<>();
        Map<AxisName,Double> axisDistance = new HashMap<>();
        List<String> additionParameters = new ArrayList<>();
        return generateCNCCode(codeWords, gCodes, axisDistance, additionParameters);
    }

    private CNCProgramCommand generateCNCCode(String[] codeWords, List<GCode> gCodes,  Map<AxisName, Double> axisDistance, List<String> additionParameters) throws Exception {
        MCodes mCode = null;
        for (String code : codeWords){
            if (code.length()>0) {
                Character codeCommand = checkAndGetCodeWord(code);
                double value = getCodeValue(code);
                switch (codeCommand) {
                    case 'G' -> gCodes.add(new GCode(Math.round(value)));
                    case 'm' -> mCode = new MCodes(value + "");
                    case 'X', 'Y', 'Z', 'C', 'A', 'B' -> axisDistance.put(AxisName.get(codeCommand).get(), value);
                    default -> additionParameters.add(code);
                }
            }
        }
        return new CNCProgramCommand(gCodes, mCode, axisDistance, additionParameters);
    }

    private double getCodeValue(String code) {
        return Double.parseDouble(code.substring(1));
    }

    //@todo Better Code Check of the Value
    private Character checkAndGetCodeWord(String code) throws Exception {
        if (code.length()<2)throw new IllegalArgumentException("CODE TO SHORT="+code);
        if (!code.substring(0,1).matches("[A-Za-z,]"))throw new Exception("DOES NOT HAVE A COMMAND"+code);
        if (!code.substring(1).matches("[0-9.]*"))throw new Exception("VALUE IS NOT RIGHT"+code);
        return code.charAt(0);
    }

    private void goToNextLine() {
        if (programLinePosition.get()>= countOfProgramLines-1){
            programLinePosition.set(0);
        }else {
            programLinePosition.set(programLinePosition.get()+1);
        }
    }

    public void setCanalState(CanalState canalState) {
        this.canalState = canalState;
    }

    public void stopRunning() {
        programLinePosition.set(0);
        canalRunState.set(false);
    }

    public AtomicBoolean getCanalRunState() {
        return canalRunState;
    }

    public CanalState getCanalState() {
        return canalState;
    }

    /**
     * is used to register if the current Program Line in while the Program runs have changed
     * @return linenumber
     */
    public ObservableIntegerValue programLinePositionProperty() {
        return programLinePosition;
    }

}
