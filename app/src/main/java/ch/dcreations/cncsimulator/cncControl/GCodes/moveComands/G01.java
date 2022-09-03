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

public class G01 extends GCodeMove {

    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());
    public G01(long codeNumber, FeedOptions feedOptions, ObservableIntegerValue spindleSpeed, Position startPosition, SimpleDoubleProperty feed, Map<AxisName,Double> parameter) throws Exception {
        super(codeNumber, feedOptions, spindleSpeed, startPosition,feed,new Position(startPosition.getX(), startPosition.getY(), startPosition.getZ()),parameter);
        distance = Calculator.vectorDistance(endPosition.getX()-startPosition.getX(),endPosition.getY()-startPosition.getY(),endPosition.getZ()-startPosition.getZ());
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
       }
    }
}
