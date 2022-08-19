package ch.dcreations.cncsimulator.gui;
import ch.dcreations.cncsimulator.cncControl.CNCAxis;
import ch.dcreations.cncsimulator.cncControl.CNCControl;
import ch.dcreations.cncsimulator.cncControl.CanalNames;
import ch.dcreations.cncsimulator.config.Config;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

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

    private final CNCControl cncControl = new CNCControl(Config.GET_CNC_CANALS());

    @FXML
    private VBox CNCVBox;


    @FXML
    private TextArea textAreaCanal1;

    @FXML
    private TextArea textAreaCanal2;

    @FXML
    private ToggleButton runCNCButton;

    @FXML
    private ToggleButton stopCNCButton;


    @FXML
    void initialize() {
        viewControllerModel.addPropertyChangeListener(evt -> {
            textAreaCanal1.setText(viewControllerModel.cncProgramText.get(CanalNames.CANAL1));
            textAreaCanal2.setText(viewControllerModel.cncProgramText.get(CanalNames.CANAL2));
        });
        setCNCControl();
        logger.log(Level.INFO,"GUI initialized");
    }


    public void closeController() {
        try {
            cncControl.terminateCNCControl();
        } catch (InterruptedException e) {
            logger.log(Level.WARNING,"SHOTDOWN CNC FAILED");
        }

    }


    @FXML
    void loadSampleProgram() {
        try {
            cncControl.setCanal1CNCProgramText(Config.CANAL1_SAMPLE_PROGRAM.getProgramText());
            cncControl.setCanal2CNCProgramText(Config.CANAL2_SAMPLE_PROGRAM.getProgramText());
            viewControllerModel.setCanal1CNCProgramText(cncControl.getCanal1CNCProgramText());
            viewControllerModel.setCanal2CNCProgramText(cncControl.getCanal2CNCProgramText());
            markLine();
        }catch (Exception e){
            logger.log(Level.WARNING,e.getMessage());
        }
    }

    public void setCNCControl() {
        for (CNCAxis cncAxis : cncControl.getCncAxes()) {
            Label label = new Label(cncAxis.getAxisName() + " = " + cncAxis.getAxisPosition());
            CNCVBox.getChildren().add(label);
        }
    }
    @FXML
    private void runCNCButtonClicked(){
        cncControl.runCNCProgram();
        stopCNCButton.setSelected(false);
    }

    @FXML
    private void stopCNCButtonClicked(){
        stopCNCControl();
        runCNCButton.setSelected(false);
    }

    @FXML
    private void resetCNCButtonClicked(){
        stopCNCControl();
        runCNCButton.setSelected(false);
        stopCNCButton.setSelected(false);
    }

    private void stopCNCControl(){
        try {
            cncControl.stopCNCProgram();
        }catch (Exception e){
            logger.log(Level.WARNING,"STOP CNC failed");
        }
    }

    @FXML
    private void goToNextStepCNCButtonClicked(){
        cncControl.goToNextStepCNCProgram();
    }

    private void markLine(){
        Text text = new Text("HALLLO");
        text.setSelectionFill(Color.BLUE);
        textAreaCanal1.setText(text.getText());

    }
}
