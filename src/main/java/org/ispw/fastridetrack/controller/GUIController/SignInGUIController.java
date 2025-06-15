package org.ispw.fastridetrack.controller.GUIController;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.ispw.fastridetrack.controller.ApplicationFacade;
import org.ispw.fastridetrack.controller.SceneNavigator;
import org.ispw.fastridetrack.exception.FXMLLoadException;
import org.ispw.fastridetrack.model.UserType;

public class SignInGUIController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private ApplicationFacade facade;

    public void setFacade(ApplicationFacade facade) {
        this.facade = facade;
    }

    @FXML
    private void onNextClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            showErrorAlert("Dati mancanti", "Inserisci username e password.");
            return;
        }

        try {
            boolean isValidClient = facade.getLoginAC().validateClientCredentials(username, password, UserType.CLIENT);
            boolean isValidDriver = facade.getLoginAC().validateDriverCredentials(username, password, UserType.DRIVER);

            if (isValidClient) {
                SceneNavigator.switchTo("/org/ispw/fastridetrack/views/Home.fxml", "Home");
            } else if (isValidDriver) {
                SceneNavigator.switchTo("/org/ispw/fastridetrack/views/Home_driver.fxml", "Home Driver");
            } else {
                showErrorAlert("Login Fallito", "Credenziali errate. Riprova.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Errore di connessione", "Impossibile connettersi al database:\n" + e.getMessage());
        }
    }

    @FXML
    private void onHomepageClick() throws FXMLLoadException {
        SceneNavigator.switchTo("/org/ispw/fastridetrack/views/Homepage.fxml", "Homepage");
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}



