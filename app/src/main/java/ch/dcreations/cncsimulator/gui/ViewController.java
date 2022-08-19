package ch.dcreations.cncsimulator.gui;
import ch.dcreations.cncsimulator.cncControl.CNCAxis;
import ch.dcreations.cncsimulator.cncControl.CNCControl;
import ch.dcreations.cncsimulator.cncControl.Canals;
import ch.dcreations.cncsimulator.config.Config;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * <p>
 *  View Controller of the Main View of the Gui
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */

public class ViewController {

    private static final Logger logger = Logger.getLogger(LogConfiguration.class.getCanonicalName());
    private final ViewControllerModel viewControllerModel = new ViewControllerModel();

    private final CNCControl cncControl = new CNCControl(Config.GET_CNC_AXIS());

    @FXML
    private VBox CNCVBox;


    @FXML
    private TextArea textAreaCanal1;

    @FXML
    private TextArea textAreaCanal2;

    @FXML
    void initialize() {
        viewControllerModel.addPropertyChangeListener(evt -> {
            textAreaCanal1.setText(viewControllerModel.cncProgramText.get(Canals.CANAL1));
            textAreaCanal2.setText(viewControllerModel.cncProgramText.get(Canals.CANAL2));
        });
        setCNCControl();
        logger.log(Level.INFO,"GUI initialized");
    }


    public void closeController() {

    }


    @FXML
    void loadSampleProgram() {
        viewControllerModel.setCanal1CNCProgramText(Config.CANAL1_SAMPLE_PROGRAM.getProgramText());
        viewControllerModel.setCanal2CNCProgramText(Config.CANAL2_SAMPLE_PROGRAM.getProgramText());
    }

    public void setCNCControl() {
        for (CNCAxis cncAxis : cncControl.getCncAxes()) {
            Label label = new Label(cncAxis.getAxisName() + " = " + cncAxis.getAxisPosition());
            CNCVBox.getChildren().add(label);
        }
    }
}
