package org.ispw.fastridetrack.model.Session;

import org.ispw.fastridetrack.dao.ClientDAO;
import org.ispw.fastridetrack.dao.DriverDAO;
import org.ispw.fastridetrack.dao.RideRequestDAO;
import org.ispw.fastridetrack.dao.TaxiRideDAO;

public interface SessionFactory {
    ClientDAO createClientDAO();
    DriverDAO createDriverDAO();
    RideRequestDAO createRideRequestDAO();
    TaxiRideDAO createTaxiRideDAO();
}