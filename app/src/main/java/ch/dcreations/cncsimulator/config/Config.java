package ch.dcreations.cncsimulator.config;

import ch.dcreations.cncsimulator.cncControl.*;
import ch.dcreations.cncsimulator.cncControl.Canal.*;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.CNCAxis;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.CNCSpindle;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.SpindelNames;
import ch.dcreations.cncsimulator.cncControl.GCodes.SpindelRotationOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    public final static CNCProgram CANAL1_SAMPLE_PROGRAM = new CNCProgram("O0001;\nG98 G97;\nM3 S1000 F1.0;\nG1 G17 X2.0 F1000.0;\nG2 X40.0  R19.0;\nG2 X2.0 Z10.0 R19.0;\nG2 X40.0  R19.0;\nG2 X2.0 R19.0;");
    public final static CNCProgram CANAL2_SAMPLE_PROGRAM = new CNCProgram("O1001;\nG98 G97;\nM3 S1000 F1.0;\nG1 X2.0 Y4.8 Z9 F1000.0;\nG1 X15.0 Y4.0");

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
