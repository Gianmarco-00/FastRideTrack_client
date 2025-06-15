package org.ispw.fastridetrack.model;

public class Map {

    private final String htmlContent;
    private final String origin;
    private final String destination;
    private final double distanceKm;
    private final double estimatedTimeMinutes;

    public Map(String htmlContent, String origin, String destination, double distanceKm, double estimatedTimeMinutes) {
        this.htmlContent = htmlContent;
        this.origin = origin;
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }
}
