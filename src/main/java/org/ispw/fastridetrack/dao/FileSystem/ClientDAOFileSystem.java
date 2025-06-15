package org.ispw.fastridetrack.dao.FileSystem;

import org.ispw.fastridetrack.dao.ClientDAO;
import org.ispw.fastridetrack.model.Client;
import org.ispw.fastridetrack.model.PaymentMethod;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAOFileSystem implements ClientDAO {
    private static final String FILE_PATH = "src/data/clients.csv";

    public ClientDAOFileSystem() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Errore nella creazione del file client CSV: " + e.getMessage());
            }
        }

    }

    @Override
    public void save(Client client) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String record = String.join(";",
                    String.valueOf(client.getUserID()),
                    client.getUsername(),
                    client.getPassword(),
                    client.getName(),
                    client.getEmail(),
                    client.getPhoneNumber(),
                    client.getPaymentMethod().name(),
                    String.valueOf(client.getLatitude()),
                    String.valueOf(client.getLongitude())
            );
            writer.write(record);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio del client: " + e.getMessage());
        }
    }

    @Override
    public Client findById(Integer id_client) {
        for (Client client : getAllClients()) {
            if (client.getUserID().equals(id_client)) {
                return client;
            }
        }
        return null;
    }

    @Override
    public Client retrieveClientByUsernameAndPassword(String username, String password) {
        for (Client client : getAllClients()) {
            if (client.getUsername().equals(username) && client.getPassword().equals(password)) {
                return client;
            }
        }
        return null;
    }

    private List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_PATH), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";", -1);
                if (tokens.length < 9) {
                    continue;
                }

                try {
                    Client client = new Client(
                            Integer.parseInt(tokens[0]),
                            tokens[1],
                            tokens[2],
                            tokens[3],
                            tokens[4],
                            tokens[5],
                            PaymentMethod.valueOf(tokens[6])
                    );
                    client.setLatitude(Double.parseDouble(tokens[7]));
                    client.setLongitude(Double.parseDouble(tokens[8]));
                    clients.add(client);
                } catch (Exception ignored) {
                    // Salta righe malformate
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura dei client da file: " + e.getMessage());
        }

        return clients;
    }
}



