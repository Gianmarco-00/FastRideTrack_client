package org.ispw.fastridetrack.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ispw.fastridetrack.model.Coordinate;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IPLocationService {
    public static Coordinate getCoordinateFromIP(String ip) throws Exception {
        URL url = new URL("http://ip-api.com/json/" + ip);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        JsonObject response = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();

        if ("success".equals(response.get("status").getAsString())) {
            double lat = response.get("lat").getAsDouble();
            double lon = response.get("lon").getAsDouble();
            return new Coordinate(lat, lon);
        } else {
            throw new Exception("Impossibile determinare la posizione dall'IP.");
        }
    }
}
