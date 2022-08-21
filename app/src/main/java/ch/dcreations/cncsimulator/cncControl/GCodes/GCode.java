package ch.dcreations.cncsimulator.cncControl.GCodes;
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
public class GCode {
    long codeNumber;

    public GCode(long codeNumber) {
        this.codeNumber = codeNumber;
    }

    public long getCodeNumber() {
        return codeNumber;
    }
}
