package ch.dcreations.cncsimulator.animation;

import java.util.ArrayList;
import java.util.List;

public class RoomMatrix {


    private double viewMatrix[][] = new double[3][3];
    private double standardMatrix[][] = new double[3][3];

    public RoomMatrix() {
        MatrixStandard();
    }

    public void rotationWinkelZ(double winkel) {
        viewMatrix[0][0] = Math.cos(Math.toRadians(winkel));
        viewMatrix[0][1] = Math.sin(Math.toRadians(winkel)) * -1;
        viewMatrix[0][2] = 0;
        viewMatrix[1][0] = Math.sin(Math.toRadians(winkel));
        viewMatrix[1][1] = Math.cos(Math.toRadians(winkel));
        viewMatrix[1][2] = 0;
        viewMatrix[2][0] = 0;
        viewMatrix[2][1] = 0;
        viewMatrix[2][2] = 1;
    }


    public void rotationWinkelX(double winkel) {
        viewMatrix[0][0] = 1;
        viewMatrix[0][1] = 0;
        viewMatrix[0][2] = 0;
        viewMatrix[1][0] = 0;
        viewMatrix[1][1] = Math.cos(Math.toRadians(winkel));
        viewMatrix[1][2] = Math.sin(Math.toRadians(winkel)) * -1;
        viewMatrix[2][0] = 0;
        viewMatrix[2][1] = Math.sin(Math.toRadians(winkel));
        viewMatrix[2][2] = Math.cos(Math.toRadians(winkel));
    }

    public void rotationWinkelY(double winkel) {

        viewMatrix[0][0] = Math.cos(Math.toRadians(winkel));
        viewMatrix[0][1] = 0;
        viewMatrix[0][2] = Math.sin(Math.toRadians(winkel));
        viewMatrix[1][0] = 0;
        viewMatrix[1][1] = 1;
        viewMatrix[1][2] = 0;
        viewMatrix[2][0] = Math.sin(Math.toRadians(winkel)) * -1;
        viewMatrix[2][1] = 0;
        viewMatrix[2][2] = Math.cos(Math.toRadians(winkel));
    }



    public void zoom(double factor) {
        viewMatrix[0][0] = factor;
        viewMatrix[0][1] = 0;
        viewMatrix[0][2] = 0;
        viewMatrix[1][0] = 0;
        viewMatrix[1][1] = factor;
        viewMatrix[1][2] = 0;
        viewMatrix[2][0] = 0;
        viewMatrix[2][1] = 0;
        viewMatrix[2][2] = factor;

    }

    public Axis drawBody(Axis axis) {
        return getAxis(axis, viewMatrix);
    }


    private Axis getAxis(Axis axis, double matrix[][]) {
        double transformStartX = axis.getStartX() * matrix[0][0] + axis.getStartY() * matrix[0][1] + axis.getStartZ() * matrix[0][2];
        double transformStartY = axis.getStartX() * matrix[1][0] + axis.getStartY() * matrix[1][1] + axis.getStartZ() * matrix[1][2];
        double transformStartZ = axis.getStartX() * matrix[2][0] + axis.getStartY() * matrix[2][1] + axis.getStartZ() * matrix[2][2];
        double transformEndX = axis.getEndX() * matrix[0][0] + axis.getEndY() * matrix[0][1] + axis.getEndZ() * matrix[0][2];
        double transformEndY = axis.getEndX() * matrix[1][0] + axis.getEndY() * matrix[1][1] + axis.getEndZ() * matrix[1][2];
        double transformEndZ = axis.getEndX() * matrix[2][0] + axis.getEndY() * matrix[2][1] + axis.getEndZ() * matrix[2][2];
        Axis ret = new Axis(axis.getColor(), transformStartX, transformStartY, transformStartZ, transformEndX, transformEndY, transformEndZ);
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(" ").append(viewMatrix[j][i]).append(" ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public void MatrixStandard() {
        MatrixStandard(viewMatrix);
        MatrixStandard(standardMatrix);
    }

    private void MatrixStandard(double[][] invMatrix) {
        invMatrix[0][0] = 1;
        invMatrix[0][1] = 0;
        invMatrix[0][2] = 0;
        invMatrix[1][0] = 0;
        invMatrix[1][1] = 1;
        invMatrix[1][2] = 0;
        invMatrix[2][0] = 0;
        invMatrix[2][1] = 0;
        invMatrix[2][2] = 1;
    }

    private double[][] matrixMultiplication(double[][] a,double[][] b){
        int lenght = a.length;
        double result[][] = new double[lenght][lenght];
        for (int i= 0;i<lenght;i++){
            for (int j= 0;j<lenght;j++){
                double value = 0;
                for (int k= 0;k<lenght;k++){
                    value = value +( a[k][j] * b[i][k]);
                }
                result[i][j] =value;
            }
        }
        return result;
    }

    void printMatrix(double[][] a){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                sb.append(" ").append(a[j][i]).append(" ");
            }
            sb.append('\n');
        }
        System.out.println(sb);
    }
}
