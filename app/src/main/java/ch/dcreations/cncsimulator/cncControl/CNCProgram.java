package ch.dcreations.cncsimulator.cncControl;

public class CNCProgram {
    String programText;

    public CNCProgram(String programText) {
        this.programText = programText;
    }

    public String getProgramText() {
        return programText;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
