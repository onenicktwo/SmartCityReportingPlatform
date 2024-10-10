package com.example.citywatcherfrontend;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateIssueActivity extends CityWatcherActivity {


    private String URL;

    // Initialize activity variables
    // TODO Allow options for users to upload an image and select a location
    private EditText editIssueName;
    private EditText editIssueType;
    private EditText editIssueDescription;
    private Button buttonSubmitIssue;
    private JSONObject requestParams = new JSONObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        URL = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userId + "/issues";

        setContentView(R.layout.activity_create_issue);
        toolbar.setSubtitle("Create Issue");

        editIssueName = findViewById(R.id.editIssueName);
        editIssueType = findViewById(R.id.editIssueType);
        editIssueDescription = findViewById(R.id.editIssueDescription);
        buttonSubmitIssue = findViewById(R.id.buttonSubmitIssue);

        // Submit listener
        buttonSubmitIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editIssueName.getText().toString().isEmpty() && !editIssueType.getText().toString().isEmpty() && !editIssueDescription.getText().toString().isEmpty()) {
                    try {
                        requestParams.put("title", editIssueName.getText().toString());
                        requestParams.put("description", editIssueDescription.getText().toString());
                        requestParams.put("category", editIssueType.getText().toString());
                        requestParams.put("status", "REPORTED");
                        requestParams.put("latitude", 0);
                        requestParams.put("longitude", 0);
                        requestParams.put("imagePath", "");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    makeCreateIssueReq();
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
    }
}