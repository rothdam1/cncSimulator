package ch.dcreations.cncsimulator.cncControl;
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

    AxisName axisName;
    double axisPosition;

    public CNCAxis(AxisName axisName) {
        this.axisName = axisName;
        axisPosition = 0;
    }

    public AxisName getAxisName() {
        return axisName;
    }

    public double getAxisPosition() {
        return axisPosition;
    }
}
