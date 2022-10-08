package ch.dcreations.cncsimulator.cncControl.Canal;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.*;
import ch.dcreations.cncsimulator.cncControl.Exceptions.AxisOrSpindleDoesNotExistException;
import ch.dcreations.cncsimulator.cncControl.GCodes.FeedOptions;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.property.SimpleDoubleProperty;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class CanalDataModel {

    private CNCSpindle currentSelectedSpindle;

    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());
    private final Map<AxisName, CNCAxis> cncAxes;
    private final SimpleDoubleProperty currentFeedRate = new SimpleDoubleProperty(0);
    private FeedOptions feedOptions = FeedOptions.FEED_PER_REVOLUTION;
    private final AtomicBoolean canalRunState = new AtomicBoolean(false);
    private final Map<SpindelNames, CNCSpindle> cncSpindles;
    private CanalState canalState = CanalState.STOP;
    private Plane plane;

    private final double calculationErrorMaxInCircle;

    public CanalDataModel(Map<AxisName, CNCAxis> cncAxes, Map<SpindelNames, CNCSpindle> cncSpindles, Plane plane, double caclulationErroMaxInCircle) {
        this.cncAxes = cncAxes;
        this.cncSpindles = cncSpindles;
        this.calculationErrorMaxInCircle = caclulationErroMaxInCircle;
        setPlane(plane);
    }

    public CNCSpindle getCurrentSelectedSpindle() {
        return currentSelectedSpindle;
    }

    public void setCurrentSelectedSpindle(SpindelNames selectedSpindle) throws AxisOrSpindleDoesNotExistException {
        if (cncSpindles.containsKey(selectedSpindle)) {
            this.currentSelectedSpindle = cncSpindles.get(selectedSpindle);
        } else {
            throw new AxisOrSpindleDoesNotExistException("Selected Spindle does not Exist");
        }
    }

    public AtomicBoolean getCanalRunState() {
        return canalRunState;
    }

    public void setCanalRunState(boolean state){
        canalRunState.set(state);
    }

    public CanalState getCanalState() {
        return canalState;
    }

    public Map<AxisName, CNCAxis> getCncAxes() {
        return cncAxes;
    }

    public double getCurrentFeedRate() {
        return currentFeedRate.get();
    }

    public SimpleDoubleProperty currentFeedRateProperty() {
        return currentFeedRate;
    }

    public void setCurrentFeedRate(double currentFeedRate) {
        this.currentFeedRate.set(currentFeedRate);
    }

    public void setCurrentSpindleSpeed(int speed){
        getCurrentSelectedSpindle().setCurrentSpindleSpeed(speed);
    }

    public FeedOptions getFeedOptions() {
        return feedOptions;
    }

    public void setFeedOptions(FeedOptions feedOptions) {
        this.feedOptions = feedOptions;
    }


    public Map<SpindelNames, CNCSpindle> getCncSpindles() {
        return cncSpindles;
    }

    public void setCanalState(CanalState state) {
        canalState = state;
    }


    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public double getCalculationErrorMaxInCircle() {
        return calculationErrorMaxInCircle;
    }
}
