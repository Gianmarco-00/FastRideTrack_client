package org.ispw.fastridetrack.model.Session;

import org.ispw.fastridetrack.dao.ClientDAO;
import org.ispw.fastridetrack.dao.DriverDAO;
import org.ispw.fastridetrack.dao.FileSystem.ClientDAOFileSystem;
import org.ispw.fastridetrack.dao.FileSystem.DriverDAOFileSystem;
import org.ispw.fastridetrack.dao.FileSystem.RideRequestDAOFileSystem;
import org.ispw.fastridetrack.dao.FileSystem.TaxiRideDAOFileSystem;
import org.ispw.fastridetrack.dao.RideRequestDAO;
import org.ispw.fastridetrack.dao.TaxiRideDAO;

public class FileSystemSessionFactory implements SessionFactory {

    private final ClientDAOFileSystem clientDAO = new ClientDAOFileSystem();
    private final DriverDAOFileSystem driverDAO = new DriverDAOFileSystem();

    @Override
    public ClientDAO createClientDAO() {
        return clientDAO;
    }

    @Override
    public DriverDAO createDriverDAO() {
        return driverDAO;
    }

    @Override
    public RideRequestDAO createRideRequestDAO() {
        return new RideRequestDAOFileSystem(clientDAO);
    }

    @Override
    public TaxiRideDAO createTaxiRideDAO() {
        return new TaxiRideDAOFileSystem(clientDAO, driverDAO);
    }
}


