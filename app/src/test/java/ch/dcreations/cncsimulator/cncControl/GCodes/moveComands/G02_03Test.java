package ch.dcreations.cncsimulator.cncControl.GCodes.moveComands;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.Plane;
import ch.dcreations.cncsimulator.cncControl.Exceptions.IllegalFormatOfGCodeException;
import ch.dcreations.cncsimulator.cncControl.GCodes.FeedOptions;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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
    TEST CHECK IF PARAMETER ARE RIGHT

 */
class G02_03Test {

    long codeNumber;
    FeedOptions feedOptions = FeedOptions.FEED_PER_MINUITE;
    ObservableIntegerValue spindleSpeed =  new SimpleIntegerProperty(5000);
        Position startPosition;
        SimpleDoubleProperty feed = new SimpleDoubleProperty(0.05);
        Map<AxisName, Double> parameter;
        Map<Character, Double> additionalParameterMap;
        Plane plane;
        double distanceErrorMax = 0.5 ;

    @BeforeEach
    void setUp() {
        plane = Plane.G17;
        parameter = new HashMap<>();
        parameter.put(AxisName.X,20.0);
        parameter.put(AxisName.Y,0.0);
        parameter.put(AxisName.Z,0.0);
        additionalParameterMap = new HashMap<>();
        additionalParameterMap.put('R',20.0);
        startPosition = new Position(0.0,0.0,0.0);
    }

    //1_1 X-Y plane but K programmed should give an error
        @Test
        void checkCorrectParameter(){
            assertThrows(IllegalFormatOfGCodeException.class,() ->
            {G02_03 g02_03 = new G02_03(codeNumber,feedOptions,spindleSpeed,startPosition,feed,parameter,additionalParameterMap,plane,distanceErrorMax);});
        }

    @Test
    void testCalculatePosition() {



    }
}