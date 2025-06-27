package org.ispw.fastridetrack.dao;

import org.ispw.fastridetrack.model.TaxiRideConfirmation;

import java.util.Optional;

public interface TaxiRideDAO {
    void save(TaxiRideConfirmation ride);
    Optional<TaxiRideConfirmation> findById(int rideID);
    void update(TaxiRideConfirmation ride);
    boolean exists(int rideID);
}


