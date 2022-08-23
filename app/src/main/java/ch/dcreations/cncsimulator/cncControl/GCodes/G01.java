package ch.dcreations.cncsimulator.cncControl.GCodes;

import ch.dcreations.cncsimulator.cncControl.AxisName;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.Calculator;
import ch.dcreations.cncsimulator.config.ExeptionMessages;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.value.ObservableIntegerValue;

import java.awt.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class G01 extends GCode{

    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());

    Map<AxisName,Double> parameter;
    Position endPosition;

    public G01(long codeNumber, FeedOptions feedOptions, ObservableIntegerValue spindleSpeed, Position startPosition, double feed, Map<AxisName,Double> parameter) throws Exception {
        super(codeNumber, feedOptions, spindleSpeed, startPosition,feed);
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
        if (!parameter.containsKey("X")&&!parameter.containsKey(AxisName.U)){
            parameter.put(AxisName.X, startPosition.getX());
        }else {
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
            parameter.put(AxisName.Z, startPosition.getX()+parameter.get(AxisName.W));
        }
    }

    @Override
    protected void calculatePosition(int timesRuns, int positionCalculationResolution) {
        double distance = Calculator.vectorDistance(endPosition.getX()-startPosition.getX(),endPosition.getY()-startPosition.getY(),endPosition.getZ()-startPosition.getZ());

       if(distance == 0){
           finished.set(true);
       }else {
           double timeMS = (feedOptions == FeedOptions.FEED_PER_REVOLUTION) ?
                   (60/((distance / feed) / spindleSpeed.get()) * 1000) : ((60/(distance / feed)) * 1000);
           double countOfCalculations = Math.ceil(timeMS / positionCalculationResolution);

           logger.log(Level.INFO, "caculatetCount= " + countOfCalculations);
           logger.log(Level.INFO, "times run = " + timesRuns);
           if (countOfCalculations <= timesRuns) {
               finished.set(true);
           } else {
               //startPosition.setX(((endPosition.getX()-startPosition.getX())/countOfCalculations)*timesRuns);
               //startPosition.setY(((endPosition.getY()-startPosition.getY())/countOfCalculations)*timesRuns);
               //startPosition.setZ(((endPosition.getZ()-startPosition.getZ())/countOfCalculations)*timesRuns);
           }
       }
    }
}
