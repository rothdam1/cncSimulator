package ch.dcreations.cncsimulator.cncControl.GCodes.moveComands;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Exceptions.IllegalFormatOfGCodeException;
import ch.dcreations.cncsimulator.cncControl.GCodes.FeedOptions;
import ch.dcreations.cncsimulator.cncControl.GCodes.GCode;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.Config;
import ch.dcreations.cncsimulator.config.ExeptionMessages;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableIntegerValue;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * <p>
 * Extens the GCode Class with all the necessary parameter for the Move Class
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-10-22
 */

public class GCodeMove extends GCode {
    protected Position axisPosition;

    protected Map<AxisName, Double> parameter;
    protected Position endPosition;

    double distance = 0;


    protected double lineStartX = 0;
    protected double lineStartY = 0;
    protected double lineStartZ = 0;

    public GCodeMove(long codeNumber, FeedOptions feedOptions, ObservableIntegerValue spindleSpeed, Position startPosition, SimpleDoubleProperty feed, Position axisPosition, Map<AxisName, Double> parameter) throws Exception {
        super(codeNumber, feedOptions, spindleSpeed, startPosition, feed);
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
        if (!parameter.containsKey(AxisName.X) && parameter.containsKey(AxisName.U))
            throw new Exception(ExeptionMessages.ARGRUMENT_X_AND_U);
        if (!parameter.containsKey(AxisName.X) && !parameter.containsKey(AxisName.U)) {
            parameter.put(AxisName.X, startPosition.getX());
        } else if (parameter.containsKey(AxisName.U)) {
            parameter.put(AxisName.X, startPosition.getX() + parameter.get(AxisName.U));
        }
    }

    private void setupParameterY() throws Exception {
        if (!parameter.containsKey(AxisName.Y) && parameter.containsKey(AxisName.V))
            throw new Exception(ExeptionMessages.ARGRUMENT_X_AND_U);
        if (!parameter.containsKey(AxisName.Y) && !parameter.containsKey(AxisName.V)) {
            parameter.put(AxisName.Y, startPosition.getY());
        } else if (parameter.containsKey(AxisName.V)) {
            parameter.put(AxisName.Y, startPosition.getY() + parameter.get(AxisName.V));
        }
    }

    private void setupParameterZ() throws Exception {
        if (!parameter.containsKey(AxisName.Z) && parameter.containsKey(AxisName.W))
            throw new Exception(ExeptionMessages.ARGRUMENT_X_AND_U);
        if (!parameter.containsKey(AxisName.Z) && !parameter.containsKey(AxisName.W)) {
            parameter.put(AxisName.Z, startPosition.getZ());
        } else if (parameter.containsKey(AxisName.W)) {
            parameter.put(AxisName.Z, startPosition.getZ() + parameter.get(AxisName.W));
        }
    }

    public Position getAxisPosition() {
        return axisPosition;
    }


    public void execute(AtomicBoolean run, AtomicBoolean brakeRunningCode) throws Exception {
        /*
        finished.set(false);
        if (feed.get() == 0) throw new Exception("Feed rate us 0");
        if (distance == 0) {
            finished.set(true);
        } else {
            double timeMS = (feedOptions == FeedOptions.FEED_PER_REVOLUTION) ?
                    (60 * ((distance / feed.get()) / spindleSpeed.get()) * 1000) : ((60 * (distance / feed.get())) * 1000);
            List<Map<AxisName, Double>> toolPath = getPath(Config.POSITION_CALCULATION_RESOLUTION);
            List<Map<AxisName, Double>> drawPath = getPath(Config.POSITION_CALCULATION_RESOLUTION);
            double countOfCalculations = Math.ceil(timeMS / Config.VIEW_ACTUALISATION_MULTIPLICATION);
            for (int i = 0; i < countOfCalculations && run.get() && !finished.get(); ) {
                if (!brakeRunningCode.get()) {
                    int position = (int) Math.round(i * (toolPath.size() / countOfCalculations));
                    if (toolPath.size() > position) {
                        moveAxisToPathPosition(toolPath.get(position));
                        i++;
                        for (int j = toolPath.size() - drawPath.size(); j < position; j++) {
                            if (drawPath.size() > 0) {
                                   drawPath.remove(0);
                            }
                        }
                    }
                }else {
                    if (!finished.get()) {
                        Thread.sleep(Config.VIEW_ACTUALISATION_MULTIPLICATION);
                    }
                }
                if (!finished.get()) {
                    Thread.sleep(Config.VIEW_ACTUALISATION_MULTIPLICATION);
                }


            }
            if (run.get()) {
                Map<AxisName, Double> posistionMap = new HashMap<>();
                posistionMap.put(AxisName.X, endPosition.getX());
                posistionMap.put(AxisName.Y, endPosition.getY());
                posistionMap.put(AxisName.Z, endPosition.getZ());
                moveAxisToPathPosition(posistionMap);
                int i = 0;
                while (i < 10 || axisPosition.getX() != endPosition.getX() || axisPosition.getZ() != endPosition.getZ() || axisPosition.getY() != endPosition.getY()) {
                    Thread.sleep(Config.VIEW_ACTUALISATION_MULTIPLICATION);
                    i++;
                }
            }
        }
        finished.set(true);
        */
    }

    private void moveAxisToPathPosition(Map<AxisName, Double> position) {
        axisPosition.setX(position.get(AxisName.X));
        axisPosition.setY(position.get(AxisName.Y));
        axisPosition.setZ(position.get(AxisName.Z));
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
        Map<AxisName, Double> posistionMap = new HashMap<>();
        double xErrorDiff = endPosition.getX() - axisNameDoubleMap.get(AxisName.X);
        double yErrorDiff = endPosition.getY() - axisNameDoubleMap.get(AxisName.Y);
        double zErrorDiff = endPosition.getZ() - axisNameDoubleMap.get(AxisName.Z);
        double error = Math.sqrt(xErrorDiff * xErrorDiff + yErrorDiff * yErrorDiff + zErrorDiff * zErrorDiff);
        if (error > Config.CALCULATION_ERROR_MAX_FOR_CIRCLE_END_POINT)
            throw new IllegalFormatOfGCodeException("END POINT DOES NOT MATCH");
        posistionMap.put(AxisName.X, endPosition.getX());
        posistionMap.put(AxisName.Y, endPosition.getY());
        posistionMap.put(AxisName.Z, endPosition.getZ());
        return posistionMap;
    }
}
