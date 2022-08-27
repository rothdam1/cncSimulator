package ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors;

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

    private final SimpleDoubleProperty axisPosition = new SimpleDoubleProperty(0);


    public double getAxisPosition() {
        return axisPosition.get();
    }

    public DoubleProperty axisPositionProperty() {
        return axisPosition;
    }
}
