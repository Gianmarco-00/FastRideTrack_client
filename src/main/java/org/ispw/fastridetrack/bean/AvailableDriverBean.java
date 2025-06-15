package org.ispw.fastridetrack.bean;

public class AvailableDriverBean extends DriverBean {

    private double estimatedTime;   // Tempo stimato in minuti
    private double estimatedPrice;  // Prezzo stimato in valuta

    public AvailableDriverBean(DriverBean driverBean, double estimatedTime, double estimatedPrice) {
        super(
                driverBean.getUsername(),
                driverBean.getPassword(),
                driverBean.getUserID(),
                driverBean.getName(),
                driverBean.getEmail(),
                driverBean.getPhoneNumber(),
                driverBean.getLatitude(),
                driverBean.getLongitude(),
                driverBean.getVehicleInfo(),
                driverBean.getVehiclePlate(),
                driverBean.getAffiliation(),
                driverBean.isAvailable()
        );
        this.estimatedTime = estimatedTime;
        this.estimatedPrice = estimatedPrice;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public void setEstimatedPrice(double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    // Metodo aggiuntivo per formattare il tempo in "Xh YYmin"
    public String getEstimatedTimeFormatted() {
        int hrs = (int) (estimatedTime / 60);
        int mins = (int) Math.round(estimatedTime % 60);
        return hrs > 0 ? String.format("%dh %02dmin", hrs, mins) : String.format("%dmin", mins);
    }

    // (Opzionale) Metodo per formattare anche il prezzo
    public String getEstimatedPriceFormatted() {
        return String.format("€%.2f", estimatedPrice);
    }
}


