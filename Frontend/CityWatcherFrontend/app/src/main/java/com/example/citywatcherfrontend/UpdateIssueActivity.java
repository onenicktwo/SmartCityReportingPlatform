package com.example.citywatcherfrontend;

import android.content.Intent;
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

public class UpdateIssueActivity extends CityWatcherActivity {


    private int issueId;
    private String URL;

    // Initialize activity variables
    // TODO Allow options for users to upload an image and select a location
    private EditText updateIssueName;
    private EditText updateIssueType;
    private EditText updateIssueStatus;
    private EditText updateIssueDescription;
    private Button buttonSubmitIssue;
    private JSONObject requestParams = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = new Bundle(getIntent().getExtras());

        setContentView(R.layout.activity_update_issue);
        issueId = bundle.getInt("id");
        URL = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userId + "/issues/" + issueId;

        updateIssueName = findViewById(R.id.updateIssueName);
        updateIssueType = findViewById(R.id.updateIssueType);
        updateIssueStatus = findViewById(R.id.updateIssueStatus);
        updateIssueDescription = findViewById(R.id.updateIssueDescription);
        buttonSubmitIssue = findViewById(R.id.buttonSubmitIssue);

        // Submit listener
        buttonSubmitIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    try {
                        if (!updateIssueName.getText().toString().isEmpty())  {
                            requestParams.put("title", updateIssueName.getText().toString());
                        }
                        if (!updateIssueType.getText().toString().isEmpty()) {
                            requestParams.put("category", updateIssueType.getText().toString());
                        }
                        if (!updateIssueDescription.getText().toString().isEmpty()) {
                            requestParams.put("description", updateIssueDescription.getText().toString());
                        }
                        if (!updateIssueStatus.getText().toString().isEmpty()) {
                            requestParams.put("status", updateIssueStatus.getText().toString());
                        }
                    } catch (NullPointerException | JSONException e) {
                        throw new RuntimeException(e);
                    }
                    makeUpdateIssueReq();
                }
            }
        );
    }

    // POST Request to Update an issue
    private void makeUpdateIssueReq() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.PUT,
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
        Intent intent = new Intent(UpdateIssueActivity.this, ViewIssuesActivity.class);
        startActivity(intent);
    }
}