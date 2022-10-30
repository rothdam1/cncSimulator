package ch.dcreations.cncsimulator.cncControl;

import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    private List<String> programText;

    public CNCProgram(String programText) {
        setProgramText(programText);
    }

    public String getProgramTextAsText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : programText){
            stringBuilder.append(line).append(";\n");
        }
        return stringBuilder.toString();
    }

    public List<String> getProgramText() {
        return Collections.unmodifiableList(programText);
    }

    public void setProgramText(String programText) {
        this.programText = new LinkedList<>();
        for (String text :programText.replace("\n", "").split(";")){
            this.programText.add(text);
        }
    }
    public void addProgramText(String programText) {
        this.programText.add(programText);
    }
    public int countOfLines() {
        return this.programText.size();
    }

}
