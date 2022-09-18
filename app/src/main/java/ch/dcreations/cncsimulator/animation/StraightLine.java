package ch.dcreations.cncsimulator.animation;

import javafx.scene.shape.Line;



public class StraightLine {
    private Vector baseVector;
    private  Line drawingLine;
    private Vector viewVector;
    private  Offset offset;

    public StraightLine(Vector baseVector, Line drawingLine, Offset offset) {
            this.offset = offset;
            this.baseVector = baseVector;
            this.drawingLine = drawingLine;
            this.viewVector =  new Vector(baseVector.getColor(), baseVector.getStartX(), baseVector.getStartY(), baseVector.getStartZ(), baseVector.getEndX(), baseVector.getEndY(), baseVector.getEndZ());
            updateAxis();
      }

    public Line getDrawingLine() {
        updateAxis();
        return drawingLine;
    }

    public void updateAxis(){
        drawingLine.setStartX(viewVector.getStartX()+offset.getxOffset());
        drawingLine.setStartY(viewVector.getStartY()+offset.getyOffset());
        drawingLine.setEndX(viewVector.getEndX()+offset.getxOffset());
        drawingLine.setEndY(viewVector.getEndY()+offset.getyOffset());
        drawingLine.setStroke(viewVector.getColor());
        drawingLine.setStrokeWidth(2);
    }

    public void setBaseAxis(double startX,double startY,double startZ,double endX,double endY,double endZ){
        baseVector.setStartX(startX);
        baseVector.setStartY(startY);
        baseVector.setStartZ(startZ);
        baseVector.setEndX(endX);
        baseVector.setEndY(endY);
        baseVector.setEndZ(endZ);
    }

    public Vector getViewAxis() {
        return viewVector;
    }

    public Vector getBaseAxis() {
        return baseVector;
    }

    public void setViewAxis(Vector drawBody) {
        viewVector.setStartX(drawBody.getStartX());
        viewVector.setStartY(drawBody.getStartY());
        viewVector.setStartZ(drawBody.getStartZ());
        viewVector.setEndX(drawBody.getEndX());
        viewVector.setEndY(drawBody.getEndY());
        viewVector.setEndZ(drawBody.getEndZ());
    }

    public void setViewAxis(double startX,double startY,double startZ,double endX,double endY,double endZ){
        viewVector.setStartX(startX);
        viewVector.setStartY(startY);
        viewVector.setStartZ(startZ);
        viewVector.setEndX(endX);
        viewVector.setEndY(endY);
        viewVector.setEndZ(endZ);
    }

    public void setBaseAxis(Vector drawBody) {
        baseVector.setStartX(drawBody.getStartX());
        baseVector.setStartY(drawBody.getStartY());
        baseVector.setStartZ(drawBody.getStartZ());
        baseVector.setEndX(drawBody.getEndX());
        baseVector.setEndY(drawBody.getEndY());
        baseVector.setEndZ(drawBody.getEndZ());
    }

    public void delete() {
        baseVector.setStartZ(0);
        baseVector.setStartX(0);
        baseVector.setStartY(0);
        baseVector.setEndX(0);
        baseVector.setEndY(0);
        baseVector.setEndZ(0);
        viewVector.setStartZ(0);
        viewVector.setStartX(0);
        viewVector.setStartY(0);
        viewVector.setEndX(0);
        viewVector.setEndY(0);
        viewVector.setEndZ(0);
        drawingLine.setStrokeWidth(0);
        drawingLine.setStartX(0);
        drawingLine.setEndY(0);
        drawingLine.setEndX(0);
        drawingLine.setStartY(0);
    }
}
