package ch.dcreations.cncsimulator.gui;

import ch.dcreations.cncsimulator.cncControl.CanalNames;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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



    Map<CanalNames, StringProperty> cncProgramText = new HashMap<>();

    public ViewControllerModel() {
        this.cncProgramText.put(CanalNames.CANAL1,new SimpleStringProperty());
        this.cncProgramText.put(CanalNames.CANAL2,new SimpleStringProperty());
    }

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(cncProgramText);




    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);

    }

    public void update(Map<CanalNames,String> cncProgramTextOld ) {
        this.pcs.firePropertyChange("CanalProgram", cncProgramTextOld, cncProgramText);
    }


    public void setCanal1CNCProgramText(String canal1CNCProgramText) {
        cncProgramText.get(CanalNames.CANAL1).set(canal1CNCProgramText);
    }

    public void setCanal2CNCProgramText(String canal2CNCProgramText) {
        cncProgramText.get(CanalNames.CANAL2).set(canal2CNCProgramText);
    }

    public String getProgramFromCanal(CanalNames canalName){
        return cncProgramText.get(canalName).get();
    }
}
