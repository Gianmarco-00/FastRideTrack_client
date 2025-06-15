package org.ispw.fastridetrack.controller.GUIController;

import jakarta.mail.MessagingException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.event.ActionEvent;


import org.ispw.fastridetrack.bean.*;
import org.ispw.fastridetrack.controller.ApplicationFacade;
import org.ispw.fastridetrack.controller.SceneNavigator;
import org.ispw.fastridetrack.dao.RideRequestDAO;
import org.ispw.fastridetrack.dao.TaxiRideDAO;
import org.ispw.fastridetrack.dao.Adapter.GoogleMapsAdapter;
import org.ispw.fastridetrack.exception.FXMLLoadException;
import org.ispw.fastridetrack.model.Map;
import org.ispw.fastridetrack.model.Session.SessionManager;
import org.ispw.fastridetrack.util.TemporaryMemory;

public class SelectDriverGUIController {

    @FXML private Label driverNameLabel;
    @FXML private Label vehiclePlateLabel;
    @FXML private Label vehicleInfoLabel;
    @FXML private Label estimatedFareLabel;
    @FXML private Label estimatedTimeLabel;
    @FXML private Button confirmButton;
    @FXML public Button cancelButton;
    @FXML public Button goBackButton;
    @FXML private WebView mapView;

    private final RideRequestDAO rideRequestDAO;
    private final TaxiRideDAO taxiRideDAO;
    private final TemporaryMemory tempMemory;
    private TaxiRideConfirmationBean taxiRideBean;

    // Facade iniettata da SceneNavigator
    private ApplicationFacade facade;

    // Setter usato da SceneNavigator per iniettare il facade
    public void setFacade(ApplicationFacade facade) {
        this.facade = facade;
    }

    public SelectDriverGUIController() {
        SessionManager session = SessionManager.getInstance();
        this.rideRequestDAO = session.getRideRequestDAO();
        this.taxiRideDAO = session.getTaxiRideDAO();
        this.facade = new ApplicationFacade();
        this.tempMemory = TemporaryMemory.getInstance();
    }

    @FXML
    public void initialize() {
        this.taxiRideBean = tempMemory.getRideConfirmation();

        if (taxiRideBean == null || taxiRideBean.getDriver() == null) {
            showError("Dati mancanti", "Impossibile caricare i dati del driver.");
            confirmButton.setDisable(true);
            return;
        }

        // **Qui cambia: driver è DriverBean, non Model**
        DriverBean driver = taxiRideBean.getDriver();
        driverNameLabel.setText("Driver: " + driver.getName());
        vehicleInfoLabel.setText("Vehicle Model: " + driver.getVehicleInfo());
        vehiclePlateLabel.setText("Vehicle Plate: " + driver.getVehiclePlate());
        estimatedFareLabel.setText(taxiRideBean.getEstimatedFare() != null ?
                String.format("Estimated Fare: €%.2f", taxiRideBean.getEstimatedFare()) : "Estimated Fare: N/D");
        estimatedTimeLabel.setText(taxiRideBean.getEstimatedTime() != null ?
                "Estimated Time: " + taxiRideBean.getEstimatedTime() + " mins" : "Estimated Time: N/D");

        loadMapInView();
    }

    private void loadMapInView() {
        if (taxiRideBean == null) return;

        CoordinateBean origin = taxiRideBean.getUserLocation();
        String destination = taxiRideBean.getDestination();

        GoogleMapsAdapter mapsAdapter = new GoogleMapsAdapter();

        MapRequestBean mapRequest = new MapRequestBean();
        mapRequest.setOrigin(origin);
        mapRequest.setDestination(destination);

        Map map = mapsAdapter.calculateRoute(mapRequest);

        if (map != null && map.getHtmlContent() != null) {
            mapView.getEngine().loadContent(map.getHtmlContent());
        } else {
            showError("Errore mappa", "Impossibile caricare la mappa del percorso.");
        }
    }

    @FXML
    private void onConfirmRide() {
        try {
            EmailBean email = buildEmailBean();
            // Qui il taxiRideBean lo passo direttamente
            facade.getClientRideManagementAC().confirmRideAndNotify(taxiRideBean, email);
            showInfo("Corsa confermata", "Il driver è stato notificato via email.");
            confirmButton.setDisable(true);
            goBackButton.setDisable(true);
        } catch (MessagingException e) {
            showError("Errore", "Errore durante l'invio dell'email al driver.");
        }
    }

    private EmailBean buildEmailBean() {
        DriverBean driver = taxiRideBean.getDriver();
        GoogleMapsAdapter mapsAdapter = new GoogleMapsAdapter();

        // Ottengo l'indirizzo leggibile dalle coordinate del cliente
        String originAddress = "Indirizzo non disponibile";
        CoordinateBean originCoord = taxiRideBean.getUserLocation();
        if (originCoord != null) {
            originAddress = mapsAdapter.getAddressFromCoordinates(
                    originCoord.getLatitude(), originCoord.getLongitude()
            );
        }

        String subject = "Nuova corsa: " + taxiRideBean.getRideID();
        String body = String.format(
                "Ciao %s,\n\nHai una nuova corsa assegnata.\n\n" +
                        "Cliente: %s\n" +
                        "Partenza: %s\n" +
                        "Destinazione: %s\n" +
                        "Tariffa stimata: €%.2f\n" +
                        "Tempo stimato: %.2f minuti\n\n" +
                        "Controlla l'app per maggiori dettagli.\n\nGrazie!",
                driver.getName(),
                taxiRideBean.getClient().getName(),
                originAddress,
                taxiRideBean.getDestination(),
                taxiRideBean.getEstimatedFare(),
                taxiRideBean.getEstimatedTime()
        );
        return new EmailBean(driver.getEmail(), subject, body);
    }


    @FXML
    private void onGoBack(ActionEvent event) {
        try {
            SceneNavigator.switchTo("/org/ispw/fastridetrack/views/SelectTaxi.fxml", "Seleziona Taxi");
        } catch (FXMLLoadException e) {
            e.printStackTrace();
            showError("Errore caricamento", "Errore nel tornare alla selezione del taxi.");
        }
    }


    private void showError(String title, String message) {
        showAlert(title, message, Alert.AlertType.ERROR);
    }

    private void showInfo(String title, String message) {
        showAlert(title, message, Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}








