package ch.dcreations.cncsimulator.cncControl.Canal.CNCCodeExecuter;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CanalDataModel;
import ch.dcreations.cncsimulator.cncControl.GCodes.GCode;
import ch.dcreations.cncsimulator.cncControl.GCodes.moveComands.GCodeMove;
import ch.dcreations.cncsimulator.cncControl.PLC.MCodes;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.Config;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * <p>
 *  IS a Single Line Command of the CNC ISO CODE Like G1 X3.0 Z3.0
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */


public class CNCCommand {
    private final List<GCode> gCodes;
    private final MCodes mCode;
    private final Map<AxisName,Double> axisDistance;
    private final Map<Character,Double> additionParameters;

    private final Position startPosition;

    private final Position endposition;

    public CNCCommand(List<GCode> gCodes, MCodes mCode, Map<AxisName, Double> axisDistance, Map<Character,Double> additionParameters,Position startPosition) {
        this.gCodes = gCodes;
        this.mCode = mCode;
        this.axisDistance = axisDistance;
        this.additionParameters = additionParameters;
        this.startPosition = startPosition;
        this.endposition = getEndPosition();
    }

    private Position getEndPosition() {
        double endX = (axisDistance.containsKey(AxisName.X)) ? axisDistance.get(AxisName.X) : startPosition.getX();
        double endY = (axisDistance.containsKey(AxisName.Y)) ? axisDistance.get(AxisName.Y) : startPosition.getY();
        double endZ = (axisDistance.containsKey(AxisName.Z)) ? axisDistance.get(AxisName.Z) : startPosition.getZ();
        double endA = (axisDistance.containsKey(AxisName.A)) ? axisDistance.get(AxisName.A) : startPosition.getA();
        double endB = (axisDistance.containsKey(AxisName.B)) ? axisDistance.get(AxisName.B) : startPosition.getB();
        double endC = (axisDistance.containsKey(AxisName.C)) ? axisDistance.get(AxisName.C) : startPosition.getC();
        return new Position(endX,endY, endZ,endA,endB,endC);
    }

    public List<GCode> getGCodes() {
        return gCodes;
    }

    public MCodes getCode() {
        return mCode;
    }

    public Map<AxisName, Double> getAxisDistance() {
        return axisDistance;
    }

    public Map<Character, Double> getAdditionParameters() {
        return additionParameters;
    }


    public Collection<? extends Map<AxisName, Double>> getPath(CanalDataModel canalDataModel) {
        setFeedRate(additionParameters,canalDataModel);
        setSpindleSpeed(additionParameters,canalDataModel);
        List<Map<AxisName, Double>> positionList = new LinkedList<>();
        for (GCode gCode : getGCodes()) {
            if (GCodeMove.class.isAssignableFrom(gCode.getClass())) {
                positionList.addAll(((GCodeMove) gCode).getPath(Config.POSITION_CALCULATION_RESOLUTION));
            }
        }
        return positionList;
    }

    private void setSpindleSpeed(Map<Character, Double> additionParameters, CanalDataModel canalDataModel) {
        if (additionParameters.containsKey('S'))
            canalDataModel.setCurrentSpindleSpeed((int) Math.round(additionParameters.get('S')));
    }

    private void setFeedRate(Map<Character, Double> additionParameters, CanalDataModel canalDataModel) {
        if (additionParameters.containsKey('F')) canalDataModel.setCurrentFeedRate(additionParameters.get('F'));
    }

    public Position getEndposition() {
        return endposition;
    }
}

