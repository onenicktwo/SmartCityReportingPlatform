package com.example.citywatcherfrontend;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPRequestHandler {

    public HTTPRequestHandler() {

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
}
