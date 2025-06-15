package org.ispw.fastridetrack.controller.ApplicationController;

import org.ispw.fastridetrack.bean.*;
import org.ispw.fastridetrack.dao.DriverDAO;
import org.ispw.fastridetrack.dao.RideRequestDAO;
import org.ispw.fastridetrack.model.Coordinate;
import org.ispw.fastridetrack.model.Driver;
import org.ispw.fastridetrack.model.RideRequest;
import org.ispw.fastridetrack.model.Session.SessionManager;

import java.util.List;
import java.util.Objects;

public class DriverMatchingApplicationController {

    private final RideRequestDAO rideRequestDAO;
    private final DriverDAO driverDAO;

    public DriverMatchingApplicationController() {
        this.rideRequestDAO = SessionManager.getInstance().getRideRequestDAO();
        this.driverDAO = SessionManager.getInstance().getDriverDAO();
    }

    // Assegno un driver a una richiesta usando il bean DriverAssignmentBean.
    public void assignDriverToRequest(DriverAssignmentBean assignmentBean) {
        Objects.requireNonNull(assignmentBean, "DriverAssignmentBean non può essere nullo");
        assignDriverToRequest(assignmentBean.getRequestID(), assignmentBean.getDriver().getUserID());
    }

    // Assegno un driver alla richiesta di corsa e aggiorno la persistenza.
    public void assignDriverToRequest(int requestID, int driverID) {
        RideRequest model = rideRequestDAO.findById(requestID);
        Driver driver = driverDAO.findById(driverID);

        Objects.requireNonNull(model, "RideRequest con ID " + requestID + " non trovato");
        Objects.requireNonNull(driver, "Driver con ID " + driverID + " non trovato");

        model.setDriver(driver);
        rideRequestDAO.update(model);
    }

    // Trovo i driver disponibili entro il raggio fornito dal MapRequestBean.
    public List<AvailableDriverBean> findAvailableDrivers(MapRequestBean mapRequestBean) {
        Objects.requireNonNull(mapRequestBean, "MapRequestBean non può essere nullo");


        CoordinateBean originBean = mapRequestBean.getOrigin();
        int radiusKm = mapRequestBean.getRadiusKm();

        Coordinate origin = originBean.toModel();
        System.out.println("DEBUG - Coordinate partenza: " + origin.getLatitude() + ", " + origin.getLongitude());
        System.out.println("DEBUG - Raggio km: " + radiusKm);


        return driverDAO.findDriversAvailableWithinRadius(origin, radiusKm);

    }


    // Salvo una nuova richiesta di corsa a partire dal bean.
    public RideRequestBean saveRideRequest(RideRequestBean rideRequestBean) {
        Objects.requireNonNull(rideRequestBean, "RideRequestBean non può essere nullo");

        // Conversione: Bean → Model
        RideRequest model = rideRequestBean.toModel();

        // Salvataggio nel DAO (Model)
        RideRequest savedModel = rideRequestDAO.save(model);

        if (savedModel == null) {
            throw new RuntimeException("Errore durante il salvataggio della richiesta di corsa");
        }

        // Conversione: Model → Bean
        return RideRequestBean.fromModel(savedModel);
    }

}



