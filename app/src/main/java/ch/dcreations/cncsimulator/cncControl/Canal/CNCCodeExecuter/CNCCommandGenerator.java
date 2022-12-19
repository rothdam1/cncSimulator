package ch.dcreations.cncsimulator.cncControl.Canal.CNCCodeExecuter;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.Plane;
import ch.dcreations.cncsimulator.cncControl.Canal.CanalDataModel;
import ch.dcreations.cncsimulator.cncControl.Exceptions.AxisOrSpindleDoesNotExistException;
import ch.dcreations.cncsimulator.cncControl.Exceptions.CodeDoesNotExistException;
import ch.dcreations.cncsimulator.cncControl.GCodes.*;
import ch.dcreations.cncsimulator.cncControl.GCodes.moveComands.G01;
import ch.dcreations.cncsimulator.cncControl.GCodes.moveComands.G02G03;
import ch.dcreations.cncsimulator.cncControl.GCodes.moveComands.GCodeMove;
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
        String[] codeWords = line.replaceAll(" ", "").replaceAll(";","")
                .replaceAll("\n","").split("(?=[A-Z,%])");
        List<GCode> gCodes = new LinkedList<>();
        Map<AxisName, Double> axisDistance = new HashMap<>();
        try {
            return generateCNCCode(Arrays.stream(codeWords).toList(), gCodes, axisDistance,startPosition);
        } catch (Exception e) {
            throw new CodeDoesNotExistException("Generation Code was not success"+ line+" message" + e.getMessage());
        }
    }

    private CNCCommand generateCNCCode(List<String> codeWords, List<GCode> gCodes, Map<AxisName, Double> axisDistance, Position startPosition) throws Exception {
        MCodes mCode = null;
        Boolean GOTO = false;
        CCodeMap cCodeMap = new CCodeMap();
        Map<Character, Double> additionalParameterMap = new HashMap<>();
        List<GCodeCatogories> catogoriesOFCode = new LinkedList<>();
        // End operator
        if (codeWords.contains("%"))GOTO = true;
        for (String code : codeWords) {
            if (code.length() > 1) {
                Character codeCommand = checkAndGetCodeWord(code);
                switch (codeCommand) {
                        case 'M','G' -> {}
                    case 'X', 'Y', 'Z', 'C', 'A', 'B' -> {if (AxisName.get(codeCommand).isPresent()) {
                            axisDistance.put(AxisName.get(codeCommand).get(), getCodeValue(code));
                        }}
                    default -> additionalParameterMap.put(codeCommand, getCodeValue(code));
                }
            }
        }
        // IF CODE CONTAIONS INTERPOLATION
        // ADD INCRAMENTAL AND DOUBLE WHEN DIAMETER
        if(catogoriesOFCode.contains(GCodeCatogories.INTERPOLATION)){
            for (Character character :additionalParameterMap.keySet()){
                double codeValue = additionalParameterMap.get(character);
                switch (character){
                    case 'U' -> axisDistance.put(AxisName.X, startPosition.getX()+codeValue);
                    case 'V' -> axisDistance.put(AxisName.V, startPosition.getX()+codeValue);
                    case 'W' -> axisDistance.put(AxisName.Z, startPosition.getX()+codeValue);
                    case 'H' -> axisDistance.put(AxisName.C, startPosition.getX()+codeValue);
                }
            }
            for(AxisName axisName : axisDistance.keySet()){
                double multi = canalDataModel.getCncAxes().get(axisName).getMutiplicator();
                axisDistance.replace(axisName,axisDistance.get(axisName)/multi);
            }
        }
        for (String code : codeWords) {
            if (code.length() > 1) {
                Character codeCommand = checkAndGetCodeWord(code);
                switch (codeCommand) {
                    case 'O' -> GOTO = true;
                    case 'G' ->  decodeGCode(Math.round(getCodeValue(code)), axisDistance,additionalParameterMap,startPosition);
                    case 'M' -> mCode = new MCodes(getCodeValue(code) + "");
                }
            }
        }
        Map<AxisName, Double> parameters = setupGCodeParameters(axisDistance);
        // interpolation
        GCode gCode = null;
        if (GOTO == false) {
            switch (canalDataModel.getInterpolationFunction()) {
                case G1 -> gCode = new G01(1, canalDataModel.getFeedOptions(), canalDataModel.getCurrentSelectedSpindle().getSpindelRotationOption(), canalDataModel.getCurrentSelectedSpindle().getSpindleSpeed(), startPosition, canalDataModel.currentFeedRateProperty().get(), parameters);
                case G2 -> gCode = new G02G03(2, canalDataModel.getFeedOptions(), canalDataModel.getCurrentSelectedSpindle().getSpindelRotationOption(), canalDataModel.getCurrentSelectedSpindle().getSpindleSpeed(), startPosition, canalDataModel.currentFeedRateProperty().get(), parameters, additionalParameterMap, canalDataModel.getPlane(), canalDataModel.getCalculationErrorMaxInCircle());
                case G3 -> gCode = new G02G03(3, canalDataModel.getFeedOptions(), canalDataModel.getCurrentSelectedSpindle().getSpindelRotationOption(), canalDataModel.getCurrentSelectedSpindle().getSpindleSpeed(), startPosition, canalDataModel.currentFeedRateProperty().get(), parameters, additionalParameterMap, canalDataModel.getPlane(), canalDataModel.getCalculationErrorMaxInCircle());
            }
        }
        if (gCode != null)gCodes.add(gCode);
        return new CNCCommand(gCodes, mCode, axisDistance, additionalParameterMap,startPosition);
    }

    private void setGCodeFunktion(double codeValue) {
        if (codeValue == 0.0) {
            canalDataModel.setInterpolationFunction(InterpolationFunction.G0);
        } else if (codeValue == 1.0) {
            canalDataModel.setInterpolationFunction(InterpolationFunction.G1);
        } else if (codeValue == 2.0) {
            canalDataModel.setInterpolationFunction(InterpolationFunction.G2);
        } else if (codeValue == 3.0) {
            canalDataModel.setInterpolationFunction(InterpolationFunction.G3);
        }
    }

    //Decodes the G value to the Correct G Code G01 -> G999
    private void decodeGCode(long codeNumber, Map<AxisName, Double> axisDistance,Map<Character, Double> additionalParameterMap,Position startPosition) throws Exception {
        switch (Math.toIntExact(codeNumber)) {
            case 1 -> setGCodeFunktion(1);
            case 2 -> setGCodeFunktion(2);
            case 3 -> setGCodeFunktion(3);
            case 97 -> canalDataModel.getCurrentSelectedSpindle().setSpindleRotationOption(CONSTANT_ROTATION);
            case 96 -> canalDataModel.getCurrentSelectedSpindle().setSpindleRotationOption(CONSTANT_SURFACE_SPEED, getCNCAxisProperty(AxisName.X));
            case 98 ->  canalDataModel.setFeedOptions(FeedOptions.FEED_PER_MINUTE);
            case 99 -> canalDataModel.setFeedOptions(FeedOptions.FEED_PER_REVOLUTION);
            case 17 -> canalDataModel.setPlane(Plane.G17);
            case 18 -> canalDataModel.setPlane(Plane.G18);
            case 19 -> canalDataModel.setPlane(Plane.G19);
            default -> throw new Exception("Code does not exist");
        }
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
