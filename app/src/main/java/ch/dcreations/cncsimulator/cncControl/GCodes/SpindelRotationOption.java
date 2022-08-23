package ch.dcreations.cncsimulator.cncControl.GCodes;

public enum SpindelRotationOption {
    CONSTANT_SURFACE_SPEED("G96"),
    CONSTANT_ROTATION("G97");

    SpindelRotationOption(String SURFACE_OPTION) {
    }
}
