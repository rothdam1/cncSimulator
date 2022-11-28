package ch.dcreations.cncsimulator.cncControl.GCodes.moveComands;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Exceptions.IllegalFormatOfGCodeException;
import ch.dcreations.cncsimulator.cncControl.GCodes.FeedOptions;
import ch.dcreations.cncsimulator.cncControl.GCodes.GCode;
import ch.dcreations.cncsimulator.cncControl.GCodes.SpindelRotationOption;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.Config;
import ch.dcreations.cncsimulator.config.ExeptionMessages;
import java.util.*;

/**
 * <p>
 * <p>
 * Extends the GCode Class with all the necessary parameter for the Move Class
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-10-22
 */

public abstract class GCodeMove extends GCode {
    protected Position axisPosition;

    protected Map<AxisName, Double> parameter;
    protected Position endPosition;
    double distance = 0;
    protected FeedOptions feedOptions;

    protected SpindelRotationOption rotationOption;
    protected double spindleSpeed;
    protected Position startPosition;
    protected double feed;
    protected double lineStartX = 0;
    protected double lineStartY = 0;
    protected double lineStartZ = 0;
    protected SpindelRotationOption spindelRotationOption;

    public GCodeMove(long codeNumber, FeedOptions feedOptions,SpindelRotationOption spindelRotationOption, double spindleSpeed, Position startPosition, double feed, Position axisPosition, Map<AxisName, Double> parameter) throws Exception {
        super(codeNumber);
        this.feedOptions = feedOptions;
        this.spindelRotationOption = spindelRotationOption;
        this.spindleSpeed = spindleSpeed;
        this.startPosition = startPosition;
        this.feed = feed;
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
        endPosition = new Position(parameter.get(AxisName.X), parameter.get(AxisName.Y), parameter.get(AxisName.Z));
    }

    private void setupParameterX() throws Exception {
        if (parameter.containsKey(AxisName.X) && parameter.containsKey(AxisName.U))
            throw new Exception(ExeptionMessages.ARGRUMENT_X_AND_U);
        if (!parameter.containsKey(AxisName.X) && !parameter.containsKey(AxisName.U)) {
            parameter.put(AxisName.X, startPosition.getX());
        } else if (parameter.containsKey(AxisName.U)) {
            parameter.put(AxisName.X, startPosition.getX() + parameter.get(AxisName.U));
        }
    }

    private void setupParameterY() throws Exception {
        if (parameter.containsKey(AxisName.Y) && parameter.containsKey(AxisName.V))
            throw new Exception(ExeptionMessages.ARGRUMENT_X_AND_U);
        if (!parameter.containsKey(AxisName.Y) && !parameter.containsKey(AxisName.V)) {
            parameter.put(AxisName.Y, startPosition.getY());
        } else if (parameter.containsKey(AxisName.V)) {
            parameter.put(AxisName.Y, startPosition.getY() + parameter.get(AxisName.V));
        }
    }

    private void setupParameterZ() throws Exception {
        if (parameter.containsKey(AxisName.Z) && parameter.containsKey(AxisName.W))
            throw new Exception(ExeptionMessages.ARGRUMENT_X_AND_U);
        if (!parameter.containsKey(AxisName.Z) && !parameter.containsKey(AxisName.W)) {
            parameter.put(AxisName.Z, startPosition.getZ());
        } else if (parameter.containsKey(AxisName.W)) {
            parameter.put(AxisName.Z, startPosition.getZ() + parameter.get(AxisName.W));
        }
    }

    public List<Map<AxisName, Double>> getPath(double positionCalculationResolution) {
        List<Map<AxisName, Double>> pathList = new LinkedList<>();
        double countOfCalculations = Math.ceil(distance / positionCalculationResolution);
        for (int timesRuns = 0; timesRuns < countOfCalculations; timesRuns++) {
            try {
                pathList.add(calculatePosition(timesRuns, (int) Math.round(distance / positionCalculationResolution)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            pathList.add(endOFPath(pathList.get(pathList.size() - 1)));
        } catch (IllegalFormatOfGCodeException e) {
            throw new RuntimeException(e);
        }
        return pathList;
    }

    protected Map<AxisName, Double> calculatePosition(int timesRuns, int positionCalculationResolution) throws Exception {
        return Collections.emptyMap();
    }

    private Map<AxisName, Double> endOFPath(Map<AxisName, Double> axisNameDoubleMap) throws IllegalFormatOfGCodeException {
        Map<AxisName, Double> positionMap = new HashMap<>();
        double xErrorDiff = endPosition.getX() - axisNameDoubleMap.get(AxisName.X);
        double yErrorDiff = endPosition.getY() - axisNameDoubleMap.get(AxisName.Y);
        double zErrorDiff = endPosition.getZ() - axisNameDoubleMap.get(AxisName.Z);
        double error = Math.sqrt(xErrorDiff * xErrorDiff + yErrorDiff * yErrorDiff + zErrorDiff * zErrorDiff);
        if (error > Config.CALCULATION_ERROR_MAX_FOR_CIRCLE_END_POINT)
            throw new IllegalFormatOfGCodeException("END POINT DOES NOT MATCH");
        positionMap.put(AxisName.X, endPosition.getX());
        positionMap.put(AxisName.Y, endPosition.getY());
        positionMap.put(AxisName.Z, endPosition.getZ());
        return positionMap;
    }


    public abstract double getRunTimeInMillisecond() throws Exception;
}
