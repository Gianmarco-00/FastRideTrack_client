package org.ispw.fastridetrack.controller.GUIController;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.ispw.fastridetrack.controller.ApplicationFacade;
import org.ispw.fastridetrack.exception.FXMLLoadException;
import org.ispw.fastridetrack.controller.SceneNavigator;

import java.net.URL;
import java.util.ResourceBundle;

public class SignUpGUIController implements Initializable {

    @FXML public TextField firstNameField;
    @FXML public TextField lastNameField;
    @FXML public TextField usernameField;
    @FXML public TextField passwordField;
    @FXML private TextField phoneNumberField;
    @FXML public TextField emailField;
    @FXML private ChoiceBox<String> userTypeChoiceBox;

    // Facade iniettata da SceneNavigator
    private ApplicationFacade facade;

    // Setter usato da SceneNavigator per iniettare il facade
    public void setFacade(ApplicationFacade facade) {
        this.facade = facade;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inizializzo solo con "Client"
        userTypeChoiceBox.getItems().add("Client");
        userTypeChoiceBox.setValue("Client");

        // Restringo phoneNumberField ai numeri
        phoneNumberField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                phoneNumberField.setText(newText.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    private void onHomepage() throws FXMLLoadException {
        SceneNavigator.switchTo("/org/ispw/fastridetrack/views/Homepage.fxml", "Homepage");
    }

    @FXML
    private void onSignUp() throws FXMLLoadException {
        // Qui posso anche validare i campi e poi procedere con la schermata iniziale dell'app!
        SceneNavigator.switchTo("/org/ispw/fastridetrack/views/Homepage.fxml", "Home");
    }
}


