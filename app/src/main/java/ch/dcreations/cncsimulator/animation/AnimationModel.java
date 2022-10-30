package ch.dcreations.cncsimulator.animation;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import java.util.LinkedList;
import java.util.List;

public class AnimationModel implements CNCAnimation {

    Rotate rotateX = new Rotate();
    Rotate rotateY = new Rotate();


    private Scale scale = new Scale();
    Translate translate = new Translate();

    private Group groupOf3DObjects ;

    public AnimationModel(Group groupOf3DObjects) {
        rotateX.setAxis(new Point3D(1, 0, 0));
        rotateY.setAxis(new Point3D(0, 1, 0));
        rotateY.setAngle(0);
        rotateX.setAngle(0);
        this.groupOf3DObjects = groupOf3DObjects;
        createCoordinateSystem();
        update();
    }

    private void createCoordinateSystem()  {
        List<Line3D> lines = new LinkedList<>();
        lines.add(new Line3D(0,0,0,10,0,0,Color.GRAY));
        lines.add(new Line3D(0,0,0,0,10,0,Color.BLACK));
        lines.add(new Line3D(0,0,0,0,0,10,Color.RED));
        for(Line3D line : lines) {
            line.get3DObject().getTransforms().add(scale);
            line.get3DObject().getTransforms().add(translate);
            line.get3DObject().getTransforms().add(rotateY);
            line.get3DObject().getTransforms().add(rotateX);
            groupOf3DObjects.getChildren().add(line.get3DObject());
        }
    }

    public void setOffset(double x, double y ,double z){
    }

    @Override
    public void update() {

    }



    public void rotateXAxis(double degree){
        degree = (degree> 360) ? degree % 360 : degree;
        rotateX.setAngle(rotateX.getAngle()+degree);
    }

    public void rotateYAxis(double degree){
        degree = (degree> 360) ? degree % 360 : degree;
        rotateY.setAngle(rotateY.getAngle()+degree);
    }

    public void rotateZAxis(double degree){
    }



    public void resetView(double middleWidthOfScene,double middleHighOfScene) {
        rotateX.setAngle(0);
        rotateY.setAngle(0);
        scale.setX(2);
        scale.setY(2);
        scale.setZ(2);
        translate.setX(middleWidthOfScene);
        translate.setY(middleHighOfScene);
    }

    public void createNewLine(Vector vector){
        try {
            Line3D line = new Line3D(vector.getStartX(), vector.getStartY(), vector.getStartZ(), vector.getEndX(), vector.getEndY(), vector.getEndZ(), vector.getColor());
            line.get3DObject().getTransforms().add(scale);
            line.get3DObject().getTransforms().add(translate);
            line.get3DObject().getTransforms().add(rotateY);
            line.get3DObject().getTransforms().add(rotateX);

            groupOf3DObjects.getChildren().add(line.get3DObject());
        }catch (Exception e){
            System.out.println("ERROR " + e);
        }
    }

    public void zoomPlus() {
        scale.setX(scale.getX() * 1.5);
        scale.setY(scale.getY() * 1.5);
        scale.setZ(scale.getZ() * 1.5);
        translate.setX(translate.getX() * 0.5);
        translate.setY(translate.getY() * 0.5);
        translate.setZ(translate.getZ() * 0.5);
    }

    public void zoomMinus() {
        scale.setX(scale.getX() * 0.75);
        scale.setY(scale.getY() * 0.75);
        scale.setZ(scale.getZ() * 0.75);
        translate.setX(translate.getX() * 1.5);
        translate.setY(translate.getY() * 1.5);
        translate.setZ(translate.getZ() * 1.5);
    }


    public void moveYAxis(double distance) {
        translate.setY(translate.getY() + (distance / 2));
    }

    public void moveXAxis(double distance) {
        translate.setX(translate.getX() + (distance / 2));
    }

    public void deleteAll() {
    }
}
