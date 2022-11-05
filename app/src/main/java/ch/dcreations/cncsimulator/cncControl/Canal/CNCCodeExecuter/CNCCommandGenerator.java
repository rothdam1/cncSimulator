package ch.dcreations.cncsimulator.cncControl.Canal.CNCCodeExecuter;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.Plane;
import ch.dcreations.cncsimulator.cncControl.Canal.CanalDataModel;
import ch.dcreations.cncsimulator.cncControl.Exceptions.AxisOrSpindleDoesNotExistException;
import ch.dcreations.cncsimulator.cncControl.Exceptions.CodeDoesNotExistException;
import ch.dcreations.cncsimulator.cncControl.GCodes.FeedOptions;
import ch.dcreations.cncsimulator.cncControl.GCodes.GCode;
import ch.dcreations.cncsimulator.cncControl.GCodes.PlainGCode;
import ch.dcreations.cncsimulator.cncControl.GCodes.moveComands.G01;
import ch.dcreations.cncsimulator.cncControl.GCodes.moveComands.G02G03;
import ch.dcreations.cncsimulator.cncControl.PLC.MCodes;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.Config;
import javafx.beans.property.DoubleProperty;
import java.util.*;
import static ch.dcreations.cncsimulator.cncControl.GCodes.SpindelRotationOption.CONSTANT_ROTATION;
import static ch.dcreations.cncsimulator.cncControl.GCodes.SpindelRotationOption.CONSTANT_SURFACE_SPEED;

/**
 * <p>
 * <p>
 *  CNC Code Decoder decodes a NC - Code Line to a CNC Code Line Command
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */

public class CNCCommandGenerator {

    CanalDataModel canalDataModel;
    Position startPosition;

    public CNCCommandGenerator(CanalDataModel canalDataModel,Position startPosition) {
        this.canalDataModel = canalDataModel;
        this.startPosition = startPosition;
    }

    public CNCCommand splitCommands(String line) throws Exception {
        String[] codeWords = line.replaceAll(" ", "").replaceAll(";","").split("(?=[A-Z,%])");
        List<GCode> gCodes = new LinkedList<>();
        Map<AxisName, Double> axisDistance = new HashMap<>();
        try {
            return generateCNCCode(codeWords, gCodes, axisDistance,startPosition);
        } catch (Exception e) {
            throw new CodeDoesNotExistException("Generation Code was not success" + e.getMessage());
        }
    }

    private CNCCommand generateCNCCode(String[] codeWords, List<GCode> gCodes, Map<AxisName, Double> axisDistance, Position startPosition) throws Exception {
        MCodes mCode = null;
        Map<Character, Double> additionalParameterMap = new HashMap<>();
        for (String code : codeWords) {
            if (code.length() > 1) {
                Character codeCommand = checkAndGetCodeWord(code);
                switch (codeCommand) {
                    case '%','M','G' -> {}
                    case 'X', 'Y', 'Z', 'C', 'A', 'B' -> {if (AxisName.get(codeCommand).isPresent()) {
                            axisDistance.put(AxisName.get(codeCommand).get(), getCodeValue(code));
                        }}
                        default -> additionalParameterMap.put(codeCommand, getCodeValue(code));
                }
            }
        }
        for (String code : codeWords) {
            if (code.length() > 1) {
                Character codeCommand = checkAndGetCodeWord(code);
                switch (codeCommand) {
                    case 'G' -> gCodes.add(decodeGCode(Math.round(getCodeValue(code)), axisDistance,additionalParameterMap,startPosition));
                    case 'M' -> mCode = new MCodes(getCodeValue(code) + "");
                }
            }
        }
        return new CNCCommand(gCodes, mCode, axisDistance, additionalParameterMap,startPosition);
    }

    //Decodes the G value to the Correct G Code G01 -> G999
    private GCode decodeGCode(long codeNumber, Map<AxisName, Double> axisDistance,Map<Character, Double> additionalParameterMap,Position startPosition) throws Exception {
        GCode gCode = new PlainGCode(9999);
        Map<AxisName, Double> parameters = setupGCodeParameters(axisDistance);
        switch (Math.toIntExact(codeNumber)) {
            case 1 -> gCode = new G01(codeNumber, canalDataModel.getFeedOptions(),canalDataModel.getCurrentSelectedSpindle().getSpindelRotationOption(), canalDataModel.getCurrentSelectedSpindle().getSpindleSpeed(), startPosition, canalDataModel.currentFeedRateProperty().get(), parameters);
            case 2,3 -> gCode = new G02G03(codeNumber,canalDataModel.getFeedOptions(),canalDataModel.getCurrentSelectedSpindle().getSpindelRotationOption(),canalDataModel.getCurrentSelectedSpindle().getSpindleSpeed(), startPosition, canalDataModel.currentFeedRateProperty().get(), parameters,additionalParameterMap,canalDataModel.getPlane(),canalDataModel.getCalculationErrorMaxInCircle());
            case 97 -> canalDataModel.getCurrentSelectedSpindle().setSpindleRotationOption(CONSTANT_ROTATION);
            case 96 -> canalDataModel.getCurrentSelectedSpindle().setSpindleRotationOption(CONSTANT_SURFACE_SPEED, getCNCAxisProperty(AxisName.X));
            case 98 ->  canalDataModel.setFeedOptions(FeedOptions.FEED_PER_MINUITE);
            case 99 -> canalDataModel.setFeedOptions(FeedOptions.FEED_PER_REVOLUTION);
            case 17 -> canalDataModel.setPlane(Plane.G17);
            case 18 -> canalDataModel.setPlane(Plane.G18);
            case 19 -> canalDataModel.setPlane(Plane.G19);
            default -> throw new Exception("Code does not exist");
        }
        return gCode;
    }

    //@todo Better Code Check of the Value
    private Character checkAndGetCodeWord(String code) throws Exception {
        if (code.length() < 2 && !code.matches(Config.END_OF_PROGRAM_SYMBOLE))
            throw new IllegalArgumentException("CODE TO SHORT=" + code);
        if (!code.substring(0, 1).matches("[A-Za-z,%]")) throw new Exception("DOES NOT HAVE A COMMAND" + code);
        if (!code.matches(Config.END_OF_PROGRAM_SYMBOLE)) {
            if (!code.substring(1).matches("[-\\d.]*")) throw new Exception("VALUE IS NOT RIGHT" + code);
        }
        return code.charAt(0);
    }



    private DoubleProperty getCNCAxisProperty(AxisName axisName) throws AxisOrSpindleDoesNotExistException {
        if (canalDataModel.getCncAxes().containsKey(axisName)) {
            return canalDataModel.getCncAxes().get(axisName).axisPositionProperty();
        } else {
            throw new AxisOrSpindleDoesNotExistException("AXIS DOES NOT EXIST");
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
