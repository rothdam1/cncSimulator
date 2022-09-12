package ch.dcreations.cncsimulator.animation;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import java.util.ArrayList;
import java.util.List;

public class AnimationModel {

    private final Offset offset = new Offset() ;
    private final ObservableList<StraightLine> coordinateLines = FXCollections.observableArrayList();
    RoomMatrix roomMatrix = new RoomMatrix();
    private List<StraightLine> bodyList = new ArrayList<>();

    private Pane drawingPane;

    public AnimationModel(Pane drawingPane) {
        coordinateLines.add(new StraightLine(new Axis(Color.BLUE, 0, 0, 0, 50, 0, 0), new Line(), offset));
        coordinateLines.add(new StraightLine(new Axis(Color.RED,0,0,0,0,50,0),new Line(),offset));
        coordinateLines.add(new StraightLine(new Axis(Color.GREEN,0,0,0,0,0,50),new Line(),offset));
        this.drawingPane = drawingPane;
        for (StraightLine line : coordinateLines) {
            Platform.runLater(()-> drawingPane.getChildren().add(line.getDrawingLine()));
        }
        update();
    }

    public void setOffset(double x, double y ,double z){
        offset.setxOffset(x);
        offset.setyOffset(y);
        offset.setzOffset(z);
    }

    public List<StraightLine> getCoordinateSystemeLineList() {
        return coordinateLines.stream().toList();
    }

    public void update(){
        coordinateLines.forEach(i -> {i.setViewAxis(roomMatrix.drawBody(i.getViewAxis()));i.updateAxis();});
        bodyList.forEach(i -> {i.setViewAxis(roomMatrix.drawBody(i.getViewAxis()));i.updateAxis();});
    }

    public void rotateXAxis(double degree){
        roomMatrix.rotationWinkelX(degree);
        update();
    }

    public void rotateYAxis(double degree){
        roomMatrix.rotationWinkelY(degree);
        update();
    }

    public void rotateZAxis(double degree){
        roomMatrix.rotationWinkelZ(degree);
        update();
    }



    public void resetView() {
        coordinateLines.forEach(i -> {i.setViewAxis(roomMatrix.drawBody(i.getBaseAxis()));i.updateAxis();});
        bodyList.forEach(i -> {i.setViewAxis(roomMatrix.drawBody(i.getBaseAxis()));i.updateAxis();});
        roomMatrix.MatrixStandard();
        update();
    }

    public void createNewLine(Axis axis){
        StraightLine straightLine = new StraightLine(new Axis(axis.getColor(),axis.getStartX(),axis.getStartY(),axis.getStartZ(),axis.getEndX(),axis.getEndY(),axis.getEndZ()),new Line(),offset);
        bodyList.add(straightLine);
        Platform.runLater(()-> drawingPane.getChildren().add(straightLine.getDrawingLine()));
        Platform.runLater(()-> update());
    }

    public void zoomPlus() {
        roomMatrix.zoom(1.5);
        update();
        roomMatrix.zoom(1);
    }

    public void zoomMinus() {
        roomMatrix.zoom(0.75);
        update();
        roomMatrix.zoom(1);
    }

    public void updateLast(double xPos, double yPos, double mouseX, double mouseY) {
        StraightLine straightLine = bodyList.get(bodyList.size()-1);
        straightLine.setViewAxis(straightLine.getBaseAxis().getStartX(),straightLine.getBaseAxis().getStartY(), 0,mouseX- offset.getxOffset(),mouseY- offset.getyOffset(),0);
        straightLine.updateAxis();
    }

    public void updateLastStrightLine(double xPos, double yPos, double mouseX, double mouseY) {
        StraightLine straightLine = bodyList.get(bodyList.size()-1);
        straightLine.setViewAxis(straightLine.getBaseAxis().getStartX(),straightLine.getBaseAxis().getStartY(), 0,mouseX- offset.getxOffset(),mouseY- offset.getyOffset(),0);
        straightLine.setBaseAxis(roomMatrix.drawInverseBody(straightLine.getViewAxis()));
        update();
    }

    public void moveYAxis(double distance) {
        offset.setyOffset(offset.getyOffset()+distance) ;
        update();
    }

    public void moveXAxis(double distance) {
        offset.setxOffset(offset.getxOffset()+distance) ;
        update();
    }

    public void deleteAll() {
        bodyList.stream().forEach(i -> i.delete());
        update();
        bodyList.clear();
        System.out.println("delete");
        update();
    }
}
