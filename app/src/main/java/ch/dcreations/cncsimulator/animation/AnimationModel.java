package ch.dcreations.cncsimulator.animation;


import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import java.util.LinkedList;
import java.util.List;

public class AnimationModel implements CNCAnimation {

    private final Rotate rotateX = new Rotate(0,Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0,Rotate.Y_AXIS);

    private final Camera camera;
    private final Scale scale = new Scale();
    private final Translate zTranslation = new Translate(0,0,-100);

    private final Group groupOf3DObjects ;

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
        deleteAll();
        createCoordinateSystem();
    }

    public void createNewLine(Vector vector){
            Draw3DObject line = new Draw3DObject(vector.getStartX(), vector.getStartY(), vector.getStartZ(), vector.getEndX(), vector.getEndY(), vector.getEndZ(), vector.getColor());
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
        zTranslation.setY(zTranslation.getY() + (-distance / 3));
    }

    public void moveXAxis(double distance) {
        zTranslation.setX(zTranslation.getX() + (-distance / 3));
    }

    public void deleteAll() {
        groupOf3DObjects.getChildren().clear();
    }
}
