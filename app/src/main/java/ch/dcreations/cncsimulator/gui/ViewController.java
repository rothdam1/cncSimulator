package ch.dcreations.cncsimulator.gui;
import ch.dcreations.cncsimulator.cncControl.Canals;
import ch.dcreations.cncsimulator.config.Config;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

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

    @FXML
    private Button calculateCodeButton;

    @FXML
    private Pane cncAnimationView;

    @FXML
    private TreeView<?> cncTreeView;

    @FXML
    private TextArea textAreaChanal1;

    @FXML
    private TextArea textAreaChanal2;

    @FXML
    private Button zoomMinusButton;

    @FXML
    private Button zoomPlusButton;


    @FXML
    private AnchorPane chanal1AnchorPane;

    @FXML
    private AnchorPane chanal2AnchorPane;


    @FXML
    void initialize() {
        viewControllerModel.addPropertyChangeListener(evt -> {
            textAreaChanal1.setText(viewControllerModel.cncProgramText.get(Canals.CANAL1));
            textAreaChanal2.setText(viewControllerModel.cncProgramText.get(Canals.CANAL2));
        });
    }


    public void closeController() {

    }


    @FXML
    void loadSampleProgram(ActionEvent event) {
        viewControllerModel.setCanal1CNCProgramText(Config.CANAL1_SAMPLE_PROGRAM.getProgramText());
        viewControllerModel.setCanal2CNCProgramText(Config.CANAL2_SAMPLE_PROGRAM.getProgramText());
    }
}
