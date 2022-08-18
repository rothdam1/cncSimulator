package ch.dcreations.cncsimulator.gui;

import ch.dcreations.cncsimulator.cncControl.Canals;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * <p>
 *  Graphic user interface Model stroes the Data of the TextField
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */

public class ViewControllerModel {

    String Canal1CNCProgramText = "";
    String Canal2CNCProgramText = "";
    Map<Canals,String> cncProgramText = new HashMap<>();

    public ViewControllerModel() {
        this.cncProgramText.put(Canals.CANAL1,Canal1CNCProgramText);
        this.cncProgramText.put(Canals.CANAL2,Canal2CNCProgramText);
    }

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(cncProgramText);
    private String textField = "";




    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);

    }

    public void update(Map<Canals,String> cncProgramTextOld ) {
        this.pcs.firePropertyChange("CanalProgram", cncProgramTextOld, cncProgramText);
    }


    public void setCanal1CNCProgramText(String canal1CNCProgramText) {
        Map<Canals,String> cncProgramTextNewOld = Map.copyOf(cncProgramText);
        cncProgramText.replace(Canals.CANAL1,canal1CNCProgramText);
        update(cncProgramTextNewOld);
    }

    public void setCanal2CNCProgramText(String canal2CNCProgramText) {
        Map<Canals,String> cncProgramTextNewOld = Map.copyOf(cncProgramText);
        cncProgramText.replace(Canals.CANAL2,canal2CNCProgramText);
        update(cncProgramTextNewOld);
    }
}
