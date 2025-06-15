package org.ispw.fastridetrack.dao;

import org.ispw.fastridetrack.model.RideRequest;

public interface RideRequestDAO {
    RideRequest save(RideRequest rideRequest);
    RideRequest findById(int requestID);
    void update(RideRequest rideRequest);
}

