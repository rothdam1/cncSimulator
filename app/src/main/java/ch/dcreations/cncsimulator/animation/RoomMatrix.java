package ch.dcreations.cncsimulator.animation;

import java.util.ArrayList;
import java.util.List;

public class RoomMatrix {


    private double viewMatrix[][] = new double[3][3];
    private double invMatrix[][] = new double[3][3];
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
        addInverse();
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
        addInverse();
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
        addInverse();
    }

    private void addInverse() {
        invMatrix = matrixMultiplication(invMatrix,viewMatrix);
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
        addInverse();
    }

    public Axis drawBody(Axis axis) {
        return getAxis(axis, viewMatrix);
    }

    public Axis drawInverseBody(Axis axis) {
        return getAxis(axis, inverseTheMatrix());
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

    public double[][] inverseTheMatrix() {
        //setup for gaus
        /*
        x1 y1 z1 1 0 0
        x2 y2 z2 0 1 0
        x3 y3 z3 0 0 1
         */
        List<Integer> doneLines = new ArrayList<>();
        double inverse[][] = new double[6][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                inverse[i][j] = invMatrix[i][j];
            }
        }
        //StandartSystem
        inverse[3][0] = 1.0;
        inverse[4][1] = 1.0;
        inverse[5][2] = 1.0;
        int x;
        int y;
        do {
            y = 0;
            x = 0;
            boolean found = false;
            for (; x < 3; x++) {
                y = 0;
                for (; y < 3; y++) {
                    if (inverse[x][y] != 0 && !doneLines.contains(y)) {
                        found = true;
                        break;
                    }
                }
                if (found == true) {
                    break;
                }
            }
            if (x == 3)break;
            doneLines.add(y);
            double divisor = inverse[x][y];
            if (divisor != 1 && divisor != 0) {
                for (int i = 0; i < 6; i++) {
                    if (inverse[i][y] != 0) {
                        inverse[i][y] = (inverse[i][y] / divisor);
                    }
                }
            }
            for (int i = 0; i < 3; i++) {
                if (i != y && inverse[x][i] != 0) {
                    double multiplication = inverse[x][i];
                    for (int j = 0; j < 6; j++) {
                        inverse[j][i] = inverse[j][i] - (inverse[j][y] * multiplication);
                    }
                }
            }
        } while (x < 2);
        //write inverse to front
        for (int i = 0; i<3;i++ ){
            for (int j = 0; j<3;j++ ){
                inverse[i][j] = inverse[3+i][j];
            }

        }
        return inverse;
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
        MatrixStandard(invMatrix);
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
