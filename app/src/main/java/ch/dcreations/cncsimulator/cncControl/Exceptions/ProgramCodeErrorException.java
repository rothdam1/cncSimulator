package ch.dcreations.cncsimulator.cncControl.Exceptions;

public class ProgramCodeErrorException extends Exception{

    int progLinePos;
    public ProgramCodeErrorException(String message,int progLinePos) {
        super(message);
        this.progLinePos = progLinePos;
    }
}
