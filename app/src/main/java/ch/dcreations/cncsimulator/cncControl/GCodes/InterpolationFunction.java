package ch.dcreations.cncsimulator.cncControl.GCodes;

public enum InterpolationFunction {
    G0(0.0),
    G1(1.0),
    G2(2.0),
    G3(3.0);

    InterpolationFunction(double v) {
    }
}
