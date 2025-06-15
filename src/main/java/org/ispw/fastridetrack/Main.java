package org.ispw.fastridetrack;

import javafx.application.Application;
import javafx.stage.Stage;
import org.ispw.fastridetrack.controller.ApplicationFacade;
import org.ispw.fastridetrack.model.Session.SessionManager;
import org.ispw.fastridetrack.controller.SceneNavigator;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SessionManager.init();
        SceneNavigator.setStage(primaryStage);
        SceneNavigator.setFacade(new ApplicationFacade());
        SceneNavigator.switchTo("/org/ispw/fastridetrack/views/Homepage.fxml", "Homepage");
        primaryStage.setOnCloseRequest(event -> SessionManager.getInstance().shutdown());
    }

    @Override
    public void stop() throws Exception {
        SessionManager.getInstance().shutdown();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


