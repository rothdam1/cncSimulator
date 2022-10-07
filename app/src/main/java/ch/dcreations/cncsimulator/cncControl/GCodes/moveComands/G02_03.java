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
    double direction ;
    Plane plane;

    double radius = 0;

    double degree = 0;


    public G02_03(long codeNumber, FeedOptions feedOptions, ObservableIntegerValue spindleSpeed, Position startPosition, SimpleDoubleProperty feed, Map<AxisName,Double> parameter, Map<Character, Double> additionalParameterMap, Plane plane) throws Exception {
        super(codeNumber, feedOptions, spindleSpeed, startPosition,feed,new Position(startPosition.getX(), startPosition.getY(), startPosition.getZ()),parameter);
        this.additionalParameterMap = additionalParameterMap;
        this.plane = plane;
        setupParameterForCircle();
        lineStartX = startPosition.getX();
        lineStartY = startPosition.getY();
        lineStartZ = startPosition.getZ();
        direction =  (codeNumber == 3) ? 1 : -1;
    }

    private void setupParameterForCircle() throws IllegalFormatOfGCodeException {
        checkCircleParameter();
        calculateParameter();
        degree = calculateDegree();
        distance =( (radius*2*Math.PI)*degree/360);
    }

    private double calculateDegree() {
        double x1=0, y1=0, x2=0, y2=0,xC=0,yC=0;
        switch (plane){
            case G17 -> {
                x2 = endPosition.getX()-startPosition.getX();
                y2 = endPosition.getY()-startPosition.getY();
                xC = additionalParameterMap.get('I');
                yC = additionalParameterMap.get('J');
            }
            case G18 -> {
                x2 = endPosition.getX()-startPosition.getX();
                y2 = endPosition.getZ()-startPosition.getZ();
                xC = additionalParameterMap.get('I');
                yC = additionalParameterMap.get('K');
            }
            case G19 -> {
                x2 = endPosition.getY()-startPosition.getY();
                y2 = endPosition.getZ()- startPosition.getZ();
                xC = additionalParameterMap.get('J');
                yC = additionalParameterMap.get('K');
            }
        }
        // calculating angle between two points.
        // check if the center point is right or left from the vektor
        // to check if the circle is more then 180 degree
        double xCInBasis2 = xC*x2+y2*y2;
        double yCInBasis2 = xC*(-y2)+y2*x2;
        xCInBasis2 = xCInBasis2 - (xCInBasis2%0.0001);
        yCInBasis2 = yCInBasis2 - (yCInBasis2%0.0001);

        int yCInBasis2Sign = (yCInBasis2 >= yCInBasis2*-1) ? 1 : -1;
        int xCInBasis2Sign = (xCInBasis2 >= xCInBasis2*-1) ? 1 : -1;
        int directionSign = (directionAngle()) ? 1 : -1;
        double top = (x1-xC)*(x2-xC)+(y1-yC)*(y2-yC);
        double down1 = Math.sqrt((x1-xC)*(x1-xC)+(y1-yC)*(y1-yC));
        double down2 = Math.sqrt((x2-xC)*(x2-xC)+(y2-yC)*(y2-yC));
        double angle = Math.toDegrees(Math.acos(top/(down1*down2)));
        radius = Math.sqrt(xC*xC+yC*yC);
        angle = ((yCInBasis2Sign*xCInBasis2Sign*directionSign  > 0)) ? angle : 360- angle;
        return angle;
    }

    private boolean directionAngle() {
        return codeNumber == 3;
    }

    private void calculateParameter() {
        if (additionalParameterMap.containsKey('R')) {
            double x = endPosition.getX()-startPosition.getX();
            double y = endPosition.getY()-startPosition.getY();
            double z = endPosition.getZ()-startPosition.getZ();
            double legC = additionalParameterMap.get('R');
            int direction2 = (directionAngle()) ?  1 : -1;
            switch (plane){
                case G17 -> {
                    double legA =  Math.sqrt(x*x+y*y)/2;
                    double legB = Math.sqrt(legC*legC-legA*legA);
                    double multiplication = legB/(Math.sqrt(x*x+y*y));
                    additionalParameterMap.put('I',(x/2)+multiplication*(-1* direction2 *y));
                    additionalParameterMap.put('J',(y/2)+multiplication*(direction2* x));
                    }
                case G18 -> {
                    double legA =  Math.sqrt(x*x+z*z)/2;
                    double legB = (Math.sqrt(legC*legC-legA*legA));
                    double multiplication = legB/(Math.sqrt(x*x+z*z));
                    additionalParameterMap.put('I',(x/2)+multiplication*(-1* direction2 *z));
                    additionalParameterMap.put('K',(z/2)+multiplication*(direction2* x));
                }
                case G19 -> {
                    double legA =  Math.sqrt(z*z+y*y)/2;
                    double legB = Math.sqrt(legC*legC-legA*legA);
                    double multiplication = legB/(Math.sqrt(z*z+y*y));
                    additionalParameterMap.put('J',(y/2)+multiplication*(-1* direction2 *z));
                    additionalParameterMap.put('K',(z/2)+multiplication*(direction2* y));
                }
            }
        }else {
            switch (plane){
                case G17 -> {
                    if(!additionalParameterMap.containsKey('J'))additionalParameterMap.put('J',0.0);
                    if(!additionalParameterMap.containsKey('I'))additionalParameterMap.put('I',0.0);
                }
                case G18 -> {
                    if(!additionalParameterMap.containsKey('I'))additionalParameterMap.put('I',0.0);
                    if(!additionalParameterMap.containsKey('K'))additionalParameterMap.put('K',0.0);
                }
                case G19 -> {
                    if(!additionalParameterMap.containsKey('J'))additionalParameterMap.put('J',0.0);
                    if(!additionalParameterMap.containsKey('K'))additionalParameterMap.put('K',0.0);
                }
            }
        }
    }

    private void checkCircleParameter() throws IllegalFormatOfGCodeException {
        double distanceWay ;
        switch (plane){
            case G17 -> distanceWay = Calculator.vectorDistance(endPosition.getX()-startPosition.getX(),endPosition.getY()-startPosition.getY(),0);
            case G18 -> distanceWay = Calculator.vectorDistance(endPosition.getX()-startPosition.getX(),0,endPosition.getZ()-startPosition.getZ());
            case G19 -> distanceWay = Calculator.vectorDistance(0,endPosition.getY()-startPosition.getY(),endPosition.getZ()-startPosition.getZ());
            default -> distanceWay = Calculator.vectorDistance(endPosition.getX()-startPosition.getX(),endPosition.getY()-startPosition.getY(),endPosition.getZ()-startPosition.getZ());
        }
        if(additionalParameterMap.containsKey('R') && ((additionalParameterMap.containsKey('I') || additionalParameterMap.containsKey('J') || additionalParameterMap.containsKey('K')))){
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
                axisPosition.setX(endPosition.getX());
                axisPosition.setY(endPosition.getY());
                axisPosition.setZ(endPosition.getZ());
                finished.set(true);
            }else {
                double degreeToMove = degree/countOfCalculations*timesRuns;
                double currentPosX= 0;
                double currentPosY = 0;
                double currentPosZ = 0;

                switch (plane){
                    case G17 -> {
                        // rotate Vector with Angle
                        double radiansToMove = direction*Math.toRadians(degreeToMove);
                        double i2 = Math.cos(radiansToMove)*additionalParameterMap.get('I')-Math.sin(radiansToMove)*additionalParameterMap.get('J');
                        double j2 = Math.sin(radiansToMove)*additionalParameterMap.get('I')+Math.cos(radiansToMove)*additionalParameterMap.get('J');
                        double xMove = additionalParameterMap.get('I')-i2;
                        double jMove = additionalParameterMap.get('J')-j2;
                        currentPosX = startPosition.getX()+xMove;
                        currentPosY = startPosition.getY()+jMove;
                        currentPosZ = startPosition.getZ()+(((endPosition.getZ() - startPosition.getZ()) / countOfCalculations) * timesRuns);
                    }
                    case G18 -> {
                        // rotate Vector with Angle
                        double radiansToMove = direction*Math.toRadians(degreeToMove);
                        double i2 = Math.cos(radiansToMove)*additionalParameterMap.get('I')-Math.sin(radiansToMove)*additionalParameterMap.get('K');
                        double k2 = Math.sin(radiansToMove)*additionalParameterMap.get('I')+Math.cos(radiansToMove)*additionalParameterMap.get('K');
                        double xMove = additionalParameterMap.get('I')-i2;
                        double zMove = additionalParameterMap.get('K')-k2;
                        currentPosX = startPosition.getX()+xMove;
                        currentPosZ = startPosition.getZ()+zMove;
                        currentPosY = startPosition.getY()+ (((endPosition.getY() - startPosition.getY()) / countOfCalculations) * timesRuns);
                    }
                    case G19 -> {
                        // rotate Vector with Angle
                        double radiansToMove = direction*Math.toRadians(degreeToMove);
                        double j2 = Math.cos(radiansToMove)*additionalParameterMap.get('J')-Math.sin(radiansToMove)*additionalParameterMap.get('K');
                        double k2 = Math.sin(radiansToMove)*additionalParameterMap.get('J')+Math.cos(radiansToMove)*additionalParameterMap.get('K');
                        double jMove = additionalParameterMap.get('J')-j2;
                        double zMove = additionalParameterMap.get('K')-k2;
                        currentPosY = startPosition.getY()+jMove;
                        currentPosZ = startPosition.getZ()+zMove;
                        currentPosX = startPosition.getX()+(((endPosition.getX() - startPosition.getX()) / countOfCalculations) * timesRuns);
                    }
                }
                drawAnimation(currentPosX,currentPosY,currentPosZ);
            }
        }
    }
}
