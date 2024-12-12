package com.example.citywatcherfrontend;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * An activity class for creating an issue; The user provides the name, category, location, and description for an issue.
 */
public class CreateIssueActivity extends CityWatcherActivity {
    private String URL;

    // Initialize activity variables
    // TODO Allow options for users to upload an image and select a location
    private EditText editIssueName;
    private EditText editIssueType;
    private EditText editIssueLocation;
    private EditText editIssueDescription;
    private Button buttonSubmitIssue;
    private JSONObject requestParams = new JSONObject();

    private String formattedAddress;
    private LatLng latlng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        URL = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userId + "/issues";

        setContentView(R.layout.activity_create_issue);

        editIssueName = findViewById(R.id.editIssueTitle);
        editIssueType = findViewById(R.id.editIssueCategory);
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
                            final String[] address = {editIssueLocation.getText().toString().replace(" ", "+")};
                            String stringUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address[0] + "&key=" + MAPS_KEY;
                            try {
                                response = CityWatcherController.getInstance().getDataFromURL(stringUrl);
                            } catch (IOException | JSONException e) {
                                throw new RuntimeException(e);
                            }

                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    JSONObject jsonObject = null;
                                    try {
                                        if (!response.isEmpty()) {
                                            Pair<String, LatLng> locationData = CityWatcherController.getInstance().convertGeocodingDataToLocation(response);
                                            formattedAddress = locationData.first;
                                            latlng = locationData.second;

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
                        }
                    });
                }
            }
        });
    }

    /**
     * Sends a POST Volley request to the IssueController in the backend to create an issue.
     */
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
                headers.put("Content-Type", "application/json; charset=utf-8");
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
        finish();
    }

    /**
     * Sets the request parameters that will be used for the POST request to create an issue.
     */
    private void putIssueParams() {
        try {
            requestParams.put("title", editIssueName.getText().toString());
            requestParams.put("description", editIssueDescription.getText().toString());
            requestParams.put("latitude", latlng.latitude);
            requestParams.put("longitude", latlng.longitude);
            requestParams.put("address", formattedAddress);
            requestParams.put("category", editIssueType.getText().toString());
            requestParams.put("status", "REPORTED");
            requestParams.put("imagePath", "");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}