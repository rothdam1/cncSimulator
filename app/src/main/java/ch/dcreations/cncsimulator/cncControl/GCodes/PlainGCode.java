package ch.dcreations.cncsimulator.cncControl.GCodes;

import ch.dcreations.cncsimulator.cncControl.Position.Position;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableIntegerValue;
/**
 * <p>
 * <p>
 * Is a GCode with any funktion
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-10-22
 */
public class PlainGCode extends GCode{
    public PlainGCode(long codeNumber) {
        super(codeNumber);
    }
}
