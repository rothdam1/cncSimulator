package ch.dcreations.cncsimulator.cncControl.GCodes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * <p>
 * <p>
 *  TEST FOR THE GCode.class
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */
/* TESTS
TESTCASES 1
    TEST RUN FUNCTION
        1_1 TEST get Code number

 */
class GCodeTest {

    GCode gCode;
    @BeforeEach
    void setUp() {
        gCode = new G01(1);
    }

    @Test
    void getCodeNumber() {
        // 1_1 TEST get Code number
        assertEquals(1,gCode.getCodeNumber(),"CanalTest -> 1_1 TEST get Code number");
    }
}