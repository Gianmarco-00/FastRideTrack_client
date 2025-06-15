package org.ispw.fastridetrack.dao.Adapter;

import org.ispw.fastridetrack.bean.MapRequestBean;
import org.ispw.fastridetrack.model.Map;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.*;

public class GoogleMapsAdapter implements MapService {
    private static final String API_KEY = System.getenv("GOOGLE_MAPS_API_KEY");

    @Override
    public Map calculateRoute(MapRequestBean requestBean) {
        if (requestBean == null) {
            throw new IllegalArgumentException("MapRequestBean non può essere nullo");
        }

        String from = requestBean.getOriginAsString();
        String to = requestBean.getDestination();

        String html = generateRouteHtml(from, to);

        double estimatedTimeMinutes = -1.0;
        double distanceKm = -1.0;

        try {
            // Costruisci URL per Distance Matrix API
            String encodedOrigin = URLEncoder.encode(from, StandardCharsets.UTF_8);
            String encodedDestination = URLEncoder.encode(to, StandardCharsets.UTF_8);

            String url = "https://maps.googleapis.com/maps/api/distancematrix/json"
                    + "?origins=" + encodedOrigin
                    + "&destinations=" + encodedDestination
                    + "&mode=driving"
                    + "&language=it"
                    + "&key=" + API_KEY;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                String status = json.get("status").getAsString();

                if ("OK".equals(status)) {
                    JsonArray rows = json.getAsJsonArray("rows");
                    JsonObject element = rows.get(0).getAsJsonObject()
                            .getAsJsonArray("elements")
                            .get(0).getAsJsonObject();

                    String elementStatus = element.get("status").getAsString();
                    if ("OK".equals(elementStatus)) {
                        int durationSeconds = element.getAsJsonObject("duration").get("value").getAsInt();
                        int distanceMeters = element.getAsJsonObject("distance").get("value").getAsInt();

                        estimatedTimeMinutes = durationSeconds / 60.0;
                        distanceKm = distanceMeters / 1000.0;
                    } else {
                        throw new IllegalStateException("Errore nella risposta Distance Matrix: " + elementStatus);
                    }
                } else {
                    throw new IllegalStateException("Errore API Distance Matrix: " + status);
                }
            } else {
                throw new RuntimeException("Errore HTTP Distance Matrix: " + response.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Errore nel calcolo della distanza o tempo stimato", e);
        }

        return new Map(html, from, to, distanceKm, estimatedTimeMinutes);
    }



    private String generateRouteHtml(String origin, String destination) {
        String encodedOrigin = URLEncoder.encode(origin, StandardCharsets.UTF_8);
        String encodedDestination = URLEncoder.encode(destination, StandardCharsets.UTF_8);

        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IllegalStateException("Google Maps API key non configurata");
        }

        StringBuilder src = new StringBuilder();
        src.append("https://www.google.com/maps/embed/v1/directions")
                .append("?key=").append(API_KEY)
                .append("&origin=").append(encodedOrigin)
                .append("&destination=").append(encodedDestination)
                .append("&mode=driving")            // modalità di trasporto: driving, walking, bicycling, transit
                .append("&language=it")
                .append("&region=IT");              // formato regionale e confini adatti all’Italia

        return "<iframe width=\"100%\" height=\"100%\" frameborder=\"0\" style=\"border:0\" "
                + "src=\"" + src.toString() + "\" allowfullscreen></iframe>";
    }

    // Metodo pubblico per ottenere l'indirizzo (reverse geocoding) a partire da latitudine e longitudine.

    public String getAddressFromCoordinates(double latitude, double longitude) {
        if (API_KEY == null || API_KEY.isBlank()) {
            return "Indirizzo non disponibile (API key mancante)";
        }

        try {
            String latlngParam = latitude + "," + longitude;
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" +
                    URLEncoder.encode(latlngParam, StandardCharsets.UTF_8) +
                    "&key=" + API_KEY;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                String status = json.get("status").getAsString();

                if ("OK".equals(status)) {
                    JsonArray results = json.getAsJsonArray("results");
                    if (results.size() > 0) {
                        JsonObject firstResult = results.get(0).getAsJsonObject();
                        return firstResult.get("formatted_address").getAsString();
                    } else {
                        return "Indirizzo non trovato";
                    }
                } else {
                    return "Errore API: " + status;
                }
            } else {
                return "Errore HTTP: " + response.statusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Errore durante il reverse geocoding";
        }
    }
}









