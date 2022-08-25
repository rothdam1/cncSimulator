package ch.dcreations.cncsimulator.cncControl.Position;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Position {
    SimpleDoubleProperty x = new SimpleDoubleProperty();
    SimpleDoubleProperty y = new SimpleDoubleProperty();
    SimpleDoubleProperty z = new SimpleDoubleProperty();
    SimpleDoubleProperty a = new SimpleDoubleProperty();
    SimpleDoubleProperty b = new SimpleDoubleProperty();
    SimpleDoubleProperty c = new SimpleDoubleProperty();

    public Position(double x, double y, double z) {
        this(x,y,z,0.0,0.0,0.0);
    }

    public Position(double x, double y, double z, double a, double b, double c) {
        this.x.set(x);
        this.y.set(y);
        this.z.set(z);
        this.a.set(a);
        this.b.set(b);
        this.c.set(c);
    }

    public double getX() {
        return x.get();
    }

    public void setX(double x_) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                y.set(x_);
            }
        });
    }

    public double getY() {
        return y.get();
    }

    public void setY(double y_) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                y.set(y_);
            }
        });
    }

    public double getZ() {
        return z.get();
    }

    public void setZ(double z_) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                z.set(z_);
            }
        });;
    }

    public double getA() {
        return a.get();
    }

    public void setA(double a) {
        this.a.set(a);
    }

    public double getB() {
        return b.get();
    }

    public void setB(double b) {
        this.b.set(b);
    }

    public double getC() {
        return c.get();
    }

    public void setC(double c) {
        this.c.set(c);
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public DoubleProperty zProperty() {
        return z;
    }

    public DoubleProperty aProperty() {
        return a;
    }

    public DoubleProperty bProperty() {
        return b;
    }

    public DoubleProperty cProperty() {
        return c;
    }
}
