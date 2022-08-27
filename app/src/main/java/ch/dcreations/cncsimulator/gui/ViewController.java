package ch.dcreations.cncsimulator.gui;
import ch.dcreations.cncsimulator.cncControl.AxisName;
import ch.dcreations.cncsimulator.cncControl.CNCAxis;
import ch.dcreations.cncsimulator.cncControl.CNCControl;
import ch.dcreations.cncsimulator.cncControl.CanalNames;
import ch.dcreations.cncsimulator.config.Config;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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

    private CNCControl cncControl;
    private final TextFlow Canal1CNCProgramTextFlow = new TextFlow();

    private final TextFlow Canal12CNCProgramTextFlow = new TextFlow();

    private Config configuration;

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
    public void initialize() {
        initialize(new Config(),new CNCControl(Config.GET_CNC_CANALS()));
    }

    public void initialize(Config config,CNCControl cncControl) {
        this.configuration = config;
        this.cncControl = cncControl;
        cncControl.getCanalLinePositionAsObservables(0).addListener(
                (observable, oldValue, newValue) -> markLine(newValue.intValue(),CanalNames.CANAL1)
        );
        cncControl.getCanalLinePositionAsObservables(1).addListener(
                (observable, oldValue, newValue) -> markLine(newValue.intValue(),CanalNames.CANAL2)
        );
        setCNCControl();
        viewControllerModel.getCncProgramText().get(CanalNames.CANAL1).bind(textAreaCanal1.textProperty());
        viewControllerModel.getCncProgramText().get(CanalNames.CANAL2).bind(textAreaCanal2.textProperty());
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
    public void loadSampleProgram() {
        stopCNCControl();
        try {
            textAreaCanal1.setText(configuration.CANAL1_SAMPLE_PROGRAM.getProgramText());
            textAreaCanal2.setText(configuration.CANAL2_SAMPLE_PROGRAM.getProgramText());
        }catch (Exception e){
            logger.log(Level.WARNING,e.getMessage());
        }
    }

    private void setCNCControl() {
        int chanalNumber = 1;
        for (Map<AxisName,CNCAxis> cncAxis : cncControl.getCncAxes()) {
            Label labelCanalName = new Label("Canal "+chanalNumber);
            CNCVBox.getChildren().add(labelCanalName);
            for (AxisName axisName : cncAxis.keySet()) {
                Label label = new Label();
                label.textProperty().bind(Bindings.concat(axisName,chanalNumber," ",cncAxis.get(axisName).axisPositionProperty().asString()));
                CNCVBox.getChildren().add(label);
            }
            chanalNumber++;
        }
    }
    @FXML
    public void runCNCButtonClicked(){
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
    public void stopCNCButtonClicked(){
        stopCNCControl();
        runCNCButton.setSelected(false);

    }

    @FXML
    public void resetCNCButtonClicked(){
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
            stopCNCButton.setSelected(false);
        }
    }

    @FXML
    public void goToNextStepCNCButtonClicked(){
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
            int startViewPosition = (lineNumber == 0) ? 0 : lineNumber-1;
            for (int i = startViewPosition; i < canal1Program.length;i++){
                Text text1 = new Text(canal1Program[i]+"\n");
                if(i == startViewPosition){
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
