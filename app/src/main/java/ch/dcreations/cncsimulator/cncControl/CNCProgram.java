package ch.dcreations.cncsimulator.cncControl;
/**
 * <p>
 * <p>
 *  A cnc Program For a CNC Canal
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */

public class CNCProgram {
    String programText;

    public CNCProgram(String programText) {
        this.programText = programText;
    }

    public String getProgramText() {
        return programText;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
