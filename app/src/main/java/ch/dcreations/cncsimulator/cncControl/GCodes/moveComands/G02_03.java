package ch.dcreations.cncsimulator.cncControl.GCodes.moveComands;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.Plane;
import ch.dcreations.cncsimulator.cncControl.Exceptions.IllegalFormatOfGCodeException;
import ch.dcreations.cncsimulator.cncControl.GCodes.FeedOptions;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.Calculator;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableIntegerValue;

import java.util.Map;
import java.util.logging.Logger;

public class G02_03 extends GCodeMove {

    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());

    Map<Character, Double> additionalParameterMap;

    double distance = 0;
    Plane plane;

    double radius = 0;

    double degree = 0;

    public G02_03(long codeNumber, FeedOptions feedOptions, ObservableIntegerValue spindleSpeed, Position startPosition, SimpleDoubleProperty feed, Map<AxisName,Double> parameter, Map<Character, Double> additionalParameterMap, Plane plane) throws Exception {
        super(codeNumber, feedOptions, spindleSpeed, startPosition,feed,new Position(startPosition.getX(), startPosition.getY(), startPosition.getZ()),parameter);
        this.additionalParameterMap = additionalParameterMap;
        this.plane = plane;
        setupParameterForCircle();
    }

    private void setupParameterForCircle() throws IllegalFormatOfGCodeException {
        checkCircleParameter();
        calculateParameter();
        degree = caculateDegree();
        distance =( (radius*2*Math.PI)*degree/360);
    }

    private double caculateDegree() {
        double x1=0, y1=0, x2=0, y2=0,xC=0,yC=0;
        switch (plane){
            case G17 -> {
                x1 = startPosition.getX();
                y1 = startPosition.getY();
                x2 = endPosition.getX();
                y2 = endPosition.getY();
                xC = additionalParameterMap.get('I');
                yC = additionalParameterMap.get('J');
            }
            case G18 -> {
                x1 = startPosition.getX();
                y1 = startPosition.getZ();
                x2 = endPosition.getX();
                y2 = endPosition.getZ();
                xC = additionalParameterMap.get('I');
                yC = additionalParameterMap.get('K');
            }
            case G19 -> {
                y1 = startPosition.getY();
                y1 = startPosition.getZ();
                x2 = endPosition.getY();
                y2 = endPosition.getZ();
                xC = additionalParameterMap.get('J');
                yC = additionalParameterMap.get('K');
            }
        }
        double top = (x1-xC)*(x2-xC)+(y1-yC)*(y2-yC);
        double down1 = Math.sqrt((x1-xC)*(x1-xC)+(y1-yC)*(y1-yC));
        double down2 = Math.sqrt((x2-xC)*(x2-xC)+(y2-yC)*(y2-yC));
        double angle =  Math.acos(top/(down1*down2));
        radius = Math.sqrt(xC*xC+yC*yC);
        angle = (directionAngle() == true) ? angle : 360-angle;
        return angle;
    }

    private boolean directionAngle() {
        return (codeNumber == 2) ? true : false;
    }

    private void calculateParameter() {
        if (additionalParameterMap.containsKey('R')) {
            int direction = (codeNumber == 2) ? 1 : -1;
            double x = endPosition.getX()-startPosition.getX();
            double y = endPosition.getY()-startPosition.getY();
            double z = endPosition.getZ()-startPosition.getZ();
            double legC = additionalParameterMap.get('R');
            switch (plane){
                case G17 -> {
                    double legA =  Math.sqrt(x*x+y*y)/2;
                    double legB = Math.sqrt(legC*legC-legA*legA);
                    double multiplicatior =legC/ (legC - legB);
                    additionalParameterMap.put('I',direction*multiplicatior*(x/2));
                    additionalParameterMap.put('J',direction*multiplicatior*(x/2));
                    }
                case G18 -> {
                    double legA =  Math.sqrt(x*x+z*z)/2;
                    double legB = Math.sqrt(legC*legC-legA*legA);
                    double multiplicatior =legC/ (legC - legB);
                    additionalParameterMap.put('I',multiplicatior*(x/2));
                    additionalParameterMap.put('K',multiplicatior*(x/2));
                }
                case G19 -> {
                    double legA =  Math.sqrt(z*z+y*y)/2;
                    double legB = Math.sqrt(legC*legC-legA*legA);
                    double multiplicatior =legC/ (legC - legB);
                    additionalParameterMap.put('I',multiplicatior*(x/2));
                    additionalParameterMap.put('J',multiplicatior*(x/2));
                }
            }
        }
    }

    private void checkCircleParameter() throws IllegalFormatOfGCodeException {
        double distanceWay = Calculator.vectorDistance(endPosition.getX()-startPosition.getX(),endPosition.getY()-startPosition.getY(),endPosition.getZ()-startPosition.getZ());
        if(additionalParameterMap.containsKey('R') && (additionalParameterMap.containsKey('I') || additionalParameterMap.containsKey('J') || additionalParameterMap.containsKey('K'))){
            throw new IllegalFormatOfGCodeException("Contains R and I or K or J");
        }else if(!(additionalParameterMap.containsKey('R') || additionalParameterMap.containsKey('I') || additionalParameterMap.containsKey('J') || additionalParameterMap.containsKey('K'))){
            throw new IllegalFormatOfGCodeException("Contains no R or I or K or J");
        }
        if(additionalParameterMap.containsKey('R')){
            if (additionalParameterMap.get('R') < (distanceWay/2)) {
                throw new IllegalFormatOfGCodeException("R distance is more then 180 Degree");
            }
        }else {
            // CHECK CORRECT PLANE 
            if(plane == Plane.G17 && additionalParameterMap.containsKey('K')) throw new  IllegalFormatOfGCodeException("G17 AND K DOES NOT MATCH");
            if(plane == Plane.G18 && additionalParameterMap.containsKey('J')) throw new  IllegalFormatOfGCodeException("G18 AND J DOES NOT MATCH");
            if(plane == Plane.G19 && additionalParameterMap.containsKey('I')) throw new  IllegalFormatOfGCodeException("G19 AND I DOES NOT MATCH");
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