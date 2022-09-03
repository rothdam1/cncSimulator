package ch.dcreations.cncsimulator;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.CNCAxis;
import ch.dcreations.cncsimulator.cncControl.CNCProgram;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.CNCSpindle;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.SpindelNames;
import ch.dcreations.cncsimulator.cncControl.Canal.Canal;
import ch.dcreations.cncsimulator.cncControl.GCodes.SpindelRotationOption;
import ch.dcreations.cncsimulator.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestConfig extends Config {

    public final static CNCProgram CANAL1_SAMPLE_PROGRAM = new CNCProgram("O0001;\nG98 G97;\nM3 S1000 F1000.0;\nG1 X2.0 Y4.8 Z9 F100000.0;\nG1 X3.0 Y4.0");
    public final static CNCProgram CANAL2_SAMPLE_PROGRAM = new CNCProgram("O1001;\nG98 G97;\nM3 S1000 F1000.0;\nG1 X2.0 Y4.8 Z9 F1000.0;\nG1 X3.0 Y4.0");

    public final static int POSITION_CALCULATION_RESOLUTION = 100;

    public static String END_OF_PROGRAM_SIMBOLE = "%";

    public static Map<AxisName, CNCAxis> GET_CNC_AXIS_CANAL1(){
        Map<AxisName,CNCAxis> cncAxes = new HashMap<>();
        cncAxes.put(AxisName.X,new CNCAxis());
        cncAxes.put(AxisName.Y,new CNCAxis());
        cncAxes.put(AxisName.Z,new CNCAxis());
        cncAxes.put(AxisName.C,new CNCAxis());
        return cncAxes;
    }

    public static Map<SpindelNames, CNCSpindle> GET_CNC_SPINDLES_CANAL1(){
        Map<SpindelNames,CNCSpindle> spindles = new HashMap<>();
        spindles.put(SpindelNames.S1,new CNCSpindle(SpindelRotationOption.CONSTANT_ROTATION));
        spindles.put(SpindelNames.S11,new CNCSpindle(SpindelRotationOption.CONSTANT_ROTATION));
        return spindles;
    }

    public static Map<SpindelNames,CNCSpindle> GET_CNC_SPINDLES_CANAL2(){
        Map<SpindelNames,CNCSpindle> spindles = new HashMap<>();
        spindles.put(SpindelNames.S1,new CNCSpindle(SpindelRotationOption.CONSTANT_ROTATION));
        spindles.put(SpindelNames.S11,new CNCSpindle(SpindelRotationOption.CONSTANT_ROTATION));
        return spindles;
    }

    public static Map<AxisName,CNCAxis>  GET_CNC_AXIS_CANAL2(){
        Map<AxisName,CNCAxis> cncAxes = new HashMap<>();
        cncAxes.put(AxisName.X,new CNCAxis());
        cncAxes.put(AxisName.Y,new CNCAxis());
        cncAxes.put(AxisName.Z,new CNCAxis());
        cncAxes.put(AxisName.C,new CNCAxis());
        return cncAxes;
    }

    public static List<Canal> GET_CNC_CANALS() {
        List<Canal> cncCanals = new ArrayList<>();
        cncCanals.add(new Canal(GET_CNC_AXIS_CANAL1(),GET_CNC_SPINDLES_CANAL1()));
        cncCanals.add(new Canal(GET_CNC_AXIS_CANAL2(),GET_CNC_SPINDLES_CANAL2()));
        return cncCanals;
    }
}
