package ch.dcreations.cncsimulator.cncControl;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/* TESTS
TESTCASES 1
    TEST RUN FUNCTION
        1_1    TEST RUN STATE -> RUN  WITH EMPTY PROGRAM
        1_2    TEST RUN STATE -> RUN  WITH SAMPLE PROGRAM
        1_3    TEST RUN STATE -> SINGLE STEP  WITH EMPTY PROGRAM
        1_4    TEST RUN STATE -> RUN  WITH SAMPLE PROGRAM
        1_5    TEST RUN STATE -> STOP  WITH EMPTY PROGRAM
        1_6    TEST RUN STATE -> RUN  WITH SAMPLE PROGRAM
TESTCASES 2
    TEST SET STATE FUNCTION
        2_1 Check SET to RUN
        2_2 Check SET to SINGLE STEP
        2_3 Check SET to STOP
TESTCASES 3
    TEST STOP THREAD WITH STOP FUNCTION
        3_1 TEST THREAD STOP WORKS
        3_2 TEST THREAD IS STOPPED
 */



class CanalTest {



    @Test
    void run() {
        // 1_1    TEST RUN STATE -> RUN  WITH EMPTY PROGRAM
        Canal canal = new Canal(GET_CNC_AXIS_CANAL1());
        canal.setCanalState(CanalState.RUN);
        canal.run();
        assertEquals(0,canal.programLinePositionProperty().get());
        // 1_2    TEST RUN STATE -> RUN  WITH SAMPLE PROGRAM
        canal.setProgram(sampleProgram);
        canal.run();
        assertEquals(2,canal.programLinePositionProperty().get());
        // 1_3    TEST RUN STATE -> SINGLE STEP  WITH EMPTY PROGRAM
        canal = new Canal(GET_CNC_AXIS_CANAL1());
        canal.setCanalState(CanalState.SINGLE_STEP);
        canal.run();
        assertEquals(0,canal.programLinePositionProperty().get());
        // 1_4    TEST RUN STATE -> RUN  WITH SAMPLE PROGRAM
        canal.setProgram(sampleProgram);
        canal.run();
        assertEquals(1,canal.programLinePositionProperty().get());
        // 1_5    TEST RUN STATE -> STOP  WITH EMPTY PROGRAM
        canal = new Canal(GET_CNC_AXIS_CANAL1());
        canal.setCanalState(CanalState.STOP);
        canal.run();
        assertEquals(0,canal.programLinePositionProperty().get());
        // 1_6    TEST RUN STATE -> RUN  WITH SAMPLE PROGRAM
        canal.setProgram(sampleProgram);
        canal.run();
        assertEquals(0,canal.programLinePositionProperty().get());
    }

    @Test
    void setCanalState() {
        Canal canal = new Canal(GET_CNC_AXIS_CANAL1());
        canal.setCanalState(CanalState.RUN);
        //2_1 Check SET to RUN
        assertEquals(CanalState.RUN,canal.getCanalState());
        assertNotEquals(CanalState.SINGLE_STEP, canal.getCanalState());
        //2_2 Check SET to SINGLE STEP
        canal.setCanalState(CanalState.SINGLE_STEP);
        assertEquals(CanalState.SINGLE_STEP,canal.getCanalState());
        //2_3 Check SET to STOP
        canal.setCanalState(CanalState.STOP);
        assertEquals(CanalState.STOP,canal.getCanalState());

    }

    @Test
    void stopRunning() throws InterruptedException {
        Canal canal = new Canal(GET_CNC_AXIS_CANAL1());
        canal.setProgram(sampleProgram);
        //3_1 TEST THREAD STOP WORKS
        assertFalse(canal.getCanalRunState().get());
        Thread thread = new Thread(canal);
        thread.start();
        canal.stopRunning();
        Thread.sleep(100);
        //3_2 TEST THREAD IS STOPPED
        assertEquals(Thread.State.TERMINATED, thread.getState());
    }


    private static List<CNCAxis> GET_CNC_AXIS_CANAL1(){
        List<CNCAxis> cncAxes = new ArrayList<>();
        cncAxes.add(new CNCAxis(AxisName.X1));
        cncAxes.add(new CNCAxis(AxisName.Y1));
        cncAxes.add(new CNCAxis(AxisName.Z1));
        cncAxes.add(new CNCAxis(AxisName.C1));
        return cncAxes;
    }
    private final static String sampleProgram = "O0001;\nG01X3R3;G3Y3";

}