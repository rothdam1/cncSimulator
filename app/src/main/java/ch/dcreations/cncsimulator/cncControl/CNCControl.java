package ch.dcreations.cncsimulator.cncControl;

import java.util.Collections;
import java.util.List;
/**
 * <p>
 * <p>
 *  The CNC control contains axis and Program
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */
public class CNCControl {

    private final List<CNCAxis> cncAxes ;

    public CNCControl(List<CNCAxis> cncAxes) {
        this.cncAxes = cncAxes;
    }

    public List<CNCAxis> getCncAxes() {
        return Collections.unmodifiableList(cncAxes);
    }
}
