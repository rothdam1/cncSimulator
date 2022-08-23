package ch.dcreations.cncsimulator.cncControl;

import java.util.Arrays;
import java.util.Optional;

/**
 * <p>
 * <p>
 *  Enum for Axis Names
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */
public enum AxisName {
    X('X'),
    Y('Y'),
    Z('Z'),
    C('C'),
    U('U'),
    V('V'),
    W('W'),
    H('H'),

    A('A'),
    B('B'),
    ;
    private Character code;
    AxisName(Character axisCode) {
        this.code = axisCode;
    }


    public static Optional<AxisName> get(Character c) {
        return Arrays.stream(AxisName.values())
                .filter(env -> env.code.equals(c))
                .findFirst();
    }
}
