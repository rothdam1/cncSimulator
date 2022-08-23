package ch.dcreations.cncsimulator.cncControl.GCodes;

public enum FeedOptions {
    FEED_PER_REVOLUTION("G99"),
    FEED_PER_MINUITE("G98");

    FeedOptions(String Gcode) {
    }
}
