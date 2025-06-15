package org.ispw.fastridetrack.exception;

public class RideNotFoundException extends RuntimeException {
    public RideNotFoundException(int rideID) {
        super("Conferma corsa con ID " + rideID + " non trovata");
    }
}

