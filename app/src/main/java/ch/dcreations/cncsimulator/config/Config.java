package ch.dcreations.cncsimulator.config;

import ch.dcreations.cncsimulator.cncControl.AxisName;
import ch.dcreations.cncsimulator.cncControl.CNCAxis;
import ch.dcreations.cncsimulator.cncControl.CNCProgram;
import ch.dcreations.cncsimulator.cncControl.Canal;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public final static CNCProgram CANAL1_SAMPLE_PROGRAM = new CNCProgram("O0001;\n<b>G1 G40<\\b>");
    public final static CNCProgram CANAL2_SAMPLE_PROGRAM = new CNCProgram("O1001;\nG1 G40");

    public static List<CNCAxis> GET_CNC_AXIS_CANAL1(){
        List<CNCAxis> cncAxes = new ArrayList<>();
        cncAxes.add(new CNCAxis(AxisName.X1));
        cncAxes.add(new CNCAxis(AxisName.Y1));
        cncAxes.add(new CNCAxis(AxisName.Z1));
        cncAxes.add(new CNCAxis(AxisName.C1));
        return cncAxes;
    }

    public static List<CNCAxis> GET_CNC_AXIS_CANAL2(){
        List<CNCAxis> cncAxes = new ArrayList<>();
        cncAxes.add(new CNCAxis(AxisName.X2));
        cncAxes.add(new CNCAxis(AxisName.Y2));
        cncAxes.add(new CNCAxis(AxisName.Z2));
        cncAxes.add(new CNCAxis(AxisName.C2));
        return cncAxes;
    }

    public static List<Canal> GET_CNC_CANALS() {
        List<Canal> cncCanals = new ArrayList<>();
        cncCanals.add(new Canal(GET_CNC_AXIS_CANAL1()));
        cncCanals.add(new Canal(GET_CNC_AXIS_CANAL2()));
        return cncCanals;
    }
}
