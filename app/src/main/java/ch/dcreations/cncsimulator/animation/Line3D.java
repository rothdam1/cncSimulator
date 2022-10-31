package ch.dcreations.cncsimulator.animation;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Sphere;
public class Line3D  {

    Sphere polPolyLine3D;
    public Line3D(double startX, double startY, double startZ, double endX, double endY, double endZ, javafx.scene.paint.Color color) {
        float width = 1;
        this.polPolyLine3D = new Sphere(width);
        polPolyLine3D.setCullFace(CullFace.BACK);
        polPolyLine3D.setTranslateX(endX);
        polPolyLine3D.setTranslateY(endY);
        polPolyLine3D.setTranslateZ(endZ);
    }

    public Sphere get3DObject() {
        return polPolyLine3D;
    }
}
