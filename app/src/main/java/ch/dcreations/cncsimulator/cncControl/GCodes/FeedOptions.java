package ch.dcreations.cncsimulator.cncControl.GCodes;
/**
 * <p>
 * <p>
 *  Enum of the two possible Feed options
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-11-28
 */
public enum FeedOptions {
    FEED_PER_REVOLUTION("G99"),
    FEED_PER_MINUTE("G98");

    private final String gCode;
    FeedOptions(String GCode) {
        this.gCode = GCode;
    }

    public String getGCode() {
        return gCode;
    }
}
