package com.example.citywatcherfrontend;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateIssueActivity extends CityWatcherActivity {

    private final String MAPS_KEY = "AIzaSyBWM0V9EhmOR0jCz7NYo9xDcmOW6Ni-trc";

    private String URL;

    // Initialize activity variables
    // TODO Allow options for users to upload an image and select a location
    private EditText editIssueName;
    private EditText editIssueType;
    private EditText editIssueLocation;
    private EditText editIssueDescription;
    private Button buttonSubmitIssue;
    private JSONObject requestParams = new JSONObject();

    private LatLng latlng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        URL = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userId + "/issues";

        setContentView(R.layout.activity_create_issue);

        editIssueName = findViewById(R.id.editIssueName);
        editIssueType = findViewById(R.id.editIssueType);
        editIssueLocation = findViewById(R.id.editIssueLocation);
        editIssueDescription = findViewById(R.id.editIssueDescription);
        buttonSubmitIssue = findViewById(R.id.buttonSubmitIssue);

        // Submit listener
        buttonSubmitIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editIssueName.getText().toString().isEmpty() && !editIssueType.getText().toString().isEmpty() && !editIssueLocation.getText().toString().isEmpty() && !editIssueDescription.getText().toString().isEmpty()) {
                    Executor getLatLngFromAddress = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());

                    getLatLngFromAddress.execute(new Runnable() {
                        String response;

                        @Override
                        public void run() {
                            try {
                                String address = editIssueLocation.getText().toString().replace(" ", "+");
                                String stringUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + MAPS_KEY;
                                HTTPRequestHandler httpHandler = new HTTPRequestHandler();
                                response = httpHandler.getDataFromURL(stringUrl);

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        JSONObject jsonObject = null;
                                        try {
                                            if (!response.isEmpty()) {
                                                jsonObject = new JSONObject(response);
                                                String lat = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString();
                                                String lng = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString();
                                                latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                                                putIssueParams();
                                                makeCreateIssueReq();
                                            } else {
                                                Toast.makeText(CreateIssueActivity.this, "Invalid address", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                            } catch (JSONException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }
            }
        });
    }

    // POST Request to create an issue
    private void makeCreateIssueReq() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                requestParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                // headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("param1", "value1");
                return params;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
        Intent intent = new Intent(CreateIssueActivity.this, ViewIssuesActivity.class);
        startActivity(intent);
    }

    private void putIssueParams() {
        try {
            requestParams.put("title", editIssueName.getText().toString());
            requestParams.put("description", editIssueDescription.getText().toString());
            requestParams.put("latitude", latlng.latitude);
            requestParams.put("longitude", latlng.longitude);
            requestParams.put("category", editIssueType.getText().toString());
            requestParams.put("status", "REPORTED");
            requestParams.put("imagePath", "");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}