package org.ispw.fastridetrack;

import javafx.application.Application;
import javafx.stage.Stage;
import org.ispw.fastridetrack.controller.guicontroller.ApplicationFacade;
import org.ispw.fastridetrack.session.SessionManager;
import org.ispw.fastridetrack.controller.guicontroller.SceneNavigator;
import org.ispw.fastridetrack.controller.clicontroller.clientclicontroller;

import static org.ispw.fastridetrack.util.ViewPath.HOMEPAGE_FXML;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SessionManager.init();
        SceneNavigator.setStage(primaryStage);
        SceneNavigator.setFacade(new ApplicationFacade());
        SceneNavigator.switchTo(HOMEPAGE_FXML, "Homepage");
        primaryStage.setOnCloseRequest(event -> SessionManager.getInstance().shutdown());
    }

    @Override
    public void stop() throws Exception {
        SessionManager.getInstance().shutdown();
        super.stop();
    }

    public static void main(String[] args) {
        String useCliEnv = System.getenv("USE_CLI");
        boolean useCLI = useCliEnv != null && useCliEnv.equalsIgnoreCase("true");

        if (useCLI) {
            System.out.println("Avvio in modalità CLI...");
            try {
                clientclicontroller cliView = new clientclicontroller();
                cliView.start();
            } catch (Exception e) {
                System.err.println("Errore durante l'esecuzione della CLI: " + e.getMessage());
                e.printStackTrace();
            } finally {
                SessionManager.getInstance().shutdown();
            }
        } else {
            System.out.println("Avvio in modalità GUI...");
            launch(args);
        }
    }

}


