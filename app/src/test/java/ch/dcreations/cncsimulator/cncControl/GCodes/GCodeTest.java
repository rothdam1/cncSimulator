package ch.dcreations.cncsimulator.cncControl.GCodes;

import ch.dcreations.cncsimulator.cncControl.GCodes.moveComands.G01;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

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
        assertDoesNotThrow(() -> {gCode = new G01(1,FeedOptions.FEED_PER_MINUITE,new SimpleIntegerProperty(00),new Position(0,0,0),new SimpleDoubleProperty(0),new HashMap<>());});
    }

    @Test
    void getCodeNumber() {
        // 1_1 TEST get Code number
        assertEquals(1,gCode.getCodeNumber(),"CanalTest -> 1_1 TEST get Code number");
    }
}