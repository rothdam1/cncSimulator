package ch.dcreations.cncsimulator.animation;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.LinkedList;
import java.util.List;

public class AnimationModel implements CNCAnimation {

    private final Offset offset = new Offset() ;

    private double rotationX=0;
    private double rotationY=0;
    private double rotationZ=0;
    private double zoomFactor=1;
    private final ObservableList<StraightLine> coordinateLines = FXCollections.observableArrayList();
    RoomMatrix roomMatrix = new RoomMatrix();
    private List<StraightLine> bodyList = new LinkedList<>();

    private Pane drawingPane;

    public AnimationModel(Pane drawingPane) {
        createCoordinateSystem(drawingPane);
        update();
    }

    private void createCoordinateSystem(Pane drawingPane)  {
        coordinateLines.add(new StraightLine(new Vector(Color.BLUE, 0, 0, 0, 50, 0, 0), new Line(), offset));
        coordinateLines.add(new StraightLine(new Vector(Color.RED,0,0,0,0,50,0),new Line(),offset));
        coordinateLines.add(new StraightLine(new Vector(Color.GREEN,0,0,0,0,0,50),new Line(),offset));
        this.drawingPane = drawingPane;
        for (StraightLine line : coordinateLines) {
            Platform.runLater(()-> drawingPane.getChildren().add(line.getDrawingLine()));
        }
    }

    public void setOffset(double x, double y ,double z){
        offset.setXOffset(x);
        offset.setYOffset(y);
    }

    public List<StraightLine> getCoordinateSystemeLineList() {
        return coordinateLines.stream().toList();
    }

    public void update(){
        RoomMatrix transformationMatrix = new RoomMatrix();
        transformationMatrix.resetMatrix();
        transformationMatrix.rotationWinkelX(rotationX);
        transformationMatrix.rotationWinkelY(rotationY);
        transformationMatrix.rotationWinkelZ(rotationZ);
        transformationMatrix.zoom(zoomFactor);
        Platform.runLater(() ->coordinateLines.forEach(i -> tranformAxis(i,transformationMatrix)));
        Platform.runLater(() -> bodyList.forEach(i -> tranformAxis(i,transformationMatrix)));
    }
    private void tranformAxis(StraightLine straightLine,RoomMatrix roomMatrixI ){
        Vector tranformation = straightLine.getBaseAxis();
        tranformation = roomMatrixI.drawAngle(tranformation);
        straightLine.updateAxis(tranformation.getStartX(), tranformation.getStartY(), tranformation.getEndX(), tranformation.getEndY());
    };

    public void rotateXAxis(double degree){
        rotationX += degree;
        rotationX = (rotationX > 360) ? rotationX-360 : rotationX;
        update();
    }

    public void rotateYAxis(double degree){
        rotationY += degree;
        rotationY = (rotationY > 360) ? rotationY-360 : rotationY;
        update();
    }

    public void rotateZAxis(double degree){
        rotationZ += degree;
        rotationZ = (rotationZ > 360) ? rotationZ-360 : rotationZ;
        update();
    }



    public void resetView() {
        rotationZ = 0;
        rotationY = 0;
        rotationX = 0;
        zoomFactor = 1;
        roomMatrix.resetMatrix();
        update();
    }

    public void createNewLine(Vector vector){
        StraightLine straightLine = new StraightLine(new Vector(vector.getColor(), vector.getStartX(), vector.getStartY(), vector.getStartZ(), vector.getEndX(), vector.getEndY(), vector.getEndZ()),new Line(),offset);
        bodyList.add(straightLine);
        Platform.runLater(()-> drawingPane.getChildren().add(straightLine.getDrawingLine()));
    }

    public void zoomPlus() {
        zoomFactor = (zoomFactor*1.5);
        update();
    }

    public void zoomMinus() {
        zoomFactor = (zoomFactor*0.75);
        update();
    }


    public void moveYAxis(double distance) {
        offset.setYOffset(offset.getYOffset()+distance) ;
        update();
    }

    public void moveXAxis(double distance) {
        offset.setXOffset(offset.getXOffset()+distance) ;
        update();
    }

    public void deleteAll() {
        bodyList.stream().forEach(i -> i.delete());
        coordinateLines.forEach(i -> i.delete());
        drawingPane.getChildren().clear();
        bodyList.clear();
        createCoordinateSystem(drawingPane);
        update();
    }
}
