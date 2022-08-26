package ch.dcreations.cncsimulator.cncControl.GCodes.moveComands;

import ch.dcreations.cncsimulator.cncControl.GCodes.FeedOptions;
import ch.dcreations.cncsimulator.cncControl.GCodes.GCode;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableIntegerValue;

public class GCodeMove extends GCode {
    protected Position axisPosition ;

    public GCodeMove(long codeNumber, FeedOptions feedOptions, ObservableIntegerValue spindleSpeed, Position startPosition, SimpleDoubleProperty feed,Position axisPosition) {
        super(codeNumber, feedOptions, spindleSpeed, startPosition, feed);
        this.axisPosition = axisPosition;
    }

    public Position getAxisPosition() {
        return axisPosition;
    }
}
