package ch.dcreations.cncsimulator.gui;

import ch.dcreations.cncsimulator.cncControl.Canal.CanalNames;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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



    private final Map<CanalNames, StringProperty> cncProgramText = new HashMap<>();

    public ViewControllerModel() {
        this.cncProgramText.put(CanalNames.CANAL1,new SimpleStringProperty());
        this.cncProgramText.put(CanalNames.CANAL2,new SimpleStringProperty());
    }

    public Map<CanalNames, StringProperty> getCncProgramText() {
        return cncProgramText;
    }

    public String getProgramFromCanal(CanalNames canalName){
        return cncProgramText.get(canalName).get();
    }
}
