package org.ispw.fastridetrack.dao.MYSQL;

import org.ispw.fastridetrack.dao.ClientDAO;
import org.ispw.fastridetrack.model.Client;
import org.ispw.fastridetrack.model.PaymentMethod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientDAOMYSQL implements ClientDAO {

    private final Connection connection;

    public ClientDAOMYSQL(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Client client) {
        String sql = "INSERT INTO client (userId, username, password, name, email, phonenumber, paymentMethod, latitude, longitude) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, client.getUserID());
            stmt.setString(2, client.getUsername());
            stmt.setString(3, client.getPassword());
            stmt.setString(4, client.getName());
            stmt.setString(5, client.getEmail());
            stmt.setString(6, client.getPhoneNumber());
            stmt.setString(7, String.valueOf(client.getPaymentMethod()));
            stmt.setDouble(8, client.getLatitude());
            stmt.setDouble(9, client.getLongitude());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.err.println("Save client failed: no rows affected.");
            }
        } catch (SQLException e) {
            System.err.println("SQL error during save client:");
            e.printStackTrace();
        }
    }

    @Override
    public Client findById(Integer id_client) {
        String sql = "SELECT * FROM client WHERE userID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id_client);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractClientFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il recupero del client con ID " + id_client, e);
        }
        return null;
    }

    @Override
    public Client retrieveClientByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM client WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractClientFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il login del client", e);
        }
        return null;
    }

    // Metodo privato di supporto per evitare duplicazione codice
    private Client extractClientFromResultSet(ResultSet rs) throws SQLException {
        Integer userID = rs.getInt("userID");
        String uname = rs.getString("username");
        String pwd = rs.getString("password");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String phone = rs.getString("phoneNumber");
        PaymentMethod paymentMethod = PaymentMethod.valueOf(rs.getString("paymentMethod"));
        double latitude = rs.getDouble("latitude");
        double longitude = rs.getDouble("longitude");

        Client client = new Client(userID, uname, pwd, name, email, phone, paymentMethod);
        client.setLatitude(latitude);
        client.setLongitude(longitude);
        return client;
    }
}


