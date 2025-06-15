package org.ispw.fastridetrack.dao.MYSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SingletonDBSession {
    private static SingletonDBSession instance;
    private static Connection connection;

    private SingletonDBSession() {
        try {
            // Leggo le credenziali dal file di configurazione delle variabili d'ambiente
            String url = System.getenv("DB_URL");
            String username = System.getenv("DB_USERNAME");
            String password = System.getenv("DB_PASSWORD");

            // Verifico che le variabili d'ambiente non siano null
            if (url == null || username == null || password == null) {
                throw new RuntimeException("Le variabili d'ambiente per il database non sono state configurate correttamente.");
            }

            System.out.println("[DEBUG] Connessione a MySQL...");
            System.out.println("URL: " + url);
            System.out.println("Utente: " + username);

            connection = DriverManager.getConnection(url, username, password);
            System.out.println("[DEBUG] Connessione stabilita.");
        } catch (SQLException e) {
            System.err.println("[ERRORE] Connessione MySQL fallita:");
            e.printStackTrace();
            throw new RuntimeException("Connessione al database non riuscita", e);
        }
    }

    // Metodo per ottenere l'istanza del Singleton
    public static SingletonDBSession getInstance() {
        if (instance == null) {
            synchronized (SingletonDBSession.class) {
                if (instance == null) {
                    instance = new SingletonDBSession();
                }
            }
        }
        return instance;
    }

    // Metodo per ottenere la connessione al database
    public Connection getConnection() {
        return connection;
    }

    // Metodo per chiudere la connessione al database
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) { /* log */ }
        }
    }

    public static synchronized void reset() {
        if (instance != null) {
            instance.closeConnection();
            instance = null;
            connection = null;
        }
    }
}



