package org.ispw.fastridetrack.model.Session;

import org.ispw.fastridetrack.dao.Adapter.EmailService;
import org.ispw.fastridetrack.dao.Adapter.GmailAdapter;
import org.ispw.fastridetrack.dao.Adapter.GoogleMapsAdapter;
import org.ispw.fastridetrack.dao.Adapter.MapService;
import org.ispw.fastridetrack.dao.ClientDAO;
import org.ispw.fastridetrack.dao.DriverDAO;
import org.ispw.fastridetrack.dao.MYSQL.SingletonDBSession;
import org.ispw.fastridetrack.dao.RideRequestDAO;
import org.ispw.fastridetrack.dao.TaxiRideDAO;
import org.ispw.fastridetrack.model.Client;
import org.ispw.fastridetrack.model.Driver;

public class SessionManager {

    private static SessionManager instance;
    private final boolean persistenceEnabled;
    private final SessionFactory sessionFactory;
    private Client loggedClient;
    private Driver loggedDriver;

    private SessionManager() {
        // Carico la configurazione dalla variabile d'ambiente
        String usePersistenceEnv = System.getenv("USE_PERSISTENCE");

        // Determino la session factory in base al valore della variabile
        if ("true".equalsIgnoreCase(usePersistenceEnv)) {
            this.persistenceEnabled = true;
            this.sessionFactory = new PersistenceSessionFactory();
        } else if ("file".equalsIgnoreCase(usePersistenceEnv)) {
            this.persistenceEnabled = true;
            this.sessionFactory = new FileSystemSessionFactory();
        } else {
            this.persistenceEnabled = false;
            this.sessionFactory = new InMemorySessionFactory();
        }

        // Adapter unificati per Gmail e GoogleMaps
        this.mapService = new GoogleMapsAdapter();
        this.emailService = new GmailAdapter();
    }


    public static void init() {
        if (instance == null) {
            instance = new SessionManager();
        }
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SessionManager non inizializzato. Chiama init() prima.");
        }
        return instance;
    }

    public ClientDAO getClientDAO() { return sessionFactory.createClientDAO(); }

    public DriverDAO getDriverDAO() { return sessionFactory.createDriverDAO(); }

    public RideRequestDAO getRideRequestDAO() {
        return sessionFactory.createRideRequestDAO();
    }

    public TaxiRideDAO getTaxiRideDAO() {
        return sessionFactory.createTaxiRideDAO();
    }


    // === CLIENT ===
    public Client getLoggedClient() {
        return loggedClient;
    }

    public void setLoggedClient(Client client) {
        this.loggedClient = client;
    }

    // === DRIVER ===
    public Driver getLoggedDriver() {
        return loggedDriver;
    }

    public void setLoggedDriver(Driver driver) {
        this.loggedDriver = driver;
    }

    // === CLEAR SESSION ===
    public void clearSession() {
        System.out.println("Sessione utente terminata.");
        this.loggedClient = null;
        this.loggedDriver = null;
    }

    // === SERVIZI ESTERNI ===
    private final MapService mapService;
    private final EmailService emailService;

    public MapService getMapService() {
        return mapService;
    }

    public EmailService getEmailService() {
        return emailService;
    }

    // === CHIUSURA PULITA DEL DB ===
    public void shutdown() {
        if (persistenceEnabled) {
            SingletonDBSession.reset();
            System.out.println("Connessione DB chiusa e singleton resettato.");
        }
    }

}



