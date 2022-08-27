package ch.dcreations.cncsimulator.cncControl.Canal;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.GCodes.GCode;
import ch.dcreations.cncsimulator.cncControl.PLC.MCodes;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * <p>
 *  IS a Single Line Comand of the CNC ISO CODE Like G1 X3.0 Z3.0
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */


public class CNCProgramCommand {
    private List<GCode> gCodes;
    private MCodes mCode;
    private Map<AxisName,Double> axisDistance;
    private Map<Character,Double> additionParameters;

    public CNCProgramCommand(List<GCode> gCodes, MCodes mCode, Map<AxisName, Double> axisDistance, Map<Character,Double> additionParameters) {
        this.gCodes = gCodes;
        this.mCode = mCode;
        this.axisDistance = axisDistance;
        this.additionParameters = additionParameters;
    }

    public List<GCode> getgCodes() {
        return gCodes;
    }

    public MCodes getCode() {
        return mCode;
    }

    public Map<AxisName, Double> getAxisDistance() {
        return axisDistance;
    }

    public Map<Character, Double> getAdditionParameters() {
        return additionParameters;
    }
}

