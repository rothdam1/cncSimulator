package ch.dcreations.cncsimulator.cncControl.Canal.CNCCodeExecuter;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CanalDataModel;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCProgramCommand;
import ch.dcreations.cncsimulator.cncControl.Exceptions.AxisOrSpindleDoesNotExistExeption;
import ch.dcreations.cncsimulator.cncControl.GCodes.FeedOptions;
import ch.dcreations.cncsimulator.cncControl.GCodes.GCode;
import ch.dcreations.cncsimulator.cncControl.GCodes.PlainGCode;
import ch.dcreations.cncsimulator.cncControl.GCodes.moveComands.G01;
import ch.dcreations.cncsimulator.cncControl.PLC.MCodes;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.Config;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.property.DoubleProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static ch.dcreations.cncsimulator.cncControl.GCodes.SpindelRotationOption.CONSTANT_ROTATION;
import static ch.dcreations.cncsimulator.cncControl.GCodes.SpindelRotationOption.CONSTANT_SURFACE_SPEED;

public class CNCCodeDecoder {

    CanalDataModel canalDataModel;

    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());
    public CNCCodeDecoder(CanalDataModel canalDataModel) {
        this.canalDataModel = canalDataModel;
    }

    public CNCProgramCommand splitCommands(String line) throws Exception {
        String[] codeWords = line.replace(" ", "").split("(?=[A-Z,%])");
        List<GCode> gCodes = new ArrayList<>();
        Map<AxisName, Double> axisDistance = new HashMap<>();
        try {

            return generateCNCCode(codeWords, gCodes, axisDistance);
        } catch (Exception e) {
            throw new Exception("GENARATION CODE WAS NOT SUCZESS");
        }
    }

    private CNCProgramCommand generateCNCCode(String[] codeWords, List<GCode> gCodes, Map<AxisName, Double> axisDistance) throws Exception {
        MCodes mCode = null;
        Map<Character, Double> additionalParameterMap = new HashMap<>();
        for (String code : codeWords) {
            if (code.length() > 0) {
                Character codeCommand = checkAndGetCodeWord(code);
                switch (codeCommand) {
                    case '%' -> {
                    }
                    case 'X', 'Y', 'Z', 'C', 'A', 'B' ->
                            axisDistance.put(AxisName.get(codeCommand).get(), getCodeValue(code));
                    default -> additionalParameterMap.put(codeCommand, getCodeValue(code));
                }
            }
        }
        for (String code : codeWords) {
            if (code.length() > 0) {
                Character codeCommand = checkAndGetCodeWord(code);
                switch (codeCommand) {
                    case 'G' -> gCodes.add(decodeGCode(Math.round(getCodeValue(code)), axisDistance));
                    case 'M' -> mCode = new MCodes(getCodeValue(code) + "");
                }
            }
        }
        return new CNCProgramCommand(gCodes, mCode, axisDistance, additionalParameterMap);
    }

    //Decodes the G value to the Correct G Code G01 -> G999
    private GCode decodeGCode(long codeNumber, Map<AxisName, Double> axisDistance) throws Exception {
        GCode gCode = new PlainGCode(9999, canalDataModel.getFeedOptions(), canalDataModel.getCurrentSelectedSpindle().currentSpindleSpeedProperty(),canalDataModel.currentFeedRateProperty());
        Map<AxisName, Double> parameters = setupGCodeParameters(axisDistance);
        Position startPosition = new Position(canalDataModel.getCncAxes().get(AxisName.X).getAxisPosition(), canalDataModel.getCncAxes().get(AxisName.Y).getAxisPosition(), canalDataModel.getCncAxes().get(AxisName.Z).getAxisPosition(), 0, 0, 0);
        switch (Math.toIntExact(codeNumber)) {
            case 1 ->
                    gCode = new G01(codeNumber, canalDataModel.getFeedOptions(), canalDataModel.getCurrentSelectedSpindle().currentSpindleSpeedProperty(), startPosition, canalDataModel.currentFeedRateProperty(), parameters);
            case 97 -> canalDataModel.getCurrentSelectedSpindle().setSpindleRotationOption(CONSTANT_ROTATION);
            case 96 -> canalDataModel.getCurrentSelectedSpindle().setSpindleRotationOption(CONSTANT_SURFACE_SPEED, getCNCAxisProperty(AxisName.X));
            case 98 ->  canalDataModel.setFeedOptions(FeedOptions.FEED_PER_MINUITE);
            case 99 -> canalDataModel.setFeedOptions(FeedOptions.FEED_PER_REVOLUTION);
            default -> throw new Exception("Code does not exist");
        }
        return gCode;
    }

    //@todo Better Code Check of the Value
    private Character checkAndGetCodeWord(String code) throws Exception {
        if (code.length() < 2 && !code.matches(Config.END_OF_PROGRAM_SIMBOLE))
            throw new IllegalArgumentException("CODE TO SHORT=" + code);
        if (!code.substring(0, 1).matches("[A-Za-z,%]")) throw new Exception("DOES NOT HAVE A COMMAND" + code);
        if (!code.matches(Config.END_OF_PROGRAM_SIMBOLE)) {
            if (!code.substring(1).matches("[0-9.]*")) throw new Exception("VALUE IS NOT RIGHT" + code);
        }
        return code.charAt(0);
    }



    private DoubleProperty getCNCAxisProperty(AxisName axisName) throws AxisOrSpindleDoesNotExistExeption {
        if (canalDataModel.getCncAxes().containsKey(axisName)) {
            return canalDataModel.getCncAxes().get(axisName).axisPositionProperty();
        } else {
            throw new AxisOrSpindleDoesNotExistExeption("AXIS DOES NOT EXIST");
        }
    }

    private Map<AxisName, Double> setupGCodeParameters(Map<AxisName, Double> axisDistance) {
        Map<AxisName, Double> parameters = new HashMap<>();
        for (AxisName cncAxis : axisDistance.keySet()) {
            parameters.put(cncAxis, axisDistance.get(cncAxis));
        }
        return parameters;
    }

    private double getCodeValue(String code) {
        return Double.parseDouble(code.substring(1));
    }

}
