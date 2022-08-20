package ch.dcreations.cncsimulator.gui;
import ch.dcreations.cncsimulator.cncControl.CNCAxis;
import ch.dcreations.cncsimulator.cncControl.CNCControl;
import ch.dcreations.cncsimulator.cncControl.CanalNames;
import ch.dcreations.cncsimulator.config.Config;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.io.IOException;
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
    TextFlow Canal1CNCProgramTextFlow = new TextFlow();

    TextFlow Canal12CNCProgramTextFlow = new TextFlow();

    @FXML
    private VBox CNCVBox;


    @FXML
    private AnchorPane canal1AnchorPane;

    @FXML
    private AnchorPane canal2AnchorPane;
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

        cncControl.getCanalLinePositionAsObservables(0).addListener(
                (observable, oldValue, newValue) -> markLine(newValue.intValue(),CanalNames.CANAL1)
        );
        cncControl.getCanalLinePositionAsObservables(1).addListener(
                (observable, oldValue, newValue) -> markLine(newValue.intValue(),CanalNames.CANAL2)
        );
        setCNCControl();
        viewControllerModel.cncProgramText.get(CanalNames.CANAL1).bind(textAreaCanal1.textProperty());
        viewControllerModel.cncProgramText.get(CanalNames.CANAL2).bind(textAreaCanal2.textProperty());
        logger.log(Level.INFO,"GUI initialized");
    }


    public void closeController() {
        try {
            cncControl.terminateCNCControl();
        } catch (InterruptedException e) {
            logger.log(Level.WARNING,"SHUTDOWN CNC FAILED");
        }

    }


    @FXML
    void loadSampleProgram() {
        stopCNCControl();
        try {
            textAreaCanal1.setText(Config.CANAL1_SAMPLE_PROGRAM.getProgramText());
            textAreaCanal2.setText(Config.CANAL2_SAMPLE_PROGRAM.getProgramText());
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
        setCNCProgramToControl();
        cncControl.runCNCProgram();
        stopCNCButton.setSelected(false);
        runCNCButton.setSelected(true);
        canal1AnchorPane.getChildren().clear();
        canal1AnchorPane.getChildren().add(Canal1CNCProgramTextFlow);
        canal2AnchorPane.getChildren().clear();
        canal2AnchorPane.getChildren().add(Canal12CNCProgramTextFlow);
        runCNCButton.setSelected(false);
    }

    private void setCNCProgramToControl() {
        try {
            cncControl.setCanal1CNCProgramText(viewControllerModel.getProgramFromCanal(CanalNames.CANAL1));
            cncControl.setCanal2CNCProgramText(viewControllerModel.getProgramFromCanal(CanalNames.CANAL2));
        } catch (IOException e) {
            logger.log(Level.WARNING,"Load Program to NC Failed");
        }
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
        }finally {
            canal1AnchorPane.getChildren().clear();
            canal1AnchorPane.getChildren().add(textAreaCanal1);
            canal2AnchorPane.getChildren().clear();
            canal2AnchorPane.getChildren().add(textAreaCanal2);
        }
    }

    @FXML
    private void goToNextStepCNCButtonClicked(){
        setCNCProgramToControl();
        cncControl.goToNextStepCNCProgram();
        canal1AnchorPane.getChildren().clear();
        canal1AnchorPane.getChildren().add(Canal1CNCProgramTextFlow);
        canal2AnchorPane.getChildren().clear();
        canal2AnchorPane.getChildren().add(Canal12CNCProgramTextFlow);
    }

    private void markLine(int lineNumber,CanalNames canalName){
        Platform.runLater(() -> {
            String[] canal1Program = viewControllerModel.getProgramFromCanal(canalName).replace("\n","").split(";");
            //Highlight current Program line
            switch (canalName) {
                case CANAL1 -> Canal1CNCProgramTextFlow.getChildren().clear();
                case CANAL2 -> Canal12CNCProgramTextFlow.getChildren().clear();
            }
            for (int i = lineNumber; i < canal1Program.length;i++){
                Text text1 = new Text(canal1Program[i]+"\n");
                if(i == lineNumber){
                    text1.setFill(Color.RED);
                    text1.setFont(Font.font("Helvetica",  FontWeight.BOLD, 20));
                }else {
                    text1.setFill(Color.BLACK);
                    text1.setFont(Font.font("Helvetica",  FontWeight.NORMAL, 20));
                }
                switch (canalName) {
                    case CANAL1 -> Canal1CNCProgramTextFlow.getChildren().add(text1);
                    case CANAL2 -> Canal12CNCProgramTextFlow.getChildren().add(text1);
                }
            }
        });


    }

}
