package ch.dcreations.cncsimulator.cncControl.GCodes.moveComands;

import ch.dcreations.cncsimulator.animation.AnimationModel;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.GCodes.FeedOptions;
import ch.dcreations.cncsimulator.cncControl.GCodes.GCode;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.Calculator;
import ch.dcreations.cncsimulator.config.ExeptionMessages;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableIntegerValue;

import java.util.Map;
import java.util.Optional;

public class GCodeMove extends GCode {
    protected Position axisPosition ;

    Map<AxisName,Double> parameter;
    Position endPosition;

    double distance = 0;
    protected Optional<AnimationModel> animationModelOptional  = Optional.empty() ;;

    public GCodeMove(long codeNumber, FeedOptions feedOptions, ObservableIntegerValue spindleSpeed, Position startPosition, SimpleDoubleProperty feed,Position axisPosition,Map<AxisName,Double> parameter) throws Exception {
        super(codeNumber, feedOptions, spindleSpeed, startPosition, feed);
        this.axisPosition = axisPosition;
        this.parameter = parameter;
        setupParameterForRun();
    }

    private void setupParameterForRun() throws Exception {
        setupParameterX();
        setupParameterY();
        setupParameterZ();
        setEndPosition();
    }

    private void setEndPosition() {
        endPosition = new Position(parameter.get(AxisName.X),parameter.get(AxisName.Y),parameter.get(AxisName.Z));
    }

    private void setupParameterX() throws Exception {
        if (!parameter.containsKey(AxisName.X)&&parameter.containsKey(AxisName.U)) throw new Exception(ExeptionMessages.ARGRUMENT_X_AND_U);
        if (!parameter.containsKey(AxisName.X)&&!parameter.containsKey(AxisName.U)){
            parameter.put(AxisName.X, startPosition.getX());
        }else if (parameter.containsKey(AxisName.U)){
            parameter.put(AxisName.X, startPosition.getX()+parameter.get(AxisName.U));
        }
    }

    private void setupParameterY() throws Exception {
        if (!parameter.containsKey(AxisName.Y)&&parameter.containsKey(AxisName.V)) throw new Exception(ExeptionMessages.ARGRUMENT_X_AND_U);
        if (!parameter.containsKey(AxisName.Y)&&!parameter.containsKey(AxisName.V)){
            parameter.put(AxisName.Y, startPosition.getY());
        }else if (parameter.containsKey(AxisName.V)){
            parameter.put(AxisName.Y, startPosition.getY()+parameter.get(AxisName.V));
        }
    }

    private void setupParameterZ() throws Exception {
        if (!parameter.containsKey(AxisName.Z)&&parameter.containsKey(AxisName.W)) throw new Exception(ExeptionMessages.ARGRUMENT_X_AND_U);
        if (!parameter.containsKey(AxisName.Z)&&!parameter.containsKey(AxisName.W)){
            parameter.put(AxisName.Z, startPosition.getZ());
        }else if (parameter.containsKey(AxisName.W)){
            parameter.put(AxisName.Z, startPosition.getZ()+parameter.get(AxisName.W));
        }
    }

    public Position getAxisPosition() {
        return axisPosition;
    }

    public void setAnimationModel(Optional<AnimationModel> animationModelOptional) {
        this.animationModelOptional = animationModelOptional;
    }
}
