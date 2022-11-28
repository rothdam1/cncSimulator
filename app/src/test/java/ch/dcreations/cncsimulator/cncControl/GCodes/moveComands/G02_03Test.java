package ch.dcreations.cncsimulator.cncControl.GCodes.moveComands;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.Plane;
import ch.dcreations.cncsimulator.cncControl.GCodes.FeedOptions;
import ch.dcreations.cncsimulator.cncControl.GCodes.SpindelRotationOption;
import ch.dcreations.cncsimulator.cncControl.Position.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
/**
 * <p>
 * <p>
 *  TEST FOR THE G2/G3 Calculation
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */
/* TESTS
TESTCASES 1
    TEST CHECK IF PARAMETER ARE RIGHT
TESTCASE 2
    2_1 CHECK IF A CIRCLE WITH * POINTS IS CALCULATED RIGHT  WHEN  CALCULATING 3 POINT CHECK POINTS in G2
    2_2 CHECK IF A CIRCLE WITH * POINTS IS CALCULATED RIGHT  WHEN  CALCULATING 3 POINT CHECK POINTS in G3
TESTCASE 3
    3_1 CHECK IF CALCULATED TIME IS RIGHT DISTANCE AND SPEED

 */
class G02G03Test {

    long codeNumber;
    FeedOptions feedOptions = FeedOptions.FEED_PER_REVOLUTION;
    double spindleSpeed =  5000;
        Position startPosition;
        SpindelRotationOption spindelRotationOption = SpindelRotationOption.CONSTANT_ROTATION;
        double feed = 0.05;
        Map<AxisName, Double> parameter;
        Map<Character, Double> additionalParameterMap;
        Plane plane;
        double distanceErrorMax = 0.5 ;

    @BeforeEach
    void setUp() {
        plane = Plane.G17;
        parameter = new HashMap<>();
        parameter.put(AxisName.X,40.0);
        parameter.put(AxisName.Y,0.0);
        parameter.put(AxisName.Z,0.0);
        additionalParameterMap = new HashMap<>();
        additionalParameterMap.put('R',20.0);
        startPosition = new Position(0.0,0.0,0.0);
    }

    //1_1 X-Y plane but K programmed should give an error
        @Test
        void checkCorrectParameter(){
        parameter.put(AxisName.X,41.0);
            assertThrows(Exception.class,() ->
                    new G02G03(codeNumber,feedOptions,spindelRotationOption,spindleSpeed,startPosition,feed,parameter,additionalParameterMap,plane,distanceErrorMax));
        }

    /*2_1 CHECK IF A CIRCLE WITH * POINTS IS CALCULATED RIGHT IN G2
                                           P2@
                                          @     @
                                        @         @
                             START  P1 @           @P3
    */
    @Test
    void testCalculatePositionG2() throws Exception {
        G02G03 g02_G_03 = new G02G03(2,feedOptions,spindelRotationOption,spindleSpeed,startPosition,feed,parameter,additionalParameterMap,plane,distanceErrorMax);
        Map<AxisName,Double> position=  g02_G_03.calculatePosition(0,3);
        assertEquals(startPosition.getX(),position.get(AxisName.X),"Check X Circle Beginning");
        assertEquals(startPosition.getY(),position.get(AxisName.Y),"Check Y Circle Beginning");
        assertEquals(startPosition.getZ(),position.get(AxisName.Z),"Check Z Circle Beginning");

        position=  g02_G_03.calculatePosition(3,3);
        assertEquals(parameter.get(AxisName.X),round(position.get(AxisName.X),10_000),"Check X Circle Beginning");
        assertEquals(parameter.get(AxisName.Y),round(position.get(AxisName.Y),10_000),"Check Y Circle Beginning");
        assertEquals(parameter.get(AxisName.Z),round(position.get(AxisName.Z),10_000),"Check Z Circle Beginning");

        position=  g02_G_03.calculatePosition(1,2);
        assertEquals(20,round(position.get(AxisName.X),10_000),"Check X Circle Beginning");
        assertEquals(20,round(position.get(AxisName.Y),10_000),"Check Y Circle Beginning");
        assertEquals(0,round(position.get(AxisName.Z),10_000),"Check Z Circle Beginning");
    }
//    2_1 CHECK IF A CIRCLE WITH * POINTS IS CALCULATED RIGHT  WHEN  CALCULATING 3 POINT CHECK POINTS in G3
    @Test
    void testCalculatePositionG3() throws Exception {
        G02G03 g02_G_03 = new G02G03(3,feedOptions,spindelRotationOption,spindleSpeed,startPosition,feed,parameter,additionalParameterMap,plane,distanceErrorMax);
        Map<AxisName,Double> position=  g02_G_03.calculatePosition(0,3);
        assertEquals(startPosition.getX(),position.get(AxisName.X),"Check X Circle Beginning");
        assertEquals(startPosition.getY(),position.get(AxisName.Y),"Check Y Circle Beginning");
        assertEquals(startPosition.getZ(),position.get(AxisName.Z),"Check Z Circle Beginning");

        position=  g02_G_03.calculatePosition(3,3);
        assertEquals(parameter.get(AxisName.X),round(position.get(AxisName.X),10_000),"Check X Circle Beginning");
        assertEquals(parameter.get(AxisName.Y),round(position.get(AxisName.Y),10_000),"Check Y Circle Beginning");
        assertEquals(parameter.get(AxisName.Z),round(position.get(AxisName.Z),10_000),"Check Z Circle Beginning");

        position=  g02_G_03.calculatePosition(1,2);
        assertEquals(20,round(position.get(AxisName.X),10_000),"Check X Circle Beginning");
        assertEquals(-20,round(position.get(AxisName.Y),10_000),"Check Y Circle Beginning");
        assertEquals(0,round(position.get(AxisName.Z),10_000),"Check Z Circle Beginning");
    }

// 3_1 CHECK IF CALCULATED TIME IS RIGHT DISTANCE AND SPEED
    @Test
    void testTimeCalculation() throws Exception {
        G02G03 g02_G_03 = new G02G03(3,feedOptions,spindelRotationOption,spindleSpeed,startPosition,feed,parameter,additionalParameterMap,plane,distanceErrorMax);
        double distance = additionalParameterMap.get('R')*2*Math.PI/2;
        double seconds = distance/(feed*spindleSpeed)*60;
        assertEquals(round(seconds,100) , (round(g02_G_03.getRunTimeInMillisecond(),100)));
    }

    private double round(Double aDouble,int digitsAfterPoint) {
        return( Math.round(aDouble*digitsAfterPoint)/(digitsAfterPoint*1.0));
    }
}