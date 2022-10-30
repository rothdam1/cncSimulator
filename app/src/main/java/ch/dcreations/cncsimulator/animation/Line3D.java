package ch.dcreations.cncsimulator.animation;
import javafx.scene.shape.Shape3D;
import org.fxyz3d.shapes.composites.PolyLine3D;

import java.util.LinkedList;
import java.util.List;

public class Line3D  {

    PolyLine3D polPolyLine3D;
    public Line3D(double startX, double startY, double startZ, double endX, double endY, double endZ, javafx.scene.paint.Color color) {
        List<org.fxyz3d.geometry.Point3D> points = new LinkedList<>();
        points.add(new org.fxyz3d.geometry.Point3D(startX,startY,startZ));
        points.add(new org.fxyz3d.geometry.Point3D(endX,endY,endZ));
        float width = 2;
        polPolyLine3D = new PolyLine3D(points,width,color, PolyLine3D.LineType.TRIANGLE);
    }

    public PolyLine3D get3DObject() {
        return polPolyLine3D;
    }
}
