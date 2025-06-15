package org.ispw.fastridetrack.controller.GUIController;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import org.ispw.fastridetrack.bean.*;
import org.ispw.fastridetrack.controller.ApplicationFacade;
import org.ispw.fastridetrack.controller.SceneNavigator;
import org.ispw.fastridetrack.exception.FXMLLoadException;
import org.ispw.fastridetrack.model.Map;
import org.ispw.fastridetrack.model.PaymentMethod;
import org.ispw.fastridetrack.model.RideConfirmationStatus;
import org.ispw.fastridetrack.model.Session.SessionManager;
import org.ispw.fastridetrack.util.TemporaryMemory;


import java.time.LocalDateTime;
import java.util.List;

public class SelectTaxiGUIController {

    @FXML public Button confirmRideButton;
    @FXML public Button goBackButton;
    @FXML private WebView mapView;
    @FXML private TableView<AvailableDriverBean> taxiTable;
    @FXML private TableColumn<AvailableDriverBean, String> driverNameColumn;
    @FXML private TableColumn<AvailableDriverBean, String> carModelColumn;
    @FXML private TableColumn<AvailableDriverBean, String> plateColumn;
    @FXML private TableColumn<AvailableDriverBean, String> etaColumn;
    @FXML private TableColumn<AvailableDriverBean, String> priceColumn;
    @FXML private ChoiceBox<String> paymentChoiceBox;
    @FXML private TextField destinationField;


    private MapRequestBean mapRequestBean;

    private final TemporaryMemory tempMemory = TemporaryMemory.getInstance();

    // Facade iniettata da SceneNavigator
    private ApplicationFacade facade;

    // Setter usato da SceneNavigator per iniettare il facade
    public void setFacade(ApplicationFacade facade) {
        this.facade = facade;
        //System.out.println("[SelectTaxiGUIController] setFacade chiamato");

        // Ora che la facade è disponibile, completo l'inizializzazione
        if (mapRequestBean == null) {
            mapRequestBean = tempMemory.getMapRequestBean();
        }

        if (mapRequestBean == null) {
            showAlert("Richiesta di mappa non trovata in memoria.");
            return;
        }

        try {
            Map map = facade.getMapAC().showMap(mapRequestBean);
            setMapAndRequest(mapRequestBean, map);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Errore nel caricamento della mappa o dei driver.");
        }
    }

    @FXML
    public void initialize() {
        //System.out.println("[SelectTaxiGUIController] initialize() start");
        initializeTable();
        initializePaymentChoices();
        destinationField.setEditable(false);
    }


    private void initializeTable() {
        driverNameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
        carModelColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getVehicleInfo()));
        plateColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getVehiclePlate()));
        // ETA come stringa formattata (es. "1h 05min" o "45min")
        etaColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getEstimatedTimeFormatted()));
        // Prezzo formattato (es. "€12.30")
        priceColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getEstimatedPriceFormatted()));
    }


    private void initializePaymentChoices() {
        paymentChoiceBox.getItems().clear();
        // Popolo con versioni leggibili dell'enum PaymentMethod (es. "Cash", "Card")
        for (PaymentMethod pm : PaymentMethod.values()) {
            String displayName = pm.name().charAt(0) + pm.name().substring(1).toLowerCase();
            paymentChoiceBox.getItems().add(displayName);
        }

        // Se c'è un metodo pagamento selezionato in memoria, lo seleziono
        if (tempMemory.getSelectedPaymentMethod() != null) {
            String savedMethod = tempMemory.getSelectedPaymentMethod();
            paymentChoiceBox.getSelectionModel().select(savedMethod);
        } else {
            paymentChoiceBox.getSelectionModel().selectFirst();
        }
    }

    // Metodo per impostare la mappa e la richiesta di corsa.
    public void setMapAndRequest(MapRequestBean bean, Map map) {
        //System.out.println("setMapAndRequest chiamato con bean: " + bean + ", map: " + map);
        if (bean == null || map == null || map.getHtmlContent() == null) {
            showAlert("Dati mappa o richiesta non validi.");
            return;
        }
        this.mapRequestBean = bean;
        destinationField.setText(bean.getDestination());

        try {
            // Ottengo la lista base di driver vicini (con solo posizione ecc.)
            List<AvailableDriverBean> baseDrivers = facade.getDriverMatchingAC().findAvailableDrivers(bean);
            //System.out.println("Numero driver trovati: " + baseDrivers.size());

            // Utente attuale
            CoordinateBean userPos = bean.getOrigin();
            String destination = bean.getDestination();

            // Lista arricchita con ETA e Prezzo reali
            for (AvailableDriverBean driver : baseDrivers) {
                CoordinateBean driverPos = driver.getCoordinate();
                if (driverPos == null || userPos == null) continue;

                String userPosStr = userPos.getLatitude() + "," + userPos.getLongitude();

                MapRequestBean etaRequest = new MapRequestBean(driverPos, userPosStr, 0);
                Map etaMap = facade.getMapAC().showMap(etaRequest);
                double eta = etaMap.getEstimatedTimeMinutes();

                MapRequestBean rideReq = new MapRequestBean(userPos, destination, 0);
                Map rideMap = facade.getMapAC().showMap(rideReq);

                double km = rideMap.getDistanceKm();
                double min = rideMap.getEstimatedTimeMinutes();

                double estimatedFare = km * 1.0 + min * 0.20;

                driver.setEstimatedTime(eta + min);  // tempo totale attesa + corsa
                driver.setEstimatedPrice(estimatedFare);
            }

            // Aggiorna tabella
            taxiTable.getItems().setAll(baseDrivers);

            // Salva in memoria
            tempMemory.setMapRequestBean(bean);
            tempMemory.setAvailableDrivers(baseDrivers);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Errore nel caricamento dei driver disponibili.");
        }

        mapView.getEngine().loadContent(map.getHtmlContent());
    }


    @FXML
    private void onConfirmRide() {
        AvailableDriverBean selectedDriver = taxiTable.getSelectionModel().getSelectedItem();
        if (selectedDriver == null) {
            showAlert("Seleziona un driver dalla tabella.");
            return;
        }

        String paymentMethodStr = paymentChoiceBox.getSelectionModel().getSelectedItem();
        if (paymentMethodStr == null || paymentMethodStr.isEmpty()) {
            showAlert("Seleziona un metodo di pagamento.");
            return;
        }

        PaymentMethod paymentMethodEnum;
        try {
            paymentMethodEnum = PaymentMethod.valueOf(paymentMethodStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            showAlert("Metodo di pagamento non valido.");
            return;
        }

        ClientBean currentClient = ClientBean.fromModel(SessionManager.getInstance().getLoggedClient());
        if (currentClient == null) {
            showAlert("Utente non loggato o client non trovato.");
            return;
        }

        try {
            RideRequestBean rideRequest = new RideRequestBean(
                    mapRequestBean.getOrigin(),
                    mapRequestBean.getDestination(),
                    mapRequestBean.getRadiusKm(),
                    paymentMethodEnum
            );

            rideRequest.setDriver(selectedDriver);
            rideRequest.setClient(currentClient);

            tempMemory.setSelectedDriver(selectedDriver);
            tempMemory.setSelectedPaymentMethod(paymentMethodStr.toUpperCase());

            RideRequestBean savedRequest = facade.getDriverMatchingAC().saveRideRequest(rideRequest);

            DriverAssignmentBean assignmentBean = new DriverAssignmentBean(
                    savedRequest.getRequestID(),
                    selectedDriver.toModel()
            );
            facade.getDriverMatchingAC().assignDriverToRequest(assignmentBean);

            TaxiRideConfirmationBean confirmationBean = new TaxiRideConfirmationBean(
                    savedRequest.getRequestID(),
                    DriverBean.fromModel(selectedDriver.toModel()),
                    ClientBean.fromModel(currentClient.toModel()),
                    savedRequest.getOriginAsCoordinateBean(),
                    savedRequest.getDestination(),
                    RideConfirmationStatus.PENDING,
                    selectedDriver.getEstimatedPrice(),
                    selectedDriver.getEstimatedTime(),
                    savedRequest.getPaymentMethod(),
                    LocalDateTime.now()
            );

            tempMemory.setRideConfirmation(confirmationBean);

            SceneNavigator.switchTo("/org/ispw/fastridetrack/views/SelectDriver.fxml", "Conferma Driver");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Errore nella conferma della corsa.");
        }
    }

    @FXML
    private void onGoBackHome() {
        try {
            SceneNavigator.switchTo("/org/ispw/fastridetrack/views/Home.fxml", "Home");
        } catch (FXMLLoadException e) {
            e.printStackTrace();
            showAlert("Errore nel ritorno alla schermata Home.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attenzione");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}








