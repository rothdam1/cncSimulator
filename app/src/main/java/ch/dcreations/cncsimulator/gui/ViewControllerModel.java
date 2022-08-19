package ch.dcreations.cncsimulator.gui;

import ch.dcreations.cncsimulator.cncControl.CanalNames;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * <p>
 *  Graphic user interface Model stores the Data of the TextField
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */

public class ViewControllerModel {

    String Canal1CNCProgramText = "";
    String Canal2CNCProgramText = "";
    Map<CanalNames,String> cncProgramText = new HashMap<>();

    public ViewControllerModel() {
        this.cncProgramText.put(CanalNames.CANAL1,Canal1CNCProgramText);
        this.cncProgramText.put(CanalNames.CANAL2,Canal2CNCProgramText);
    }

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(cncProgramText);




    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);

    }

    public void update(Map<CanalNames,String> cncProgramTextOld ) {
        this.pcs.firePropertyChange("CanalProgram", cncProgramTextOld, cncProgramText);
    }


    public void setCanal1CNCProgramText(String canal1CNCProgramText) {
        Map<CanalNames,String> cncProgramTextNewOld = Map.copyOf(cncProgramText);
        cncProgramText.replace(CanalNames.CANAL1,canal1CNCProgramText);
        update(cncProgramTextNewOld);
    }

    public void setCanal2CNCProgramText(String canal2CNCProgramText) {
        Map<CanalNames,String> cncProgramTextNewOld = Map.copyOf(cncProgramText);
        cncProgramText.replace(CanalNames.CANAL2,canal2CNCProgramText);
        update(cncProgramTextNewOld);
    }
}
