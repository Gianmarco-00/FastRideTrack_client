package org.ispw.fastridetrack.session;

import org.ispw.fastridetrack.dao.ClientDAO;
import org.ispw.fastridetrack.dao.DriverDAO;
import org.ispw.fastridetrack.dao.filesystem.ClientDAOFileSystem;
import org.ispw.fastridetrack.dao.filesystem.DriverDAOFileSystem;
import org.ispw.fastridetrack.dao.filesystem.RideRequestDAOFileSystem;
import org.ispw.fastridetrack.dao.filesystem.TaxiRideDAOFileSystem;
import org.ispw.fastridetrack.dao.RideRequestDAO;
import org.ispw.fastridetrack.dao.TaxiRideDAO;

public class FileSystemSessionFactory implements SessionFactory {

    private final ClientDAOFileSystem clientDAO = new ClientDAOFileSystem();
    private final DriverDAOFileSystem driverDAO = new DriverDAOFileSystem();

    public FileSystemSessionFactory() {
        String mode = System.getenv("USE_PERSISTENCE");
        if (!"file".equalsIgnoreCase(mode)) {
            throw new IllegalStateException("FileSystemSessionFactory può essere usata solo con USE_PERSISTENCE=file");
        }
    }

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


