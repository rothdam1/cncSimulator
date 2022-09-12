package ch.dcreations.cncsimulator.cncControl.GCodes;

import ch.dcreations.cncsimulator.animation.AnimationModel;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import ch.dcreations.cncsimulator.config.Config;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableIntegerValue;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * <p>
 *  GCode is used as basic for all G Codes
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */
public abstract class GCode {

    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());

    protected long codeNumber;
    protected AtomicBoolean finished = new AtomicBoolean(false);
    protected FeedOptions feedOptions;
    protected ObservableIntegerValue spindleSpeed;
    protected Position startPosition;

    protected SimpleDoubleProperty feed;

    public GCode(long codeNumber, FeedOptions feedOptions, ObservableIntegerValue spindleSpeed, Position startPosition, SimpleDoubleProperty feed) {
        this.codeNumber = codeNumber;
        this.feedOptions = feedOptions;
        this.spindleSpeed = spindleSpeed;
        this.startPosition = startPosition;
        this.feed = feed;
    }

    public long getCodeNumber() {
        return codeNumber;
    }

    public void execute(AtomicBoolean run, AtomicBoolean brakeRunningCode ) throws Exception {
        finished.set(false);
        int timesRuns = 0;
        while (run.get() && !finished.get()) {
            Thread.sleep(Config.POSITION_CALCULATION_RESOLUTION);
            if (!brakeRunningCode.get()){
                calculatePosition(timesRuns, Config.POSITION_CALCULATION_RESOLUTION);
                timesRuns++;
            }
        }
        finished.set(true);
    }

    protected void calculatePosition(int timesRuns, int positionCalculationResolution) throws Exception {
        finished.set(true);
    }


}
