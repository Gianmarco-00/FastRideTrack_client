package org.ispw.fastridetrack.model.Session;

import org.ispw.fastridetrack.dao.*;
import org.ispw.fastridetrack.dao.MYSQL.*;

public class PersistenceSessionFactory implements SessionFactory {

    private final SingletonDBSession dbSession = SingletonDBSession.getInstance();

    @Override
    public ClientDAO createClientDAO() {
        return new ClientDAOMYSQL(dbSession.getConnection());
    }

    @Override
    public DriverDAO createDriverDAO() {
        return new DriverDAOMYSQL(dbSession.getConnection());
    }

    @Override
    public RideRequestDAO createRideRequestDAO() {
        ClientDAO clientDAO = createClientDAO();
        return new RideRequestDAOMYSQL(dbSession.getConnection(), clientDAO);
    }

    @Override
    public TaxiRideDAO createTaxiRideDAO() {
        return new TaxiRideDAOMYSQL(dbSession.getConnection());
    }
}


