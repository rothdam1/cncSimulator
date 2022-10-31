package ch.dcreations.cncsimulator.animation;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public class AnimationModel implements CNCAnimation {

    private final Rotate rotateX = new Rotate(0,Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0,Rotate.Y_AXIS);

    private final Camera camera;
    private final Scale scale = new Scale();
    private final Translate translate = new Translate();

    private final Translate zTranslation = new Translate(0,0,-100);

    private Group groupOf3DObjects ;

    public AnimationModel(Group groupOf3DObjects, Camera camera) {
        this.camera = camera;
        setupCamera();
        this.groupOf3DObjects = groupOf3DObjects;
        try {
            createCoordinateSystem();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void setupCamera() {
        camera.setNearClip(0.01);
        camera.setFarClip(0.01);
        camera.getTransforms().add(rotateX);
        camera.getTransforms().add(rotateY);
        camera.getTransforms().add(zTranslation);
    }

    private void createCoordinateSystem()  {
        List<Shape3D> lines = new LinkedList<>();
        Box boxX = new Box(20,1,1) ;
        Box boxY = new Box(1,20,1) ;
        Box boxZ = new Box(1,1,20) ;
        boxX.setTranslateX(10);
        boxY.setTranslateY(10);
        boxZ.setTranslateZ(10);
        boxX.setMaterial(new PhongMaterial(Color.RED));
        boxY.setMaterial(new PhongMaterial(Color.GREEN));
        boxZ.setMaterial(new PhongMaterial(Color.BLUE));
        lines.add(boxX);
        lines.add(boxY);
        lines.add(boxZ);
        for(Shape3D line : lines) {
            groupOf3DObjects.getChildren().add(line);
        }
    }



    @Override
    public void scale(double i) {
        scale.setX(i);
        scale.setY(i);
        scale.setZ(i);
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
        scale.setX(1);
        scale.setY(1);
        scale.setZ(1);
    }

    public void createNewLine(Vector vector){
            Line3D line = new Line3D(vector.getStartX(), vector.getStartY(), vector.getStartZ(), vector.getEndX(), vector.getEndY(), vector.getEndZ(), vector.getColor());
            line.get3DObject().getTransforms().add(scale);
            groupOf3DObjects.getChildren().add(line.get3DObject());
    }

    public void zoomPlus() {
        zTranslation.setZ(zTranslation.getZ()+50);
    }

    public void zoomMinus() {
        zTranslation.setZ(zTranslation.getZ()-50);
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
