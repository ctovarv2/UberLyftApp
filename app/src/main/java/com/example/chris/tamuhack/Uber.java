package com.example.chris.tamuhack;

/**
 * Created by Tyler on 1/27/18.
 */

public class Uber {

    private String vehicleType;

    private double timeEstimate;

    private String priceEstimate;

    public Uber() {
        vehicleType = "";
        timeEstimate = 0;
        priceEstimate = "";
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getTimeEstimate() {
        return timeEstimate;
    }

    public void setTimeEstimate(double timeEstimate) {
        this.timeEstimate = timeEstimate;
    }

    public String getPriceEstimate() {
        return priceEstimate;
    }

    public void setPriceEstimate(String priceEstimate) {
        this.priceEstimate = priceEstimate;
    }
}
