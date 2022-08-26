package ch.dcreations.cncsimulator.cncControl.GCodes.moveComands;

import ch.dcreations.cncsimulator.cncControl.AxisName;
import ch.dcreations.cncsimulator.cncControl.GCodes.FeedOptions;
import ch.dcreations.cncsimulator.cncControl.GCodes.GCode;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.Calculator;
import ch.dcreations.cncsimulator.config.ExeptionMessages;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableIntegerValue;

import java.awt.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class G01 extends GCodeMove {

    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());

    Map<AxisName,Double> parameter;
    Position endPosition;;

    double distance = 0;

    public G01(long codeNumber, FeedOptions feedOptions, ObservableIntegerValue spindleSpeed, Position startPosition, SimpleDoubleProperty feed, Map<AxisName,Double> parameter) throws Exception {
        super(codeNumber, feedOptions, spindleSpeed, startPosition,feed,new Position(startPosition.getX(), startPosition.getY(), startPosition.getZ()));
        this.parameter = parameter;
        setupParameterForRun();
    }

    private void setupParameterForRun() throws Exception {
        setupParameterX();
        setupParameterY();
        setupParameterZ();
        setEndPosition();
        distance = Calculator.vectorDistance(endPosition.getX()-startPosition.getX(),endPosition.getY()-startPosition.getY(),endPosition.getZ()-startPosition.getZ());

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

    @Override
    protected void calculatePosition(int timesRuns, int positionCalculationResolution) throws Exception {
        if(feed.get() == 0) throw new Exception("Feed rate us 0");
        if(distance == 0){
           finished.set(true);
       }else {
           double timeMS = (feedOptions == FeedOptions.FEED_PER_REVOLUTION) ?
                   (60*((distance / feed.get()) / spindleSpeed.get()) * 1000) : ((60*(distance / feed.get())) * 1000);
           double countOfCalculations = Math.ceil(timeMS / positionCalculationResolution);
           if (countOfCalculations < timesRuns) {
               finished.set(true);
           }else {
               axisPosition.setX(startPosition.getX()+(((endPosition.getX() - startPosition.getX()) / countOfCalculations) * timesRuns));
               axisPosition.setY(startPosition.getY()+ (((endPosition.getY() - startPosition.getY()) / countOfCalculations) * timesRuns));
               axisPosition.setZ(startPosition.getZ()+(((endPosition.getZ() - startPosition.getZ()) / countOfCalculations) * timesRuns));
           }
           //logger.log(Level.INFO,"DISTANCE CACULATET"+startPosition.getX());
       }
    }
}
