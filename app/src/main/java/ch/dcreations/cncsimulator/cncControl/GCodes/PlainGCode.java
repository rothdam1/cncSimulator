package ch.dcreations.cncsimulator.cncControl.GCodes;

import ch.dcreations.cncsimulator.cncControl.Position.Position;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableIntegerValue;

public class PlainGCode extends GCode{
    public PlainGCode(long codeNumber, FeedOptions feedOptions, ObservableIntegerValue spindleSpeed,SimpleDoubleProperty feed) {
        super(codeNumber, feedOptions, spindleSpeed, new Position(0,0,0), feed);
    }
}
