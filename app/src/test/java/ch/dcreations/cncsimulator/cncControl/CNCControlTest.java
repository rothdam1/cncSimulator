package ch.dcreations.cncsimulator.cncControl;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.CNCAxis;
import ch.dcreations.cncsimulator.cncControl.Canal.Canal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
/**
 * <p>
 * <p>
 *  TEST FOR THE CNCControl class
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */
/* TESTS
TESTCASES 1
    TEST RUN FUNCTION
        1_1 CHECK THAT THE AXIS ADDED GET WEN USE FUNCTION GET CNC AXIS
TESTCASES 2
    TEST READ AND WRITE PROGRAM TO CANAL 1
        2_1 Check that Program is written in the CNC Control if the Canal exist and don't make an exception
        2_2 Check that Program is written make a IOException if canal 1 does not exist
TESTCASES 3
    TEST READ AND WRITE PROGRAM TO CANAL 2
        3_1 Check that Program is written in the CNC Control if the Canal exist and don't make an exception
        3_2 Check that Program is written make a IOException if canal 1 does not exist
TESTCASES 4
    TEST CNC run a Stop Function
        4_1 TEST if run set CNCState to run
        4_2 TEST if stop set CNCState to Stop and does not throw an Exception
TESTCASES 5
    TEST CNC run correct one Step without alarm
        5_1 check that at start is at line 0
        5_2 check that after goToNExtStepCNC  is at line 1
TESTCASES 6
    TEST CNC terminate correct
        6_1 Check that function work without alarm
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
            assertEquals(cncControl.getCncAxes().get(i).getAxisName(),cncAxesExpected.get(i).getAxisName(),"CNCControl Test -> 1_1 CHECK THAT THE AXIS ADDED GET WEN USE FUNCTION GET CNC AXIS");
        }
    }



    @Test
    void setAndGetCanal1CNCProgramText() {
        //2_1 Check that Program is written in the CNC Control if the Canal exist and don't make an exception
        assertDoesNotThrow(() -> cncControl.setCanal1CNCProgramText(sampleProgram),"CNCControl Test ->2_1 Check that Program is written in the CNC Control if the Canal exist and don't make an exception");
        assertDoesNotThrow(() -> assertEquals(sampleProgram,cncControl.getCanal1CNCProgramText()),"CNCControl Test ->2_1 Check that Program is written in the CNC Control if the Canal exist and don't make an exception");
        //2_2 Check that Program is written make a IOException if canal 1 does not exist
        CNCControl cncControlWithZeroCanals = new CNCControl(new ArrayList<>());
        assertThrows(IOException.class,() -> cncControlWithZeroCanals.setCanal1CNCProgramText(sampleProgram),"2_2 Check that Program is written make a IOException if canal 1 does not exist");
        assertThrows(IOException.class,() -> assertEquals(sampleProgram,cncControlWithZeroCanals.getCanal1CNCProgramText()),"2_2 Check that Program is written make a IOException if canal 1 does not exist");
    }

    @Test
    void setAndGetCanal2CNCProgramText() {
        //3_1 Check that Program is written in the CNC Control if the Canal exist and don't make an exception
        assertDoesNotThrow(() -> cncControl.setCanal2CNCProgramText(sampleProgram),"CNCControl Test -> 3_1 Check that Program is written in the CNC Control if the Canal exist and don't make a exception");
        assertDoesNotThrow(() -> assertEquals(sampleProgram,cncControl.getCanal2CNCProgramText()),"CNCControl Test -> 3_1 Check that Program is written in the CNC Control if the Canal exist and don't make a exception");
        //3_2 Check that Program is written make a IOException if canal 1 does not exist
        CNCControl cncControlWithZeroCanals = new CNCControl(new ArrayList<>());
        assertThrows(IOException.class,() -> cncControlWithZeroCanals.setCanal2CNCProgramText(sampleProgram),"CNCControl Test -> 3_2 Check that Program is written make a IOException if canal 1 does not exist");
        assertThrows(IOException.class,() -> assertEquals(sampleProgram,cncControlWithZeroCanals.getCanal2CNCProgramText()),"CNCControl Test -> 3_2 Check that Program is written make a IOException if canal 1 does not exist");

    }


    @Test
    void RunAndStopCNCProgram() {
        //4_1 TEST if run set CNCState to run
        cncControl.runCNCProgram();
        assertEquals(CNCState.RUN,cncControl.getCncRunState(),"CNCControl Test -> TEST 4_1  TEST if run set CNCState to ru ");
        //4_2 TEST if stop set CNCState to Stop and does not throw an Exception
        assertDoesNotThrow(() -> cncControl.stopCNCProgram());
        assertEquals(CNCState.STOP,cncControl.getCncRunState(),"CNCControl Test -> TEST 4_2 TEST if stop set CNCState to Stop and does not throw an Exception ");
    }


    @Test
    void goToNextStepCNCProgram() {
        assertDoesNotThrow(()->cncControl.setCanal1CNCProgramText(sampleProgram),"CNCControl Test -> 5_1 check that at start is at line 0");
        assertDoesNotThrow(()->cncControl.setCanal2CNCProgramText(sampleProgram),"CNCControl Test -> 5_1 check that at start is at line 0");
        //5_1 check that at start is at line 0
        assertEquals(0,cncControl.getCanalLinePositionAsObservables(0).get(),"CNCControl Test -> 5_1 check that at start is at line 0");
        assertEquals(0,cncControl.getCanalLinePositionAsObservables(1).get(),"CNCControl Test -> 5_1 check that at start is at line 0");
        //5_2 check that after goToNExtStepCNC  is at line 1
        cncControl.goToNextStepCNCProgram();
        assertEquals(1,cncControl.getCanalLinePositionAsObservables(0).get(),"CNCControl Test -> 5_2 check that after goToNExtStepCNC  is at line 1");
        assertEquals(1,cncControl.getCanalLinePositionAsObservables(1).get(),"CNCControl Test -> 5_2 check that after goToNExtStepCNC  is at line 1");
    }

    @Test
    void terminateCNCControl() {
        //6_1 Check that function work without alarm
        assertDoesNotThrow( () -> cncControl.terminateCNCControl(),"CNCControl Test -> 6_1 Check that function work without alarm");
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
        cncCanals.add(new Canal(GET_CNC_AXIS_CANAL1(), cncSpindles));
        cncCanals.add(new Canal(GET_CNC_AXIS_CANAL2(), cncSpindles));
        return cncCanals;
    }

    private final static String sampleProgram = "O0001;\nG1 X4.0;\nG4 Y4";
}