package ch.dcreations.cncsimulator.gui;
import ch.dcreations.cncsimulator.animation.StraightLine;
import ch.dcreations.cncsimulator.animation.AnimationModel;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.AxisName;
import ch.dcreations.cncsimulator.cncControl.Canal.CNCMotors.CNCAxis;
import ch.dcreations.cncsimulator.cncControl.CNCControl;
import ch.dcreations.cncsimulator.cncControl.Canal.CanalNames;
import ch.dcreations.cncsimulator.config.Config;
import ch.dcreations.cncsimulator.config.LogConfiguration;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
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
    private Button stopCNCButton;

    @FXML
    private TextArea WarringTextView;

    @FXML
    private TabPane cncAnimationView;

    private double xPos = 0;
    private double yPos = 0;

    private List<AnimationModel> animationModelList = new ArrayList<>();

;
    @FXML
    public void initialize() {
       initialize(new Config(),new CNCControl(Config.GET_CNC_CANALS()));
    }

    public void initialize(Config config,CNCControl cncControl) {
        this.configuration = config;
        this.cncControl = cncControl;
        cncControl.getCanalLinePositionAsObservables(0).addListener((observable, oldValue, newValue) -> markLine(newValue.intValue(),CanalNames.CANAL1));
        cncControl.getCanalLinePositionAsObservables(1).addListener((observable, oldValue, newValue) -> markLine(newValue.intValue(),CanalNames.CANAL2));
        setCNCControl();
        viewControllerModel.getCncProgramText().get(CanalNames.CANAL1).bind(textAreaCanal1.textProperty());
        viewControllerModel.getCncProgramText().get(CanalNames.CANAL2).bind(textAreaCanal2.textProperty());
        logger.log(Level.INFO,"GUI initialized");
        logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord logRecord) {
                if (logRecord.getMessage().contains(":")) {
                    WarringTextView.setText(logRecord.getMessage().substring(logRecord.getMessage().indexOf(":",2)-1));
                    WarringTextView.setText(logRecord.getMessage());
                }else {
                    WarringTextView.setText(logRecord.getMessage());
                }
            }

            @Override
            public void flush() {

            }

            @Override
            public void close() throws SecurityException {

            }
        });

        for (int i = 1; i<= cncControl.getCncAxes().size() ; i++) {
            Tab tab = new Tab();
            tab.setText("Canal " + i);
            Pane pane = new Pane();
            tab.setContent(pane);
            cncAnimationView.getTabs().add(tab);
            animationModelList.add(new AnimationModel(pane));
        }
        cncControl.setAnimationView(animationModelList);
    }

    public void initializeAnimation() {
        animationModelList.stream().forEach((x) -> {
            x.setOffset(cncAnimationView.getWidth()/2,cncAnimationView.getHeight()/2,0);
            x.update();
        });

    }


    public void closeController() {
        try {
            cncControl.terminateCNCControl();
        } catch (InterruptedException e) {
            logger.log(Level.WARNING,"SHUTDOWN CNC FAILED");
        }

    }
    @FXML
    private void mouseHandler(MouseEvent event) {
                AnimationModel animationModel = animationModelList.get(cncAnimationView.getSelectionModel().getSelectedIndex());
                if (event.isSecondaryButtonDown()){
                    double diff = getMouseX(event) - xPos;
                     animationModel.moveXAxis((diff) );
                    xPos = getMouseX(event);
                    diff = getMouseY(event) - yPos;
                    animationModel.moveYAxis((diff) );
                    yPos = getMouseY(event);
                }else {
                    double diff = getMouseX(event) - xPos;
                    animationModel.rotateYAxis((diff) * 1);
                    xPos = getMouseX(event);
                    diff = getMouseY(event) - yPos;
                    animationModel.rotateXAxis((diff) * -1);
                    yPos = getMouseY(event);
                }
    }

    @FXML
    private void mouseClickedOnPanel(MouseEvent event) {
        xPos = getMouseX(event);
        yPos = getMouseY(event);
    }
    @FXML
    private void zoomPlus() {
        AnimationModel animationModel = animationModelList.get(cncAnimationView.getSelectionModel().getSelectedIndex());

        animationModel.zoomPlus();
    }


    @FXML
    private void zoomMinus() {
        AnimationModel animationModel = animationModelList.get(cncAnimationView.getSelectionModel().getSelectedIndex());

        animationModel.zoomMinus();
    }

    @FXML
    private void centerView() {
        AnimationModel animationModel = animationModelList.get(cncAnimationView.getSelectionModel().getSelectedIndex());

        animationModel.setOffset(cncAnimationView.getWidth()/2,cncAnimationView.getHeight()/2,0);
        animationModel.resetView();
    }

    private double getMouseX(MouseEvent event) {
        return event.getX();
    }


    private double getMouseY(MouseEvent event) {
        return event.getY();
    }

    @FXML
    public void loadSampleProgram() {
        resetCNCButtonClicked();
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

        resetNCControllSwitch();
        WarringTextView.setText("");
        setCNCProgramToControl();
        try {
            cncControl.runCNCProgram();
        }catch (Exception e){
            logger.log(Level.WARNING,e.getMessage());
        }finally {
            runCNCButton.setSelected(true);
            canal1AnchorPane.getChildren().clear();
            canal1AnchorPane.getChildren().add(Canal1CNCProgramTextFlow);
            canal2AnchorPane.getChildren().clear();
            canal2AnchorPane.getChildren().add(Canal12CNCProgramTextFlow);
            runCNCButton.setSelected(false);
        }
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
        switchNCCControl();
    }

    private boolean brakeState = false;
    private void switchNCCControl() {
       if(cncControl.isTheNCRunning()){
        if (!brakeState) {
            WarringTextView.setText("");
            brakeState = true;
            stopCNCButton.setText("restart");
            cncControl.stopCNCControl();
        } else {
            stopCNCButton.setText("stop");
            brakeState = false;
            WarringTextView.setText("");
            cncControl.runStoppedCNCControl();

        }
    }
    }

    private void resetNCControllSwitch(){
        stopCNCButton.setText("stop");
        brakeState = false;
        WarringTextView.setText("");
        cncControl.runStoppedCNCControl();
        AnimationModel animationModel = animationModelList.get(cncAnimationView.getSelectionModel().getSelectedIndex());
        animationModel.deleteAll();
        animationModel.resetView();
        cncControl.resetAxis();
    }



    @FXML
    public void resetCNCButtonClicked(){
        WarringTextView.setText("");
        runCNCButton.setSelected(false);
        try {
            cncControl.stopCNCProgram();
        }catch (Exception e){
            logger.log(Level.WARNING,"STOP CNC failed");
        }finally {
            canal1AnchorPane.getChildren().clear();
            canal1AnchorPane.getChildren().add(textAreaCanal1);
            canal2AnchorPane.getChildren().clear();
            canal2AnchorPane.getChildren().add(textAreaCanal2);
            resetNCControllSwitch();
        }
    }


    @FXML
    public void goToNextStepCNCButtonClicked(){

        brakeState = false;
        try {
            setCNCProgramToControl();
            cncControl.goToNextStepCNCProgram();
        }catch (Exception e){
            logger.log(Level.WARNING,e.getMessage());
        }finally {
            canal1AnchorPane.getChildren().clear();
            canal1AnchorPane.getChildren().add(Canal1CNCProgramTextFlow);
            canal2AnchorPane.getChildren().clear();
            canal2AnchorPane.getChildren().add(Canal12CNCProgramTextFlow);
        }
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
