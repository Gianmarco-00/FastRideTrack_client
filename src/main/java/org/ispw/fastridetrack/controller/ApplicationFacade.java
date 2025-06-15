package org.ispw.fastridetrack.controller;

import org.ispw.fastridetrack.controller.ApplicationController.ClientRideManagementApplicationController;
import org.ispw.fastridetrack.controller.ApplicationController.DriverMatchingApplicationController;
import org.ispw.fastridetrack.controller.ApplicationController.LoginApplicationController;
import org.ispw.fastridetrack.controller.ApplicationController.MapApplicationController;

public class ApplicationFacade {

    private final LoginApplicationController loginAC;
    private final DriverMatchingApplicationController driverMatchingAC;
    private final ClientRideManagementApplicationController clientRideManagementAC;
    private final MapApplicationController mapAC;

    public ApplicationFacade() {
        this.loginAC = new LoginApplicationController();
        this.driverMatchingAC = new DriverMatchingApplicationController();
        this.clientRideManagementAC = new ClientRideManagementApplicationController();
        this.mapAC = new MapApplicationController();
    }
    public LoginApplicationController getLoginAC() {
        return loginAC;
    }
    public DriverMatchingApplicationController getDriverMatchingAC() {
        return driverMatchingAC;
    }
    public ClientRideManagementApplicationController getClientRideManagementAC() {
        return clientRideManagementAC;
    }
    public MapApplicationController getMapAC() {
        return mapAC;
    }
}
