package com.example.citywatcherfrontend;

import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CityWatcherController {
    private static CityWatcherController instance;

    private int userId;
    private String username;
    private boolean loggedIn;
    private boolean connected;


    public static CityWatcherController getInstance() {
        if (instance == null) {
            instance = new CityWatcherController();
        }
        return instance;
    }

    private CityWatcherController(){}

    public int getUserId() {
        return userId;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getDataFromURL(String stringUrl) throws IOException, JSONException {
        URL url = new URL(stringUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setReadTimeout(15000);
        conn.setConnectTimeout(15000);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String requestResponse = "";
            String curLine = "";
            BufferedReader bReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while (curLine != null) {
                curLine = bReader.readLine();
                requestResponse += curLine;
            }
            conn.disconnect();

            return requestResponse;
        }
        return null;
    }

    public Pair<String, LatLng> convertDataToLocation (String data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data);
        String lat = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString();
        String lng = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString();
        String formattedAddress = jsonObject.getJSONArray("results").getJSONObject(0).get("formatted_address").toString();
        LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

        return new Pair<String, LatLng>(formattedAddress, latlng);
    }


}
