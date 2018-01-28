package com.example.chris.tamuhack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 1/27/18.
 */

public class JsonParser {

    public static List<Uber> getAvailableUbers(String timeResponse, String priceResponse) {
        List<Uber> ubers = new ArrayList<>();

        try {
            // get Json Arrays
            JSONObject timeObject = new JSONObject(timeResponse);
            JSONObject priceObject = new JSONObject(priceResponse);
            JSONArray timeArray = timeObject.getJSONArray("times");
            JSONArray priceArray = priceObject.getJSONArray("prices");

            // loop through time Array
            for (int i = 0; i < timeArray.length(); ++i) {
                JSONObject currentObject = timeArray.getJSONObject(i);
                Uber currentUber = new Uber();
                currentUber.setVehicleType(currentObject.getString("localized_display_name"));
                Double timeEstimate = currentObject.getDouble("estimate");
                if (timeEstimate != null) {
                    currentUber.setTimeEstimate((int)timeEstimate.doubleValue()/60);
                } else {
                    currentUber.setTimeEstimate(-1);
                }
                ubers.add(currentUber);
            }

            // loop through priceArray
            for (int i = 0; i < priceArray.length(); ++i) {
                JSONObject currentObject = priceArray.getJSONObject(i);
                String currentDisplayName = currentObject.getString("localized_display_name");
                boolean foundMatch = false;
                for (Uber uber: ubers) {
                    if (uber.getVehicleType().equals(currentDisplayName)) {
                        foundMatch = true;
                        String priceEstimate = currentObject.getString("estimate");
                        Double priceMax = currentObject.getDouble("high_estimate");
                        if (priceEstimate != null && priceEstimate != "null" && priceMax != null) {
                            uber.setPriceEstimate(priceEstimate);
                            uber.setMaxPrice(priceMax.doubleValue());
                        } else {
                            uber.setPriceEstimate(null);
                            uber.setMaxPrice(-1);
                        }
                        break;
                    }
                }
                if (!foundMatch) {
                    Uber newUber = new Uber();
                    newUber.setVehicleType(currentDisplayName);
                    String priceEstimate = currentObject.getString("estimate");
                    Double priceMax = currentObject.getDouble("high_estimate");
                    if (priceEstimate != null && priceEstimate != "null" && priceMax != null) {
                        newUber.setPriceEstimate(priceEstimate);
                        newUber.setMaxPrice(priceMax.doubleValue());
                    } else {
                        newUber.setPriceEstimate(null);
                        newUber.setMaxPrice(-1);
                    }
                    newUber.setTimeEstimate(-1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ubers;
    }


    public static List<Lyft> getAvailableLyfts(String timeResponse, String priceResponse) {
        List<Lyft> lyfts = new ArrayList<>();

        try {
            // get Json Arrays
            JSONObject timeObject = new JSONObject(timeResponse);
            JSONObject priceObject = new JSONObject(priceResponse);
            JSONArray timeArray = timeObject.getJSONArray("eta_estimates");
            JSONArray priceArray = priceObject.getJSONArray("cost_estimates");

            // loop through time Array
            for (int i = 0; i < timeArray.length(); ++i) {
                JSONObject currentObject = timeArray.getJSONObject(i);
                Lyft currentLyft = new Lyft();
                currentLyft.setVehicleType(currentObject.getString("display_name"));
                Double timeEstimate = currentObject.getDouble("eta_seconds");
                if (timeEstimate != null) {
                    currentLyft.setTimeEstimate((int)timeEstimate.doubleValue()/60);
                } else {
                    currentLyft.setTimeEstimate(-1);
                }
                lyfts.add(currentLyft);
            }

            // loop through priceArray
            for (int i = 0; i < priceArray.length(); ++i) {
                JSONObject currentObject = priceArray.getJSONObject(i);
                boolean foundMatch = false;
                String currentDisplayName = currentObject.getString("display_name");
                for (Lyft lyft: lyfts) {
                    if (lyft.getVehicleType().equals(currentDisplayName)) {
                        foundMatch = true;
                        Double priceEstimateMin = currentObject.getDouble("estimated_cost_cents_min");
                        Double priceEstimateMax = currentObject.getDouble("estimated_cost_cents_max");
                        if (priceEstimateMin != null && priceEstimateMax != null) {
                            lyft.setPriceEstimate("$" + priceEstimateMin/100 + "-" + priceEstimateMax/100);
                            lyft.setMaxPrice(priceEstimateMax.doubleValue());
                        } else {
                            lyft.setPriceEstimate(null);
                            lyft.setMaxPrice(-1);
                        }
                        break;
                    }
                }
                if (!foundMatch) {
                    Lyft newLyft = new Lyft();
                    newLyft.setVehicleType(currentDisplayName);
                    Double priceEstimateMin = currentObject.getDouble("estimated_cost_cents_min");
                    Double priceEstimateMax = currentObject.getDouble("estimated_cost_cents_max");
                    if (priceEstimateMin != null && priceEstimateMax != null) {
                        newLyft.setPriceEstimate("$" + priceEstimateMin/100 + "-" + priceEstimateMax/100);
                        newLyft.setMaxPrice(priceEstimateMax.doubleValue());
                    } else {
                        newLyft.setPriceEstimate(null);
                        newLyft.setMaxPrice(-1);
                    }
                    newLyft.setTimeEstimate(-1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lyfts;
    }

}
