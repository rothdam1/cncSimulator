package ch.dcreations.cncsimulator.animation;


public interface CNCAnimation {


    /**
     *  ObjectSize
     * @param i multiplicator
     */
    public void scale(double i);


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
