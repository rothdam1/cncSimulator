package ch.dcreations.cncsimulator.cncControl.GCodes.moveComands;


import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.GCodes.FeedOptions;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.Calculator;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableIntegerValue;

import java.util.Map;
import java.util.logging.Logger;

/**
 * <p>
 * <p>
 * Subclass of moving Code, Calculates the G01 path
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-10-22
 */

public class G01 extends GCodeMove {

    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());

    public G01(long codeNumber, FeedOptions feedOptions, ObservableIntegerValue spindleSpeed, Position startPosition, SimpleDoubleProperty feed, Map<AxisName,Double> parameter) throws Exception {
        super(codeNumber, feedOptions, spindleSpeed, startPosition,feed,new Position(startPosition.getX(), startPosition.getY(), startPosition.getZ()),parameter);
        distance = Calculator.vectorDistance(endPosition.getX()-startPosition.getX(),endPosition.getY()-startPosition.getY(),endPosition.getZ()-startPosition.getZ());
        lineStartX = startPosition.getX();
        lineStartY = startPosition.getY();
        lineStartZ = startPosition.getZ();
    }

    @Override
    protected void calculatePosition(int timesRuns, int positionCalculationResolution) throws Exception {
        if(feed.get() == 0) throw new Exception("Feed rate us 0");
        if(distance == 0){
            axisPosition.setX(endPosition.getX());
            axisPosition.setY(endPosition.getY());
            axisPosition.setZ(endPosition.getZ());
           finished.set(true);
       }else {
           double timeMS = (feedOptions == FeedOptions.FEED_PER_REVOLUTION) ?
                   (60*((distance / feed.get()) / spindleSpeed.get()) * 1000) : ((60*(distance / feed.get())) * 1000);
           double countOfCalculations = Math.ceil(timeMS / positionCalculationResolution);
           if (countOfCalculations < timesRuns) {
               finished.set(true);
           }else {
               double currentPosX =  startPosition.getX()+(((endPosition.getX() - startPosition.getX()) / countOfCalculations) * timesRuns);
               double currentPosY = startPosition.getY()+ (((endPosition.getY() - startPosition.getY()) / countOfCalculations) * timesRuns);
               double currentPosZ = startPosition.getZ()+(((endPosition.getZ() - startPosition.getZ()) / countOfCalculations) * timesRuns);
               axisPosition.setX(currentPosX);
               axisPosition.setY(currentPosY);
               axisPosition.setZ(currentPosZ);
               setPositionAndDrawAnimation(currentPosX,currentPosY,currentPosZ);
           }
       }

    }
}
