package org.ispw.fastridetrack.model.Session;

import org.ispw.fastridetrack.dao.*;
import org.ispw.fastridetrack.dao.InMemory.*;

public class InMemorySessionFactory implements SessionFactory {

    @Override
    public ClientDAO createClientDAO() {
        return new ClientDAOInMemory();
    }

    @Override
    public DriverDAO createDriverDAO() {
        return new DriverDAOInMemory();
    }

    @Override
    public RideRequestDAO createRideRequestDAO() {
        return new RideRequestDAOInMemory();
    }

    @Override
    public TaxiRideDAO createTaxiRideDAO() {
        return new TaxiRideDAOInMemory();
    }
}

