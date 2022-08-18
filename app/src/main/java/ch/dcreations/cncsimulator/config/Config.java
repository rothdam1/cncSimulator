package ch.dcreations.cncsimulator.config;

import ch.dcreations.cncsimulator.cncControl.CNCProgram;

public class Config {

    public final static CNCProgram CANAL1_SAMPLE_PROGRAM = new CNCProgram("O0001;\nG1 G40");
    public final static CNCProgram CANAL2_SAMPLE_PROGRAM = new CNCProgram("O1001;\nG1 G40");
}
