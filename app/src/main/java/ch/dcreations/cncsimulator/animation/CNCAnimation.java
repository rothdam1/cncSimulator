package ch.dcreations.cncsimulator.animation;


public interface CNCAnimation {


    public void setOffset(double x, double y ,double z);


    public void update();


    public void rotateXAxis(double degree);

    public void rotateYAxis(double degree);

    public void rotateZAxis(double degree);


    public void resetView(double with,double height) ;

    public void createNewLine(Vector vector);

    public void zoomPlus() ;

    public void zoomMinus();


    public void moveYAxis(double distance);

    public void moveXAxis(double distance);

    public void deleteAll();
}
