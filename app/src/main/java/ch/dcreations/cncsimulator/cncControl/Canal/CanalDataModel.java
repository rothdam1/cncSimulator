package ch.dcreations.cncsimulator.cncControl.Canal;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.CNCAxis;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.CNCSpindle;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.SpindelNames;
import ch.dcreations.cncsimulator.cncControl.Exceptions.AxisOrSpindleDoesNotExistExeption;
import ch.dcreations.cncsimulator.cncControl.GCodes.FeedOptions;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class CanalDataModel {

    private CNCSpindle currentSelectedSpindle;

    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());
    private final Map<AxisName, CNCAxis> cncAxes;
    private SimpleDoubleProperty currentFeedRate = new SimpleDoubleProperty(0);
    private FeedOptions feedOptions = FeedOptions.FEED_PER_REVOLUTION;
    private final AtomicBoolean canalRunState = new AtomicBoolean(false);
    private boolean RunLineIsCompleted = false;
    private final Map<SpindelNames, CNCSpindle> cncSpindles;
    private CanalState canalState = CanalState.STOP;

    public CanalDataModel(Map<AxisName, CNCAxis> cncAxes, Map<SpindelNames, CNCSpindle> cncSpindles) {
        this.cncAxes = cncAxes;
        this.cncSpindles = cncSpindles;
    }

    public CNCSpindle getCurrentSelectedSpindle() {
        return currentSelectedSpindle;
    }

    public void setCurrentSelectedSpindle(SpindelNames selectedSpindle) throws AxisOrSpindleDoesNotExistExeption {
        if (cncSpindles.containsKey(selectedSpindle)) {
            this.currentSelectedSpindle = cncSpindles.get(selectedSpindle);
        } else {
            throw new AxisOrSpindleDoesNotExistExeption("Selected Spindle does not Exist");
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

    public boolean isRunLineIsCompleted() {
        return RunLineIsCompleted;
    }

    public void setRunLineIsCompleted(boolean runLineIsCompleted) {
        RunLineIsCompleted = runLineIsCompleted;
    }

    public Map<SpindelNames, CNCSpindle> getCncSpindles() {
        return cncSpindles;
    }

    public void setCanalState(CanalState state) {
        canalState = state;
    }




}
