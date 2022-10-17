package ch.dcreations.cncsimulator.animation;

public class RoomMatrix {
    private double[][] addMatrix = new double[3][3];

    private double[][] standardMatrix = new double[3][3];



    public RoomMatrix() {
        resetMatrix();
    }


    public void rotationWinkelZ(double winkel) {
        addMatrix[0][0] = Math.cos(Math.toRadians(winkel));
        addMatrix[0][1] = Math.sin(Math.toRadians(winkel)) * -1;
        addMatrix[0][2] = 0;
        addMatrix[1][0] = Math.sin(Math.toRadians(winkel));
        addMatrix[1][1] = Math.cos(Math.toRadians(winkel));
        addMatrix[1][2] = 0;
        addMatrix[2][0] = 0;
        addMatrix[2][1] = 0;
        addMatrix[2][2] = 1;
        matrixMultiplication(addMatrix);
    }




    public void rotationWinkelX(double winkel) {
        addMatrix[0][0] = 1;
        addMatrix[0][1] = 0;
        addMatrix[0][2] = 0;
        addMatrix[1][0] = 0;
        addMatrix[1][1] = Math.cos(Math.toRadians(winkel));
        addMatrix[1][2] = Math.sin(Math.toRadians(winkel)) * -1;
        addMatrix[2][0] = 0;
        addMatrix[2][1] = Math.sin(Math.toRadians(winkel));
        addMatrix[2][2] = Math.cos(Math.toRadians(winkel));
        matrixMultiplication(addMatrix);
    }

    public void rotationWinkelY(double winkel) {
        addMatrix[0][0] = Math.cos(Math.toRadians(winkel));
        addMatrix[0][1] = 0;
        addMatrix[0][2] = Math.sin(Math.toRadians(winkel));
        addMatrix[1][0] = 0;
        addMatrix[1][1] = 1;
        addMatrix[1][2] = 0;
        addMatrix[2][0] = Math.sin(Math.toRadians(winkel)) * -1;
        addMatrix[2][1] = 0;
        addMatrix[2][2] = Math.cos(Math.toRadians(winkel));
        matrixMultiplication(addMatrix);
    }



    public void zoom(double factor) {
        addMatrix[0][0] = factor;
        addMatrix[0][1] = 0;
        addMatrix[0][2] = 0;
        addMatrix[1][0] = 0;
        addMatrix[1][1] = factor;
        addMatrix[1][2] = 0;
        addMatrix[2][0] = 0;
        addMatrix[2][1] = 0;
        addMatrix[2][2] = factor;
        matrixMultiplication(addMatrix);
    }


    public Vector drawBody(Vector vector) {
        return getAxis(vector, addMatrix);
    }

    public Vector drawAngle(Vector vector) {
        return getAxis(vector, standardMatrix);
    }


    private Vector getAxis(Vector vector, double[][] matrix) {
        double transformStartX = vector.getStartX() * matrix[0][0] + vector.getStartY() * matrix[0][1] + vector.getStartZ() * matrix[0][2];
        double transformStartY = vector.getStartX() * matrix[1][0] + vector.getStartY() * matrix[1][1] + vector.getStartZ() * matrix[1][2];
        double transformStartZ = vector.getStartX() * matrix[2][0] + vector.getStartY() * matrix[2][1] + vector.getStartZ() * matrix[2][2];
        double transformEndX = vector.getEndX() * matrix[0][0] + vector.getEndY() * matrix[0][1] + vector.getEndZ() * matrix[0][2];
        double transformEndY = vector.getEndX() * matrix[1][0] + vector.getEndY() * matrix[1][1] + vector.getEndZ() * matrix[1][2];
        double transformEndZ = vector.getEndX() * matrix[2][0] + vector.getEndY() * matrix[2][1] + vector.getEndZ() * matrix[2][2];
        return new Vector(vector.getColor(), transformStartX, transformStartY, transformStartZ, transformEndX, transformEndY, transformEndZ);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append(" ").append(addMatrix[j][i]).append(" ");
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public void resetMatrix() {
        resetMatrix(addMatrix);
        resetMatrix(standardMatrix);
    }

    private void resetMatrix(double[][] invMatrix) {
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

    private void matrixMultiplication(double[][] b){
        int length = standardMatrix.length;
        double[][] result = new double[length][length];
        for (int i= 0;i<length;i++){
            for (int j= 0;j<length;j++){
                double value = 0;
                for (int k= 0;k<length;k++){
                    value = value +( standardMatrix[k][j] * b[i][k]);
                }
                result[i][j] =value;
            }
        }
        standardMatrix = result;
        return;
    }

    private double[][] matrixMultiplication(double[][] a,double[][] b){
        int length = a.length;
        double[][] result = new double[length][length];
        for (int i= 0;i<length;i++){
            for (int j= 0;j<length;j++){
                double value = 0;
                for (int k= 0;k<length;k++){
                    value = value +( a[k][j] * b[i][k]);
                }
                result[i][j] =value;
            }
        }
        return result;
    }


}
