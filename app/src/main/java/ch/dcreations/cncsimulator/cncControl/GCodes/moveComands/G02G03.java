package ch.dcreations.cncsimulator.cncControl.GCodes.moveComands;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.Plane;
import ch.dcreations.cncsimulator.cncControl.Exceptions.IllegalFormatOfGCodeException;
import ch.dcreations.cncsimulator.cncControl.GCodes.FeedOptions;
import ch.dcreations.cncsimulator.cncControl.GCodes.SpindelRotationOption;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.Calculator;
import java.util.*;

/**
 * <p>
 * <p>
 * Subclass of moving Code, Calculates the G03 G01 path  // Function Circle Interpolation between point
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-10-22
 */

public class G02G03 extends GCodeMove {
    Map<Character, Double> additionalParameterMap;

    double direction;
    Plane plane;

    double radius = 0;

    double degree = 0;

    double distanceErrorMax;

    /**
     *
     * @param codeNumber will be 2 or 3 vor G02 or G03 clockwise or counterclockwise
     * @param feedOptions  of {@link FeedOptions} if feed per Revolution, FEED per Minute
     * @param spindleSpeed rotation of the Spindle Rotation per minute
     * @param startPosition {@link Position} startposition of the code
     * @param feed feed value
     * @param parameter Axis Parameter a {@link Map} of {@link AxisName} and position
     * @param additionalParameterMap all parameters that are no Axis {@link Map} of {@link Character} and position Double
     * @param plane the cncPlane G17 , G18 , G19    X-Y Axis X-Z or Y-Z
     * @param distanceErrorMax the maximum calculation error
     * @throws Exception IF the circleInterpolation is n not possible to calculate or the given parameters are not wright an exception is given
     */
    public G02G03(long codeNumber, FeedOptions feedOptions, SpindelRotationOption spindelRotationOption, double spindleSpeed, Position startPosition, double feed, Map<AxisName, Double> parameter, Map<Character, Double> additionalParameterMap, Plane plane, double distanceErrorMax) throws Exception {
        super(codeNumber, feedOptions,spindelRotationOption, spindleSpeed, startPosition, feed, new Position(startPosition.getX(), startPosition.getY(), startPosition.getZ()), parameter);
        this.additionalParameterMap = additionalParameterMap;
        this.distanceErrorMax = distanceErrorMax;
        this.plane = plane;
        setupParameterForCircle();
        lineStartX = startPosition.getX();
        lineStartY = startPosition.getY();
        lineStartZ = startPosition.getZ();
        direction = (codeNumber == 3) ? 1 : -1;
    }

    private void setupParameterForCircle() throws IllegalFormatOfGCodeException {
        checkCircleParameter();
        calculateParameter();
        degree = calculateDegree();
        distance = ((radius * 2 * Math.PI) * degree / 360);
    }

    private double calculateDegree() {
        // create a triangle white the vector to the endpoint and vector to the center point
        double[] vectorToEndpoint = {0, 0};
        double[] vectorToCircleCenterPoint = {0, 0};
        switch (plane) {
            case G17 -> {
                vectorToEndpoint[0] = endPosition.getX() - startPosition.getX();
                vectorToEndpoint[1] = endPosition.getY() - startPosition.getY();
                vectorToCircleCenterPoint[0] = additionalParameterMap.get('I');
                vectorToCircleCenterPoint[1] = additionalParameterMap.get('J');
            }
            case G18 -> {
                vectorToEndpoint[0] = endPosition.getX() - startPosition.getX();
                vectorToEndpoint[1] = endPosition.getZ() - startPosition.getZ();
                vectorToCircleCenterPoint[0] = additionalParameterMap.get('I');
                vectorToCircleCenterPoint[1] = additionalParameterMap.get('K');
            }
            case G19 -> {
                vectorToEndpoint[0] = endPosition.getY() - startPosition.getY();
                vectorToEndpoint[1] = endPosition.getZ() - startPosition.getZ();
                vectorToCircleCenterPoint[0] = additionalParameterMap.get('J');
                vectorToCircleCenterPoint[1] = additionalParameterMap.get('K');
            }
        }
        // calculating angle between two points.
        // check if the center point is right or left from the vektor
        // to check if the circle is more than 180 degree
        /*
                                    | +y
                                    |
        _-x______180_DEGREE_________|_CenterPoint______0-DEGREE_____________+x
                                    |
                                    | -y
                                    If the circle is more than 180 Degree the y2 in the Base of the Circle point will be minus
         */
        // Calculate Degree
        // x1 = 0 y1 = 0
        // Angle = cos-1(((x1-xC)(x2-xC)+(y1-yc)(y2-yc) )  /   ( sqrt((x1-xc)^2 + (y1 - yc)^2)) * sqrt((x2-xc)^2 + (y2 - yc)^2)))
        double y2InBasis2 = (-vectorToCircleCenterPoint[1]) * vectorToEndpoint[0] + vectorToCircleCenterPoint[0] * vectorToEndpoint[1];
        y2InBasis2 = y2InBasis2 - (y2InBasis2 % 0.0001); // reduce precision
        int y2InBasis2Sign = (y2InBasis2 >= y2InBasis2 * -1) ? 1 : -1;
        int directionSign = (directionAngle()) ? -1 : 1;
        double top = (-vectorToCircleCenterPoint[0]) * (vectorToEndpoint[0] - vectorToCircleCenterPoint[0]) + (-vectorToCircleCenterPoint[1]) * (vectorToEndpoint[1] - vectorToCircleCenterPoint[1]);
        double down1 = Math.sqrt((-vectorToCircleCenterPoint[0]) * (-vectorToCircleCenterPoint[0]) + (-vectorToCircleCenterPoint[1]) * (-vectorToCircleCenterPoint[1]));
        double down2 = Math.sqrt((vectorToEndpoint[0] - vectorToCircleCenterPoint[0]) * (vectorToEndpoint[0] - vectorToCircleCenterPoint[0]) + (vectorToEndpoint[1] - vectorToCircleCenterPoint[1]) * (vectorToEndpoint[1] - vectorToCircleCenterPoint[1]));
        double angle = Math.toDegrees(Math.acos(top / (down1 * down2)));
        radius = Math.sqrt(vectorToCircleCenterPoint[0] * vectorToCircleCenterPoint[0] + vectorToCircleCenterPoint[1] * vectorToCircleCenterPoint[1]);

        // check direction of the Circle because de angle result is always the shorter distance
        angle = ((y2InBasis2Sign * directionSign > 0)) ? angle : 360 - angle;
        return angle;
    }

    private boolean directionAngle() {
        return codeNumber == 3;
    }

    private void calculateParameter() {
        // The Counterpoint is being needed so
        // Calculate Center Position if only the Radius if given
        // and add parameter who are 0
        if (additionalParameterMap.containsKey('R')) {
            double x = endPosition.getX() - startPosition.getX();
            double y = endPosition.getY() - startPosition.getY();
            double z = endPosition.getZ() - startPosition.getZ();
            double legC = additionalParameterMap.get('R');
            int direction2 = (directionAngle()) ? 1 : -1;
            switch (plane) {
                case G17 -> {
                    double halfDistanceBetweenStartAndEnd = Math.sqrt(x * x + y * y);
                    double legA = halfDistanceBetweenStartAndEnd / 2;
                    double legB = Math.sqrt(legC * legC - legA * legA);
                    double multiplication = legB / halfDistanceBetweenStartAndEnd;
                    additionalParameterMap.put('I', (x / 2) + multiplication * (-1 * direction2 * y));
                    additionalParameterMap.put('J', (y / 2) + multiplication * (direction2 * x));
                }
                case G18 -> {
                    double halfDistanceBetweenStartAndEnd = Math.sqrt(x * x + z * z);
                    double legA = halfDistanceBetweenStartAndEnd / 2;
                    double legB = (Math.sqrt(legC * legC - legA * legA));
                    double multiplication = legB / halfDistanceBetweenStartAndEnd;
                    additionalParameterMap.put('I', (x / 2) + multiplication * (-1 * direction2 * z));
                    additionalParameterMap.put('K', (z / 2) + multiplication * (direction2 * x));
                }
                case G19 -> {
                    double halfDistanceBetweenStartAndEnd = Math.sqrt(z * z + y * y);
                    double legA = halfDistanceBetweenStartAndEnd / 2;
                    double legB = Math.sqrt(legC * legC - legA * legA);
                    double multiplication = legB / halfDistanceBetweenStartAndEnd;
                    additionalParameterMap.put('J', (y / 2) + multiplication * (-1 * direction2 * z));
                    additionalParameterMap.put('K', (z / 2) + multiplication * (direction2 * y));
                }
            }
        } else {
            switch (plane) {
                case G17 -> {
                    if (!additionalParameterMap.containsKey('J')) additionalParameterMap.put('J', 0.0);
                    if (!additionalParameterMap.containsKey('I')) additionalParameterMap.put('I', 0.0);
                }
                case G18 -> {
                    if (!additionalParameterMap.containsKey('I')) additionalParameterMap.put('I', 0.0);
                    if (!additionalParameterMap.containsKey('K')) additionalParameterMap.put('K', 0.0);
                }
                case G19 -> {
                    if (!additionalParameterMap.containsKey('J')) additionalParameterMap.put('J', 0.0);
                    if (!additionalParameterMap.containsKey('K')) additionalParameterMap.put('K', 0.0);
                }
            }
        }
    }

    private void checkCircleParameter() throws IllegalFormatOfGCodeException {
        // Check if the given Parameters are right and work with the current Plane of the Canal
        double distanceWay;
        switch (plane) {
            case G17 ->
                    distanceWay = Calculator.vectorDistance(endPosition.getX() - startPosition.getX(), endPosition.getY() - startPosition.getY(), 0);
            case G18 ->
                    distanceWay = Calculator.vectorDistance(endPosition.getX() - startPosition.getX(), 0, endPosition.getZ() - startPosition.getZ());
            case G19 ->
                    distanceWay = Calculator.vectorDistance(0, endPosition.getY() - startPosition.getY(), endPosition.getZ() - startPosition.getZ());
            default ->
                    distanceWay = Calculator.vectorDistance(endPosition.getX() - startPosition.getX(), endPosition.getY() - startPosition.getY(), endPosition.getZ() - startPosition.getZ());
        }
        if (additionalParameterMap.containsKey('R') && ((additionalParameterMap.containsKey('I') || additionalParameterMap.containsKey('J') || additionalParameterMap.containsKey('K')))) {
            throw new IllegalFormatOfGCodeException("Contains R and I or K or J");
        } else if (!(additionalParameterMap.containsKey('R') || additionalParameterMap.containsKey('I') || additionalParameterMap.containsKey('J') || additionalParameterMap.containsKey('K'))) {
            throw new IllegalFormatOfGCodeException("Contains no R or I or K or J");
        }
        if (additionalParameterMap.containsKey('R')) {
            if (additionalParameterMap.get('R') < (distanceWay / 2)) {
                throw new IllegalFormatOfGCodeException("R distance is more then 180 Degree");
            }
        } else {
            // CHECK CORRECT PLANE 
            if (plane == Plane.G17 && additionalParameterMap.containsKey('K'))
                throw new IllegalFormatOfGCodeException("G17 AND K DOES NOT MATCH");
            if (plane == Plane.G18 && additionalParameterMap.containsKey('J'))
                throw new IllegalFormatOfGCodeException("G18 AND J DOES NOT MATCH");
            if (plane == Plane.G19 && additionalParameterMap.containsKey('I'))
                throw new IllegalFormatOfGCodeException("G19 AND I DOES NOT MATCH");
        }
    }

    public Map<AxisName, Double> calculatePosition(int timesRuns, int resolution) {
        Map<AxisName, Double> positionMap = new HashMap<>();
        double currentPosX = 0;
        double currentPosY = 0;
        double currentPosZ = 0;
        double degreeToMove = degree / resolution * timesRuns;
        switch (plane) {
            case G17 -> {
                // rotate Vector with Angle
                double radiansToMove = direction * Math.toRadians(degreeToMove);
                double i2 = Math.cos(radiansToMove) * additionalParameterMap.get('I') - Math.sin(radiansToMove) * additionalParameterMap.get('J');
                double j2 = Math.sin(radiansToMove) * additionalParameterMap.get('I') + Math.cos(radiansToMove) * additionalParameterMap.get('J');
                double xMove = additionalParameterMap.get('I') - i2;
                double jMove = additionalParameterMap.get('J') - j2;
                currentPosX = startPosition.getX() + xMove;
                currentPosY = startPosition.getY() + jMove;
                currentPosZ = startPosition.getZ() + (((endPosition.getZ() - startPosition.getZ()) / resolution) * timesRuns);
            }
            case G18 -> {
                // rotate Vector with Angle
                double radiansToMove = direction * Math.toRadians(degreeToMove);
                double i2 = Math.cos(radiansToMove) * additionalParameterMap.get('I') - Math.sin(radiansToMove) * additionalParameterMap.get('K');
                double k2 = Math.sin(radiansToMove) * additionalParameterMap.get('I') + Math.cos(radiansToMove) * additionalParameterMap.get('K');
                double xMove = additionalParameterMap.get('I') - i2;
                double zMove = additionalParameterMap.get('K') - k2;
                currentPosX = startPosition.getX() + xMove;
                currentPosZ = startPosition.getZ() + zMove;
                currentPosY = startPosition.getY() + (((endPosition.getY() - startPosition.getY()) / resolution) * timesRuns);
            }
            case G19 -> {
                // rotate Vector with Angle
                double radiansToMove = direction * Math.toRadians(degreeToMove);
                double j2 = Math.cos(radiansToMove) * additionalParameterMap.get('J') - Math.sin(radiansToMove) * additionalParameterMap.get('K');
                double k2 = Math.sin(radiansToMove) * additionalParameterMap.get('J') + Math.cos(radiansToMove) * additionalParameterMap.get('K');
                double jMove = additionalParameterMap.get('J') - j2;
                double zMove = additionalParameterMap.get('K') - k2;
                currentPosY = startPosition.getY() + jMove;
                currentPosZ = startPosition.getZ() + zMove;
                currentPosX = startPosition.getX() + (((endPosition.getX() - startPosition.getX()) / resolution) * timesRuns);
            }
        }
        positionMap.put(AxisName.X, currentPosX);
        positionMap.put(AxisName.Y, currentPosY);
        positionMap.put(AxisName.Z, currentPosZ);
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
        double mmPerMinutes =  (feedOptions == FeedOptions.FEED_PER_REVOLUTION) ? feed*currentSpindlerotation : feed;
        return   (distance)/mmPerMinutes*(60.0) ; // (distance Millimeter to Meter and Minutes to second * 1000/60) Multiplication in the end for precision
    }
}
