package org.ispw.fastridetrack.dao;

import org.ispw.fastridetrack.bean.AvailableDriverBean;
import org.ispw.fastridetrack.model.Coordinate;
import org.ispw.fastridetrack.model.Driver;


import java.util.List;

public interface DriverDAO {
    void save(Driver driver);
    Driver findById(int id_driver);
    Driver retrieveDriverByUsernameAndPassword(String username, String password);
    List<AvailableDriverBean> findDriversAvailableWithinRadius(Coordinate origin, int radiusKm);
    //void updateAvailability(int driverId, boolean isAvailable);
}



