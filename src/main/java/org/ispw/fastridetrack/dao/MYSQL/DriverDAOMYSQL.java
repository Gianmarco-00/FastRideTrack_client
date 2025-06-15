package org.ispw.fastridetrack.dao.MYSQL;

import org.ispw.fastridetrack.bean.AvailableDriverBean;
import org.ispw.fastridetrack.bean.DriverBean;
import org.ispw.fastridetrack.dao.DriverDAO;
import org.ispw.fastridetrack.model.Coordinate;
import org.ispw.fastridetrack.model.Driver;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverDAOMYSQL implements DriverDAO {
    private final Connection connection;
    private static final String GOOGLE_API_KEY = System.getenv("GOOGLE_MAPS_API_KEY"); // Sostituisci con la tua chiave

    public DriverDAOMYSQL(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Driver driver) {
        String sql = "INSERT INTO driver (userId, username, password, name, email, phonenumber, latitude, longitude, vehicleInfo, vehiclePlate, affiliation, available) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, driver.getUserID());
            stmt.setString(2, driver.getUsername());
            stmt.setString(3, driver.getPassword());
            stmt.setString(4, driver.getName());
            stmt.setString(5, driver.getEmail());
            stmt.setString(6, driver.getPhoneNumber());
            stmt.setDouble(7, driver.getLatitude());
            stmt.setDouble(8, driver.getLongitude());
            stmt.setString(9, driver.getVehicleInfo());
            stmt.setString(10, driver.getVehiclePlate());
            stmt.setString(11, driver.getAffiliation());
            stmt.setInt(12, driver.isAvailable() ? 1 : 0);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL error during save driver:");
            e.printStackTrace();
        }
    }

    @Override
    public Driver retrieveDriverByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM driver WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractDriverFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error during retrieveDriverByUsernameAndPassword:");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Driver findById(int id_driver) {
        String sql = "SELECT * FROM driver WHERE userID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id_driver);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractDriverFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error during findById:");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<AvailableDriverBean> findDriversAvailableWithinRadius(Coordinate origin, int radiusKm) {
        List<AvailableDriverBean> availableDrivers = new ArrayList<>();
        String sql = "SELECT * FROM (\n" +
                "  SELECT *, \n" +
                "    (6371 * acos(\n" +
                "      cos(radians(?)) * cos(radians(latitude)) * cos(radians(longitude) - radians(?)) +\n" +
                "      sin(radians(?)) * sin(radians(latitude))\n" +
                "    )) AS distance \n" +
                "  FROM driver \n" +
                "  WHERE available = 1\n" +
                "    AND latitude BETWEEN ? AND ?\n" +
                "    AND longitude BETWEEN ? AND ?\n" +
                ") AS sub\n" +
                "WHERE distance <= ?\n" +
                "ORDER BY distance ASC";

        double lat = origin.getLatitude();
        double lon = origin.getLongitude();
        double radiusLatDegrees = radiusKm / 111.0;
        double radiusLonDegrees = radiusKm / (111.320 * Math.cos(Math.toRadians(lat)));

        double minLat = lat - radiusLatDegrees;
        double maxLat = lat + radiusLatDegrees;
        double minLon = lon - radiusLonDegrees;
        double maxLon = lon + radiusLonDegrees;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, lat);
            stmt.setDouble(2, lon);
            stmt.setDouble(3, lat);
            stmt.setDouble(4, minLat);
            stmt.setDouble(5, maxLat);
            stmt.setDouble(6, minLon);
            stmt.setDouble(7, maxLon);
            stmt.setDouble(8, radiusKm);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Driver driver = extractDriverFromResultSet(rs);
                    Coordinate destination = new Coordinate(driver.getLatitude(), driver.getLongitude());

                    RouteInfo info = getRouteInfoFromGoogleMaps(origin, destination);
                    double estimatedTime = (info != null) ? info.durationMin : calculateEstimatedTime(origin, destination);
                    double estimatedPrice = (info != null) ? calculateEstimatedPrice(info.distanceKm) : calculateEstimatedPrice(origin, destination);

                    AvailableDriverBean availableDriver = new AvailableDriverBean(
                            DriverBean.fromModel(driver),
                            estimatedTime,
                            estimatedPrice
                    );
                    availableDrivers.add(availableDriver);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return availableDrivers;
    }

    private Driver extractDriverFromResultSet(ResultSet rs) throws SQLException {
        return new Driver(
                rs.getInt("userID"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phoneNumber"),
                rs.getDouble("latitude"),
                rs.getDouble("longitude"),
                rs.getString("vehicleInfo"),
                rs.getString("vehiclePlate"),
                rs.getString("affiliation"),
                rs.getInt("available") == 1
        );
    }

    private static class RouteInfo {
        double distanceKm;
        double durationMin;

        public RouteInfo(double distanceKm, double durationMin) {
            this.distanceKm = distanceKm;
            this.durationMin = durationMin;
        }
    }

    private RouteInfo getRouteInfoFromGoogleMaps(Coordinate origin, Coordinate dest) {
        try {
            String urlStr = String.format(
                    "https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&key=%s",
                    origin.getLatitude(), origin.getLongitude(),
                    dest.getLatitude(), dest.getLongitude(),
                    GOOGLE_API_KEY
            );
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                responseBuilder.append(line);
            }
            in.close();

            JSONObject json = new JSONObject(responseBuilder.toString());
            JSONArray routes = json.getJSONArray("routes");
            if (routes.length() > 0) {
                JSONObject leg = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0);
                double distanceMeters = leg.getJSONObject("distance").getDouble("value");
                double durationSeconds = leg.getJSONObject("duration").getDouble("value");
                return new RouteInfo(distanceMeters / 1000.0, durationSeconds / 60.0);
            }
        } catch (Exception e) {
            System.err.println("Error retrieving route info from Google Maps: " + e.getMessage());
        }
        return null;
    }

    private double calculateEstimatedTime(Coordinate origin, Coordinate dest) {
        double distanceKm = calculateDistanceKm(origin.getLatitude(), origin.getLongitude(), dest.getLatitude(), dest.getLongitude());
        double averageSpeedKmPerH = 40.0;
        return (distanceKm / averageSpeedKmPerH) * 60;
    }

    private double calculateEstimatedPrice(Coordinate origin, Coordinate dest) {
        double distanceKm = calculateDistanceKm(origin.getLatitude(), origin.getLongitude(), dest.getLatitude(), dest.getLongitude());
        return calculateEstimatedPrice(distanceKm);
    }

    private double calculateEstimatedPrice(double distanceKm) {
        double baseFare = 3.0;
        double costPerKm = 1.2;
        return baseFare + (costPerKm * distanceKm);
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}




