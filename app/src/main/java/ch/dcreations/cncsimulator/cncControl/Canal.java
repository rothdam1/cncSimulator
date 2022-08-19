package ch.dcreations.cncsimulator.cncControl;

import java.util.Collections;
import java.util.List;

public class Canal {
    private final List<CNCAxis> cncAxes ;
    private String cncProgramText;

    public Canal(List<CNCAxis> cncAxes) {
        this.cncAxes = cncAxes;
    }

    public List<CNCAxis> getCncAxes() {
        return Collections.unmodifiableList(cncAxes);
    }

    public void setProgram(String programText) {
        cncProgramText = programText;
    }

    public String getProgramText() {
        return this.cncProgramText;
    }
}
