package ch.dcreations.cncsimulator.animation;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;



public class StraightLine {
    private  Axis baseAxis;
    private  Line drawingLine;
    private  Axis viewAxis;
    private  Offset offset;

    public StraightLine(Axis baseAxis, Line drawingLine,  Offset offset) {
            this.offset = offset;
            this.baseAxis = baseAxis;
            this.drawingLine = drawingLine;
            this.viewAxis = new Axis(baseAxis.getColor(),baseAxis.getStartX(), baseAxis.getStartY(), baseAxis.getStartZ(), baseAxis.getEndX(), baseAxis.getEndY(), baseAxis.getEndZ());
            updateAxis();
      }

    public Line getDrawingLine() {
        return drawingLine;
    }

    public void updateAxis(){
        drawingLine.setStartX(viewAxis.getStartX()+offset.getxOffset());
        drawingLine.setStartY(viewAxis.getStartY()+offset.getyOffset());
        drawingLine.setEndX(viewAxis.getEndX()+offset.getxOffset());
        drawingLine.setEndY(viewAxis.getEndY()+offset.getyOffset());
        drawingLine.setStroke(viewAxis.getColor());
        drawingLine.setStrokeWidth(2);
    }

    public void setBaseAxis(double startX,double startY,double startZ,double endX,double endY,double endZ){
        baseAxis.setStartX(startX);
        baseAxis.setStartY(startY);
        baseAxis.setStartZ(startZ);
        baseAxis.setEndX(endX);
        baseAxis.setEndY(endY);
        baseAxis.setEndZ(endZ);
    }

    public Axis getViewAxis() {
        return viewAxis;
    }

    public Axis getBaseAxis() {
        return baseAxis;
    }

    public void setViewAxis(Axis drawBody) {
        viewAxis.setStartX(drawBody.getStartX());
        viewAxis.setStartY(drawBody.getStartY());
        viewAxis.setStartZ(drawBody.getStartZ());
        viewAxis.setEndX(drawBody.getEndX());
        viewAxis.setEndY(drawBody.getEndY());
        viewAxis.setEndZ(drawBody.getEndZ());
    }

    public void setViewAxis(double startX,double startY,double startZ,double endX,double endY,double endZ){
        viewAxis.setStartX(startX);
        viewAxis.setStartY(startY);
        viewAxis.setStartZ(startZ);
        viewAxis.setEndX(endX);
        viewAxis.setEndY(endY);
        viewAxis.setEndZ(endZ);
    }

    public void setBaseAxis(Axis drawBody) {
        baseAxis.setStartX(drawBody.getStartX());
        baseAxis.setStartY(drawBody.getStartY());
        baseAxis.setStartZ(drawBody.getStartZ());
        baseAxis.setEndX(drawBody.getEndX());
        baseAxis.setEndY(drawBody.getEndY());
        baseAxis.setEndZ(drawBody.getEndZ());
    }

    public void delete() {
        baseAxis.setStartZ(0);
        baseAxis.setStartX(0);
        baseAxis.setStartY(0);
        baseAxis.setEndX(0);
        baseAxis.setEndY(0);
        baseAxis.setEndZ(0);
        viewAxis.setStartZ(0);
        viewAxis.setStartX(0);
        viewAxis.setStartY(0);
        viewAxis.setEndX(0);
        viewAxis.setEndY(0);
        viewAxis.setEndZ(0);
        drawingLine.setStrokeWidth(0);
        drawingLine.setStartX(0);
        drawingLine.setEndY(0);
        drawingLine.setEndX(0);
        drawingLine.setStartY(0);
    }
}
