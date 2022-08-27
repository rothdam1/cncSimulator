package ch.dcreations.cncsimulator.gui;

import ch.dcreations.cncsimulator.cncControl.AxisName;
import ch.dcreations.cncsimulator.cncControl.CNCAxis;
import ch.dcreations.cncsimulator.cncControl.CNCProgram;
import ch.dcreations.cncsimulator.cncControl.Canal;
import ch.dcreations.cncsimulator.config.Config;

import java.util.ArrayList;
import java.util.List;

public class TestConfig extends Config {

    public final static CNCProgram CANAL1_SAMPLE_PROGRAM = new CNCProgram("O0001;\nG1 G40;\nG1 X2.0 Y4.8 Z9;");
    public final static CNCProgram CANAL2_SAMPLE_PROGRAM = new CNCProgram("O1001;\nG1 G40;\n G1 X4;\n G3 X2 R5");

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
        cncCanals.add(new Canal(GET_CNC_AXIS_CANAL1(), cncSpindles));
        cncCanals.add(new Canal(GET_CNC_AXIS_CANAL2(), cncSpindles));
        return cncCanals;
    }
}
