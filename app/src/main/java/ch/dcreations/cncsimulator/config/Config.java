package ch.dcreations.cncsimulator.config;

import ch.dcreations.cncsimulator.cncControl.AxisName;
import ch.dcreations.cncsimulator.cncControl.CNCAxis;
import ch.dcreations.cncsimulator.cncControl.CNCProgram;
import ch.dcreations.cncsimulator.cncControl.Canal;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public final static CNCProgram CANAL1_SAMPLE_PROGRAM = new CNCProgram("O0001;\nG98 G97;\nM3 S1000 F1.0;\nG1 X2.0 Y4.8 Z9 F100.0;\nG1 X3.0 Y4.0");
    public final static CNCProgram CANAL2_SAMPLE_PROGRAM = new CNCProgram("O1001;\nG98 G97;\nM3 S1000 F1.0;\nG1 X2.0 Y4.8 Z9 F100.0;\nG1 X3.0 Y4.0");

    public final static int POSITION_CALCULATION_RESOLUTION = 10;

    public static List<CNCAxis> GET_CNC_AXIS_CANAL1(){
        List<CNCAxis> cncAxes = new ArrayList<>();
        cncAxes.add(new CNCAxis(AxisName.X));
        cncAxes.add(new CNCAxis(AxisName.Y));
        cncAxes.add(new CNCAxis(AxisName.Z));
        cncAxes.add(new CNCAxis(AxisName.C));
        return cncAxes;
    }

    public static List<CNCAxis> GET_CNC_AXIS_CANAL2(){
        List<CNCAxis> cncAxes = new ArrayList<>();
        cncAxes.add(new CNCAxis(AxisName.X));
        cncAxes.add(new CNCAxis(AxisName.Y));
        cncAxes.add(new CNCAxis(AxisName.Z));
        cncAxes.add(new CNCAxis(AxisName.C));
        return cncAxes;
    }

    public static List<Canal> GET_CNC_CANALS() {
        List<Canal> cncCanals = new ArrayList<>();
        cncCanals.add(new Canal(GET_CNC_AXIS_CANAL1()));
        cncCanals.add(new Canal(GET_CNC_AXIS_CANAL2()));
        return cncCanals;
    }
}
