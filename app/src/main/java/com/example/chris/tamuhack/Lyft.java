package com.example.chris.tamuhack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Tyler on 1/27/18.
 */

public class Lyft {

    private String vehicleType;

    private int timeEstimate;

    private String priceEstimate;

    private double maxPrice;

    public Lyft() {
        vehicleType = "";
        timeEstimate = 0;
        priceEstimate = "";
        maxPrice = 0;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getTimeEstimate() {
        return timeEstimate;
    }

    public void setTimeEstimate(int timeEstimate) {
        this.timeEstimate = timeEstimate;
    }

    public String getPriceEstimate() {
        return priceEstimate;
    }

    public void setPriceEstimate(String priceEstimate) {
        this.priceEstimate = priceEstimate;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    @Override
    public String toString() {
        String asString = "Vechicle Type: " + vehicleType +
                "\nTime Estimate: " + timeEstimate +
                "\nPrice Estimate: " + priceEstimate;
        return asString;
    }

}
