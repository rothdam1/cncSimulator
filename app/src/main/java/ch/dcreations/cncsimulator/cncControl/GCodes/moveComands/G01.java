package ch.dcreations.cncsimulator.cncControl.GCodes.moveComands;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.GCodes.FeedOptions;
import ch.dcreations.cncsimulator.cncControl.GCodes.SpindelRotationOption;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.Calculator;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * <p>
 * Subclass of moving Code, Calculates the G01 path / Linear interpolation
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-10-22
 */

public class G01 extends GCodeMove {

    public G01(long codeNumber, FeedOptions feedOptions, SpindelRotationOption spindelRotationOption, double spindleSpeed, Position startPosition, double feed, Map<AxisName,Double> parameter) throws Exception {
        super(codeNumber, feedOptions,spindelRotationOption, spindleSpeed, startPosition,feed,new Position(startPosition.getX(), startPosition.getY(), startPosition.getZ()),parameter);
        distance = Calculator.vectorDistance(endPosition.getX()-startPosition.getX(),endPosition.getY()-startPosition.getY(),endPosition.getZ()-startPosition.getZ());
        lineStartX = startPosition.getX();
        lineStartY = startPosition.getY();
        lineStartZ = startPosition.getZ();
    }



    public Map<AxisName, Double> calculatePosition(int timesRuns, int resolution) throws Exception {
        Map<AxisName, Double> positionMap = new HashMap<>();
        if(feed == 0) throw new Exception("Feed rate us 0");
        if(distance != 0){
            double currentPosX =  startPosition.getX()+(((endPosition.getX() - startPosition.getX()) / resolution) * timesRuns);
               double currentPosY = startPosition.getY()+ (((endPosition.getY() - startPosition.getY()) / resolution) * timesRuns);
               double currentPosZ = startPosition.getZ()+(((endPosition.getZ() - startPosition.getZ()) / resolution) * timesRuns);
               positionMap.put(AxisName.X, currentPosX);
               positionMap.put(AxisName.Y, currentPosY);
               positionMap.put(AxisName.Z, currentPosZ);

           }
        return positionMap;
    }

    // TODO: 05.11.2022 FUNCTION DOES NOT SUPPORT YET G96 at the moments fake with X AXIS
    @Override
    public double getRunTimeInMillisecond() throws Exception {
        double currentSpindlerotation;
        if (rotationOption == SpindelRotationOption.CONSTANT_SURFACE_SPEED) {
            currentSpindlerotation = spindleSpeed*1000 / Math.abs(endPosition.getX()-startPosition.getX()) / Math.PI;
        }else {
            currentSpindlerotation = spindleSpeed;
        }
        double feedPerMinutes =  (feedOptions == FeedOptions.FEED_PER_REVOLUTION) ? feed*currentSpindlerotation*1000 : feed;
        return   (distance)/feedPerMinutes*(1000.0/60.0) ; // (distance Millimeter to Meter and Minutes to second * 1000/60) Multiplication in the end for precision
    }
}
