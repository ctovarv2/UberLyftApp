package com.example.chris.tamuhack;

import android.os.StrictMode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Tyler on 1/27/18.
 */

public class UberUtils {

    private static final String SERVER_TOKEN = "gGXk0zoi6TLBKZKXMnhC9Igm2CjBVVbkHjgY1j12";
    private static final String CLIENT_ID = "SHPQJcLpxERG1Iac_6jyx9Sa9-l6SrT2";

    public static List<String> makeRequests(String queryTime, String queryPrice) {
        List<String> responses = new ArrayList<>();
        HttpsURLConnection con = null;
        boolean success = false;
        StringBuilder response = new StringBuilder();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {

            // Set HttpsRequest Properties for Time
            URL url = new URL(queryTime);
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Token " + SERVER_TOKEN);

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

            System.out.println("Time Response: " + response.toString());
            responses.add(response.toString());


            // Set HttpsRequest Properties for Price
            url = new URL(queryPrice);
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Token " + SERVER_TOKEN);

            // Connect and check for successful response
            con.connect();
            responseCode = con.getResponseCode();
            response = new StringBuilder();

            if (200 <= responseCode && responseCode <= 299) {
                inputStream = con.getInputStream();
                success = true;
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

            System.out.println("Price Response: " + response.toString());
            responses.add(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }

        if(success) {
            return responses;
        } else {
            return null;
        }
    }

}
