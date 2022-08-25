package ch.dcreations.cncsimulator.cncControl;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * <p>
 * <p>
 *  Class for a base CNC Axis
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */

public class CNCAxis {

    private AxisName axisName;
    private SimpleDoubleProperty axisPosition = new SimpleDoubleProperty(0);

    public CNCAxis(AxisName axisName) {
        this.axisName = axisName;
    }

    public AxisName getAxisName() {
        return axisName;
    }

    public double getAxisPosition() {
        return axisPosition.get();
    }

    public DoubleProperty axisPositionProperty() {
        return axisPosition;
    }
}
