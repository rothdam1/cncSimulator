package ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors;

public enum Plane {
    G17(AxisName.X,AxisName.Y),
    G18(AxisName.X,AxisName.Z),
    G19(AxisName.X,AxisName.Z);

    AxisName axis1;
    AxisName axis2;

    Plane(AxisName axis1, AxisName axis2) {
        this.axis1 = axis1;
        this.axis2 = axis2;
    }

    Plane() {

    }
}
