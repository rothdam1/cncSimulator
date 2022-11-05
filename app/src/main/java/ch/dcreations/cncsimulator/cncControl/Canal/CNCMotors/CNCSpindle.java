package ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors;

import ch.dcreations.cncsimulator.cncControl.GCodes.SpindelRotationOption;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class CNCSpindle {

    private SpindelRotationOption spindelRotationOption;

    private SimpleIntegerProperty currentSpindleSpeed = new SimpleIntegerProperty();

    DoubleProperty axisPositionForConstantSurfaceSpeed = new SimpleDoubleProperty();

    public CNCSpindle(SpindelRotationOption spindelRotationOption) {
        this.spindelRotationOption = spindelRotationOption;
    }

    public void setSpindleRotationOption(SpindelRotationOption spindelRotationOption) {
        this.spindelRotationOption = spindelRotationOption;
    }

    public void setSpindleRotationOption(SpindelRotationOption spindelRotationOption, DoubleProperty axisPositionForConstantSurfaceSpeed) {
        this.spindelRotationOption = spindelRotationOption;
        this.axisPositionForConstantSurfaceSpeed = axisPositionForConstantSurfaceSpeed;
    }

    public SimpleIntegerProperty currentSpindleSpeedProperty() {
        return currentSpindleSpeed;
    }

    public void setCurrentSpindleSpeed(int currentSpindleSpeed) {
        this.currentSpindleSpeed.set(currentSpindleSpeed);
    }

    public SpindelRotationOption getSpindelRotationOption() {
        return spindelRotationOption;
    }

    public double getSpindleSpeed() {
        return currentSpindleSpeed.get();
    }
}
