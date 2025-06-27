package org.ispw.fastridetrack.session;

import org.ispw.fastridetrack.dao.ClientDAO;
import org.ispw.fastridetrack.dao.DriverDAO;
import org.ispw.fastridetrack.dao.RideRequestDAO;
import org.ispw.fastridetrack.dao.TaxiRideConfirmationDAO;

public interface SessionFactory {
    ClientDAO createClientDAO();
    DriverDAO createDriverDAO();
    RideRequestDAO createRideRequestDAO();
    TaxiRideConfirmationDAO createTaxiRideDAO();
}