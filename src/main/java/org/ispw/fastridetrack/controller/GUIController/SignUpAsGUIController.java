package org.ispw.fastridetrack.controller.GUIController;

import javafx.fxml.FXML;
import org.ispw.fastridetrack.controller.ApplicationFacade;
import org.ispw.fastridetrack.exception.FXMLLoadException;
import org.ispw.fastridetrack.controller.SceneNavigator;

public class SignUpAsGUIController {

    // Facade iniettata da SceneNavigator
    private ApplicationFacade facade;

    // Setter usato da SceneNavigator per iniettare il facade
    public void setFacade(ApplicationFacade facade) {
        this.facade = facade;
    }

    @FXML
    public void onClientSignUp() throws FXMLLoadException {
        // Cambio scena a SignUp.fxml per il client
        SceneNavigator.switchTo("/org/ispw/fastridetrack/views/SignUp.fxml", "Sign Up-Client");
    }

    @FXML
    public void onDriverSignUp() throws FXMLLoadException {
        // Cambio scena a ?????.fxml per il driver (quella di Alex)
        SceneNavigator.switchTo("/org/ispw/fastridetrack/views/Sign Up.fxml", "Sign Up-Driver");
    }

    @FXML
    public void onBackToHomepage() throws FXMLLoadException {
        // Torno alla schermata Homepage
        SceneNavigator.switchTo("/org/ispw/fastridetrack/views/Homepage.fxml", "Homepage");
    }
}


