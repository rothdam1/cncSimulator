package ch.dcreations.cncsimulator.cncControl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * <p>
 * <p>
 *  TEST FOR THE CNCProgram.class
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */
/* TESTS
TESTCASES 1
    TEST if getProgram is right
        1_1 Test getProgram is equal to set Program
 */
class CNCProgramTest {

    @Test
    void getProgramText() {
        //1_1 Test getProgram is equal to set Program
        CNCProgram cncProgram = new CNCProgram(sampleProgram);
        assertEquals(sampleProgram,cncProgram.getProgramTextAsText(),"CNCProgram Test -> 1_1 Test getProgram is equal to set Program");
    }

    private final static String sampleProgram = "O0001;\nG1 X4.0;\nG4 Y4";
}