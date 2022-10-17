package ch.dcreations.cncsimulator.cncControl.GCodes.moveComands;


import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.GCodes.FeedOptions;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.Calculator;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableIntegerValue;

import java.util.HashMap;
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


    protected Map<AxisName, Double> calculatePosition(int timesRuns, int resolution) throws Exception {
        Map<AxisName, Double> posistionMap = new HashMap<>();
        if(feed.get() == 0) throw new Exception("Feed rate us 0");
        if(distance != 0){
            double currentPosX =  startPosition.getX()+(((endPosition.getX() - startPosition.getX()) / resolution) * timesRuns);
               double currentPosY = startPosition.getY()+ (((endPosition.getY() - startPosition.getY()) / resolution) * timesRuns);
               double currentPosZ = startPosition.getZ()+(((endPosition.getZ() - startPosition.getZ()) / resolution) * timesRuns);
               posistionMap.put(AxisName.X, currentPosX);
               posistionMap.put(AxisName.Y, currentPosY);
               posistionMap.put(AxisName.Z, currentPosZ);

           }
        return posistionMap;
    }
}
