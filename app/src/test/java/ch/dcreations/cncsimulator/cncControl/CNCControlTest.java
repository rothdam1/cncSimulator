package ch.dcreations.cncsimulator.cncControl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
/* TESTS
TESTCASES 1
    TEST RUN FUNCTION
        1_1 CHECK THAT THE AXIS ADDED GET WEN USE FUNCTION GET CNC AXIS
 */



class CNCControlTest {

    CNCControl cncControl ;
    @BeforeEach
    void setUp() {
        cncControl = new CNCControl(GET_CNC_CANALS());
    }

    @Test
    void getCncAxes() {
        //1_1 CHECK THAT THE AXIS ADDED GET WEN USE FUNCTION GET CNC AXIS
        cncControl.getCncAxes();
        List<CNCAxis> cncAxesExpected  = new ArrayList<>();
        cncAxesExpected.addAll(GET_CNC_AXIS_CANAL1());
        cncAxesExpected.addAll(GET_CNC_AXIS_CANAL2());
        for (int i = 0  ; i<cncControl.getCncAxes().size();i++){
            assertEquals(cncControl.getCncAxes().get(i).getAxisName(),cncAxesExpected.get(i).getAxisName());
        }
    }

    @Test
    void setCanal1CNCProgramText() {
    }

    @Test
    void setCanal2CNCProgramText() {
    }

    @Test
    void getCanal1CNCProgramText() {
    }

    @Test
    void getCanal2CNCProgramText() {
    }

    @Test
    void stopCNCProgram() {
    }

    @Test
    void runCNCProgram() {
    }

    @Test
    void goToNextStepCNCProgram() {
    }

    @Test
    void terminateCNCControl() {
    }

    @Test
    void getCanalLinePositionAsObservables() {
    }


    private static List<CNCAxis> GET_CNC_AXIS_CANAL1(){
        List<CNCAxis> cncAxes = new ArrayList<>();
        cncAxes.add(new CNCAxis(AxisName.X));
        cncAxes.add(new CNCAxis(AxisName.Y));
        cncAxes.add(new CNCAxis(AxisName.Z));
        cncAxes.add(new CNCAxis(AxisName.C));
        return cncAxes;
    }

    private static List<CNCAxis> GET_CNC_AXIS_CANAL2(){
        List<CNCAxis> cncAxes = new ArrayList<>();
        cncAxes.add(new CNCAxis(AxisName.X));
        cncAxes.add(new CNCAxis(AxisName.Y));
        cncAxes.add(new CNCAxis(AxisName.Z));
        cncAxes.add(new CNCAxis(AxisName.C));
        return cncAxes;
    }

    private static List<Canal> GET_CNC_CANALS() {
        List<Canal> cncCanals = new ArrayList<>();
        cncCanals.add(new Canal(GET_CNC_AXIS_CANAL1()));
        cncCanals.add(new Canal(GET_CNC_AXIS_CANAL2()));
        return cncCanals;
    }
}