package ch.dcreations.cncsimulator.cncControl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * <p>
 * <p>
 *  The CNC control contains axis and Program
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */
public class CNCControl {


    private final List<Canal> canals;

    public CNCControl(List<Canal> canals) {
        this.canals = canals;
    }

    public List<CNCAxis> getCncAxes() {
        List<CNCAxis> cncAxes = new ArrayList<>();
        for (Canal canal : canals) {
            cncAxes.addAll(canal.getCncAxes());
        }
    return Collections.unmodifiableList(cncAxes);
    }

    public void setCanal1CNCProgramText(String programText) throws IOException {
        setCNCProgramToCanal(1, programText);
    }
    public void setCanal2CNCProgramText(String programText) throws IOException {
        setCNCProgramToCanal(2, programText);
    }
    private void setCNCProgramToCanal(int canalNumber, String programText) throws IOException {
        if (canals.size()< canalNumber){
            throw new IOException("Canal does not Exist");
        }
        else {
            canals.get(canalNumber-1).setProgram(programText);
        }
    }

    public String getCanal1CNCProgramText() throws IOException {
        return getCNCPRogramTextFromCanal(1);
    }

    public String getCanal2CNCProgramText() throws IOException {
        return getCNCPRogramTextFromCanal(2);
    }
    private String getCNCPRogramTextFromCanal(int canalNumber) throws IOException {
        if (canals.size()< canalNumber){
            throw new IOException("Canal does not Exist");
        }
        else {
            return canals.get(canalNumber-1).getProgramText();
        }
    }
}
