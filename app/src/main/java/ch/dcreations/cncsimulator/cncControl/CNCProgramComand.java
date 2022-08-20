package ch.dcreations.cncsimulator.cncControl;

import ch.dcreations.cncsimulator.cncControl.GCodes.GCode;
import ch.dcreations.cncsimulator.cncControl.PLC.MCodes;

import java.util.List;
import java.util.Map;

public class CNCProgramComand {
    List<GCode> gCodes;
    MCodes mCode;
    Map<AxisName,Double> axisDistance;
    List<String> additionParameters;

    public CNCProgramComand(List<GCode> gCodes, MCodes mCode, Map<AxisName, Double> axisDistance, List<String> additionParameters) {
        this.gCodes = gCodes;
        this.mCode = mCode;
        this.axisDistance = axisDistance;
        this.additionParameters = additionParameters;
    }
}

