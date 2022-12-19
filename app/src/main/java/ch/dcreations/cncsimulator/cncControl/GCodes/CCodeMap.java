package ch.dcreations.cncsimulator.cncControl.GCodes;

import java.util.HashMap;
import java.util.Map;

public class CCodeMap {

    // Declaring the static map
    public static Map<Double, GCodeCatogories> map;

    // Instantiating the static map
    static
    {
        map = new HashMap<>();
        map.put(0.0, GCodeCatogories.INTERPOLATION);
        map.put(1.0, GCodeCatogories.INTERPOLATION);
        map.put(2.0, GCodeCatogories.INTERPOLATION);
        map.put(3.0, GCodeCatogories.INTERPOLATION);
        map.put(4.0, GCodeCatogories.WAIT);
        map.put(96.0, GCodeCatogories.CNCSETTING);
        map.put(97.0, GCodeCatogories.CNCSETTING);
        map.put(98.0, GCodeCatogories.CNCSETTING);
        map.put(99.0, GCodeCatogories.CNCSETTING);
    }
}
