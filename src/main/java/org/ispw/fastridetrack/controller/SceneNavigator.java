package org.ispw.fastridetrack.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ispw.fastridetrack.exception.FXMLLoadException;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

//Classe helper per la gestione della navigazione tra scene JavaFX.
public class SceneNavigator {
    private static final Logger LOGGER = Logger.getLogger(SceneNavigator.class.getName());
    private static Stage mainStage;
    private static ApplicationFacade facade;

    //Imposta lo stage principale dell'applicazione.

    public static void setStage(Stage stage) {
        if (stage == null) {
            throw new IllegalArgumentException("Stage non può essere null");
        }
        mainStage = stage;
    }

    // Imposta la facade condivisa tra i controller.

    public static void setFacade(ApplicationFacade applicationFacade) {
        facade = applicationFacade;
    }

    // Restituisce l'istanza corrente di ApplicationFacade.

    public static ApplicationFacade getFacade() {
        return facade;
    }

    // Cambia scena caricando il file FXML specificato e impostando il titolo della finestra.
    // Se il controller contiene un metodo "setFacade(ApplicationFacade)" verrà invocato.

    public static void switchTo(String fxmlPath, String title) throws FXMLLoadException {
        if (mainStage == null) {
            throw new IllegalStateException("Stage principale non impostato. Chiamare setStage() prima.");
        }

        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller != null && facade != null) {
                injectFacade(controller, facade);
            }

            mainStage.setTitle(title);
            mainStage.setScene(new Scene(root));
            mainStage.show();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nel caricamento del file FXML: " + fxmlPath, e);
            throw new FXMLLoadException("Errore nel caricamento del file FXML: " + fxmlPath, e);
        }
    }

    // Metodo privato per iniettare la facade nel controller, se presente il metodo setFacade.

    private static void injectFacade(Object controller, ApplicationFacade facade) {
        try {
            Method setFacadeMethod = controller.getClass().getMethod("setFacade", ApplicationFacade.class);
            setFacadeMethod.invoke(controller, facade);
            LOGGER.log(Level.FINE, "Facade iniettata in controller {0}", controller.getClass().getSimpleName());
        } catch (NoSuchMethodException e) {
            LOGGER.log(Level.INFO, "Nessun metodo setFacade in {0}", controller.getClass().getSimpleName());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore durante l'iniezione della facade in " + controller.getClass().getSimpleName(), e);
        }
    }
}





