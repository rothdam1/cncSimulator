package ch.dcreations.cncsimulator.cncControl;

import ch.dcreations.cncsimulator.cncControl.GCodes.*;
import ch.dcreations.cncsimulator.cncControl.PLC.MCodes;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ch.dcreations.cncsimulator.cncControl.GCodes.SpindelRotationOption.CONSTANT_ROTATION;
import static ch.dcreations.cncsimulator.cncControl.GCodes.SpindelRotationOption.CONSTANT_SURFACE_SPEED;

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
public class Canal implements Callable {
    private final List<CNCAxis> cncAxes ;

    private final AtomicBoolean canalRunState = new AtomicBoolean(false);
    private CanalState canalState = CanalState.STOP;
    private final SimpleIntegerProperty programLinePosition = new SimpleIntegerProperty(0);
    private int countOfProgramLines = 0;
    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());
    private String cncProgramText;

    private FeedOptions feedOptions = FeedOptions.FEED_PER_REVOLUTION;
    private SpindelRotationOption spindelRotationOption = CONSTANT_ROTATION;

    private SimpleIntegerProperty currentSpindleSpeed = new SimpleIntegerProperty();

    double currentFeed = 0.0;

    public Canal(List<CNCAxis> cncAxes) {
        super();
        this.cncAxes = cncAxes;
    }

    @Override
    public Boolean call(){
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
        return true;
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
        canalRunState.set(true);
        setSpindleSpeed(cncProgramCommand.additionParameters);
        setFeedRate(cncProgramCommand.additionParameters);
        for (GCode gCode : cncProgramCommand.gCodes){
            try {
                gCode.execute(canalRunState);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        canalRunState.set(false);
    }

    private void setSpindleSpeed(Map<Character,Double> additionParameters) {
        if (additionParameters.containsKey('S')) currentSpindleSpeed.set((int)Math.round(additionParameters.get('S'))) ;
    }
    private void setFeedRate(Map<Character,Double> additionParameters) {
        if (additionParameters.containsKey('F')) currentFeed = (additionParameters.get('F')) ;
    }

    private CNCProgramCommand splitCommands(String line) throws Exception{
        String[] codeWords = line.replace(" ","").split("(?=[A-Z])");
        List<GCode> gCodes  = new ArrayList<>();
        Map<AxisName,Double> axisDistance = new HashMap<>();
        return generateCNCCode(codeWords, gCodes, axisDistance);
    }

    private CNCProgramCommand generateCNCCode(String[] codeWords, List<GCode> gCodes,  Map<AxisName, Double> axisDistance) throws Exception {
        MCodes mCode = null;
        Map<Character,Double> additionalParameterMap = new HashMap<>();
        for (String code : codeWords){
            if (code.length()>0) {
                Character codeCommand = checkAndGetCodeWord(code);
                double value = getCodeValue(code);
                switch (codeCommand) {
                    case 'X', 'Y', 'Z', 'C', 'A', 'B' -> axisDistance.put(AxisName.get(codeCommand).get(), value);
                    default -> additionalParameterMap.put(codeCommand,value);
                }
            }
        }
        for (String code : codeWords){
            if (code.length()>0) {
                Character codeCommand = checkAndGetCodeWord(code);
                double value = getCodeValue(code);
                switch (codeCommand) {
                    case 'G' -> gCodes.add(decodeGCode(Math.round(value),axisDistance));
                    case 'M' -> mCode = new MCodes(value + "");
                }
            }
        }
        return new CNCProgramCommand(gCodes, mCode, axisDistance, additionalParameterMap);
    }

    //Decodes the G value to the Correct G Code G01 -> G999
    private GCode decodeGCode(long codeNumber,Map<AxisName, Double> axisDistance) throws Exception {
        GCode gCode = new PlainGCode(9999,feedOptions,currentSpindleSpeed);
        Map<AxisName,Double> parameters = setupGCodeParameters(axisDistance);
        Position startPosition = new Position(cncAxes.get(0).getAxisPosition(),cncAxes.get(1).getAxisPosition(),cncAxes.get(2).axisPosition,0,0,0);
        switch (Math.toIntExact(codeNumber)){
            case 1 -> gCode = new G01(codeNumber,feedOptions,currentSpindleSpeed,startPosition,currentFeed,parameters);
            case 97 -> spindelRotationOption = CONSTANT_ROTATION;
            case 96 -> spindelRotationOption = CONSTANT_SURFACE_SPEED;
            case 98 -> feedOptions = FeedOptions.FEED_PER_MINUITE;
            case 99 -> feedOptions = FeedOptions.FEED_PER_REVOLUTION;
            default -> throw new Exception("Code does not exist");
        }
        return gCode;
    }

    private Map<AxisName, Double> setupGCodeParameters(Map<AxisName, Double> axisDistance) {
        Map<AxisName,Double> parameters = new HashMap<>();
        for ( AxisName cncAxis: axisDistance.keySet()){
            parameters.put(cncAxis,axisDistance.get(cncAxis));
        }
        return parameters;
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
