package ch.dcreations.cncsimulator.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * <p>
 * <p>
 *  Graphic user interface for the cnc Simulator
 * <p>
 *
 * @author Damian www.d-creations.org
 * @version 1.0
 * @since 2022-08-18
 */
public class GUI extends Application {

    ViewController controller;


    @Override
    public void start(Stage primaryStage) {
        firstWindow(primaryStage);
    }

    private void firstWindow(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("mainView.fxml"));
            Pane rootPane = loader.load();
            Scene scene = new Scene(rootPane, 1000, 600, true);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            controller = loader.getController();
            primaryStage.setOnCloseRequest(event -> controller.closeController());
            primaryStage.setTitle("CNC-Simulator");
            primaryStage.show();
        } catch (Exception e) {
            //Print.printError("Error starting up UI " + e.getMessage() + e);
        }
    }
}
