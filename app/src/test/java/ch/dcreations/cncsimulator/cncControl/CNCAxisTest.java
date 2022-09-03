package ch.dcreations.cncsimulator.cncControl;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.CNCAxis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * <p>
 * <p>
 *  TEST FOR THE CNCAxis.class
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */
/* TESTS
TESTCASES 1
    TEST get Name
        1_1 Check that name is right
TESTCASES 2
    TEST get Position
         2_1 Check that position is right"

 */
class CNCAxisTest {

    CNCAxis cncAxis;
    @BeforeEach
    void setUp() {
        cncAxis = new CNCAxis();
    }



    @Test
    void getAxisPosition() {
        // 2_1 Check that position is right
        assertEquals(0,0,"CNCAxis Test -> 2_1 Check that position is right");
    }
}