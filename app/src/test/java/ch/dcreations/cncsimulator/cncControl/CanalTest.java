package ch.dcreations.cncsimulator.cncControl;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CanalTest {



    @Test
    void run() {

    }

    @Test
    void setCanalState() {
        Canal canal = new Canal(GET_CNC_AXIS_CANAL1());
        canal.setCanalState(CanalState.RUN);
        assertEquals(CanalState.RUN,canal.getState());
    }

    @Test
    void stopRunning() {
    }


    public static List<CNCAxis> GET_CNC_AXIS_CANAL1(){
        List<CNCAxis> cncAxes = new ArrayList<>();
        cncAxes.add(new CNCAxis(AxisName.X1));
        cncAxes.add(new CNCAxis(AxisName.Y1));
        cncAxes.add(new CNCAxis(AxisName.Z1));
        cncAxes.add(new CNCAxis(AxisName.C1));
        return cncAxes;
    }
}