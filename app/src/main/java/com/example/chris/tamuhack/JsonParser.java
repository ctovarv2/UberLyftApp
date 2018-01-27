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

        System.out.println("Inputed time response: " + timeResponse);
        System.out.println("Inputed price response: " + priceResponse);

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
                    currentUber.setTimeEstimate(timeEstimate.doubleValue()/60);
                } else {
                    currentUber.setTimeEstimate(-1);
                }
                ubers.add(currentUber);
            }

            // loop through priceArray
            for (int i = 0; i < priceArray.length(); ++i) {
                JSONObject currentObject = priceArray.getJSONObject(i);
                String currentDisplayName = currentObject.getString("localized_display_name");
                for (Uber uber: ubers) {
                    if (uber.getVehicleType().equals(currentDisplayName)) {
                        String priceEstimate = currentObject.getString("estimate");
                        if (priceEstimate != null || priceEstimate != "null") {
                            uber.setPriceEstimate(priceEstimate);
                        } else {
                            uber.setPriceEstimate(null);
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ubers;
    }

}
