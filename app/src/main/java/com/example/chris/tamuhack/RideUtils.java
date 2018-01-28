package com.example.chris.tamuhack;

import android.os.StrictMode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Tyler on 1/27/18.
 */

public class RideUtils {

    private static final String UBER_SERVER_TOKEN = "gGXk0zoi6TLBKZKXMnhC9Igm2CjBVVbkHjgY1j12";
    private static final String LYFT_CLIENT_ID = "f9_flU3DCaYx";
    private static final String LYFT_CLIENT_SECRET = "HVNQyl47Vr0l40XC1JLw9WOAo9zJlvAK";

    public static Uber getShortestUber(List<Uber> availableUbers) {
        Uber shortestUber = new Uber();
        boolean firstUber = true;
        for (Uber currentUber : availableUbers) {
            if (firstUber) {
                shortestUber = currentUber;
                firstUber = false;
            } else {
                if (currentUber.getTimeEstimate() < shortestUber.getTimeEstimate()) {
                    shortestUber = currentUber;
                }
            }
        }
        return shortestUber;
    }

    public static Uber getCheapestUber(List<Uber> availableUbers) {
        Uber cheapestUber = new Uber();
        boolean firstUber = true;
        for (Uber currentUber : availableUbers) {
            if (firstUber) {
                cheapestUber = currentUber;
                firstUber = false;
            } else {
                if (currentUber.getMaxPrice() < cheapestUber.getMaxPrice()) {
                    cheapestUber = currentUber;
                }
            }
        }
        return cheapestUber;
    }

    public static Lyft getShortestLyft(List<Lyft> availableLyfts) {
        Lyft shortestLyft = new Lyft();
        boolean firstLyft = true;
        for (Lyft currentLyft : availableLyfts) {
            if (firstLyft) {
                shortestLyft = currentLyft;
                firstLyft = false;
            } else {
                if (currentLyft.getTimeEstimate() < shortestLyft.getTimeEstimate()) {
                    shortestLyft = currentLyft;
                }
            }
        }
        return shortestLyft;
    }

    public static Lyft getCheapestLyft(List<Lyft> availableLyfts) {
        Lyft cheapestLyft = new Lyft();
        boolean firstLyft = true;
        for (Lyft currentLyft : availableLyfts) {
            if (firstLyft) {
                cheapestLyft = currentLyft;
                firstLyft = false;
            } else {
                if (currentLyft.getMaxPrice() < cheapestLyft.getMaxPrice()) {
                    cheapestLyft = currentLyft;
                }
            }
        }
        return cheapestLyft;
    }

    public static List<String> getAvailableUbers(String queryTime, String queryPrice) {
        List<String> responses = new ArrayList<>();
        HttpsURLConnection con = null;
        boolean timeSuccess = false;
        boolean priceSuccess = false;
        StringBuilder response = new StringBuilder();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {

            // Set HttpsRequest Properties for Time
            URL url = new URL(queryTime);
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Token " + UBER_SERVER_TOKEN);

            // Connect and check for successful response
            con.connect();
            int responseCode = con.getResponseCode();
            InputStream inputStream;

            if (200 <= responseCode && responseCode <= 299) {
                inputStream = con.getInputStream();
                timeSuccess = true;
            } else {
                inputStream = con.getErrorStream();
            }

            // Collect response as String
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                response.append(currentLine);
            }

            in.close();

            System.out.println("Uber Time Response: " + response.toString());
            responses.add(response.toString());


            // Set HttpsRequest Properties for Price
            url = new URL(queryPrice);
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Token " + UBER_SERVER_TOKEN);

            // Connect and check for successful response
            con.connect();
            responseCode = con.getResponseCode();
            response = new StringBuilder();

            if (200 <= responseCode && responseCode <= 299) {
                inputStream = con.getInputStream();
                priceSuccess = true;
            } else {
                inputStream = con.getErrorStream();
            }

            // Collect response as String
            in = new BufferedReader(new InputStreamReader(inputStream));

            currentLine = "";
            while ((currentLine = in.readLine()) != null) {
                response.append(currentLine);
            }

            in.close();

            System.out.println("Uber Price Response: " + response.toString());
            responses.add(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }

        if(timeSuccess && priceSuccess) {
            return responses;
        } else {
            return null;
        }
    }

    private static String getLyftBearer() {
        HttpsURLConnection con = null;
        boolean success = false;
        StringBuilder response = new StringBuilder();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String bearerToken = "";

        try {

            URL url = new URL("https://api.lyft.com/oauth/token");
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            String encodedProp = android.util.Base64.encodeToString((LYFT_CLIENT_ID + ":" + LYFT_CLIENT_SECRET).getBytes(), android.util.Base64.DEFAULT).trim();
            con.setRequestProperty("Authorization", "Basic " + encodedProp);
            con.setDoOutput(true);

            String data = URLEncoder.encode("grant_type", "UTF-8")
                    + "=" + URLEncoder.encode("client_credentials", "UTF-8");

            data += "&" + URLEncoder.encode("scope", "UTF-8") + "="
                    + URLEncoder.encode("public", "UTF-8");

            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write( data );
            wr.flush();

            // Connect and check for successful response
            con.connect();
            int responseCode = con.getResponseCode();
            InputStream inputStream;

            if (200 <= responseCode && responseCode <= 299) {
                inputStream = con.getInputStream();
                success = true;
            } else {
                inputStream = con.getErrorStream();
            }

            // Collect response as String
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                response.append(currentLine);
            }

            in.close();

            JSONObject responseObject = new JSONObject(response.toString());
            bearerToken = responseObject.getString("access_token").trim();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (success) {
            return bearerToken;
        } else {
            return null;
        }
    }


    public static List<String> getAvailableLyfts(String queryTime, String queryPrice) {
        List<String> responses = new ArrayList<>();
        HttpsURLConnection con = null;
        boolean timeSuccess = false;
        boolean priceSuccess = false;
        StringBuilder response = new StringBuilder();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String bearerToken = RideUtils.getLyftBearer();
        if (bearerToken != null) {
            try {
                // Set HttpsRequest Properties for Time
                URL url = new URL(queryTime);
                con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Authorization", "Bearer " + bearerToken);

                // Connect and check for successful response
                con.connect();
                int responseCode = con.getResponseCode();
                InputStream inputStream;

                if (200 <= responseCode && responseCode <= 299) {
                    inputStream = con.getInputStream();
                    timeSuccess = true;
                } else {
                    inputStream = con.getErrorStream();
                }

                // Collect response as String
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

                String currentLine;
                while ((currentLine = in.readLine()) != null) {
                    response.append(currentLine);
                }

                in.close();

                System.out.println("Lyft Time Response: " + response.toString());
                responses.add(response.toString());


                // Set HttpsRequest Properties for Price
                url = new URL(queryPrice);
                con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Authorization", "Bearer " + bearerToken);

                // Connect and check for successful response
                con.connect();
                responseCode = con.getResponseCode();
                response = new StringBuilder();

                if (200 <= responseCode && responseCode <= 299) {
                    inputStream = con.getInputStream();
                    priceSuccess = true;
                } else {
                    inputStream = con.getErrorStream();
                }

                // Collect response as String
                in = new BufferedReader(new InputStreamReader(inputStream));

                currentLine = "";
                while ((currentLine = in.readLine()) != null) {
                    response.append(currentLine);
                }

                in.close();

                System.out.println("Lyft Price Response: " + response.toString());
                responses.add(response.toString());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                con.disconnect();
            }

            if(timeSuccess && priceSuccess) {
                return responses;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
