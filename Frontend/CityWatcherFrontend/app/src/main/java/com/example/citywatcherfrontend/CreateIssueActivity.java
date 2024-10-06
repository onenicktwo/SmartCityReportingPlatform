package com.example.citywatcherfrontend;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateIssueActivity extends NavbarActivity {

    private static final String URL = "https://9e46c104-36a4-402d-8e2a-545063f0e49c.mock.pstmn.io/issue/create";

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
        setContentView(R.layout.activity_create_issue);
        toolbar.setSubtitle("Create Issue");

        editIssueName = findViewById(R.id.editIssueName);
        editIssueType = findViewById(R.id.editIssueType);
        editIssueDescription = findViewById(R.id.editIssueDescription);
        buttonSubmitIssue = findViewById(R.id.buttonSubmitIssue);

        buttonSubmitIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editIssueName.getText().toString().isEmpty() && !editIssueType.getText().toString().isEmpty() && !editIssueDescription.getText().toString().isEmpty()) {
                    try {
                        // requestParams.put("reporter", null);
                        requestParams.put("title", editIssueName.getText().toString());
                        requestParams.put("category", editIssueType.getText().toString());
                        requestParams.put("status", 0);
                        requestParams.put("imagePath", null);
                        requestParams.put("description", editIssueDescription.getText().toString());
                        requestParams.put("latitude", null);
                        requestParams.put("longitude", null);
                        requestParams.put("assignedOfficial", null);
                        makeCreateIssueReq();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

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
//                headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
//                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("param1", "value1");
//                params.put("param2", "value2");
                return params;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }
}