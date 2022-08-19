package ch.dcreations.cncsimulator.config;

import ch.dcreations.cncsimulator.cncControl.AxisName;
import ch.dcreations.cncsimulator.cncControl.CNCAxis;
import ch.dcreations.cncsimulator.cncControl.CNCProgram;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public final static CNCProgram CANAL1_SAMPLE_PROGRAM = new CNCProgram("O0001;\nG1 G40");
    public final static CNCProgram CANAL2_SAMPLE_PROGRAM = new CNCProgram("O1001;\nG1 G40");

    public final static List<CNCAxis> GET_CNC_AXIS(){
        List<CNCAxis> cncAxes = new ArrayList<>();
        cncAxes.add(new CNCAxis(AxisName.X1));
        cncAxes.add(new CNCAxis(AxisName.Y1));
        cncAxes.add(new CNCAxis(AxisName.Z1));
        cncAxes.add(new CNCAxis(AxisName.X2));
        cncAxes.add(new CNCAxis(AxisName.Y2));
        cncAxes.add(new CNCAxis(AxisName.Z2));
        cncAxes.add(new CNCAxis(AxisName.C1));
        cncAxes.add(new CNCAxis(AxisName.C2));
        return cncAxes;
    }
}
