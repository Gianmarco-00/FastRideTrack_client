package org.ispw.fastridetrack.bean;

import org.ispw.fastridetrack.model.Coordinate;

public class CoordinateBean {
    private double latitude;
    private double longitude;

    public CoordinateBean(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public CoordinateBean(Coordinate coordinate) {
        if (coordinate != null) {
            this.latitude = coordinate.getLatitude();
            this.longitude = coordinate.getLongitude();
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Coordinate toModel() {
        return new Coordinate(latitude, longitude);
    }
}

