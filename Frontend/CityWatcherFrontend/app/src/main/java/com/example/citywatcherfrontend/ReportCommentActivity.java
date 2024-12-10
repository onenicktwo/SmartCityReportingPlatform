package com.example.citywatcherfrontend;

import android.content.Intent;
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

public class ReportCommentActivity extends CityWatcherActivity{
    int issueId;
    int commentId;
    String URL;
    JSONObject requestParams = new JSONObject();

    EditText reportReason;
    Button buttonReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_comment);
        Bundle bundle = getIntent().getExtras();

        issueId = bundle.getInt("issueID");
        commentId = bundle.getInt("commentID");

        URL = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userId + "/issues/" + issueId + "/comments/" + commentId + "/report";

        reportReason = findViewById(R.id.editReportReason);
        buttonReport = findViewById(R.id.buttonReportComment);

        buttonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!reportReason.getText().toString().isEmpty()) {
                    try {
                        requestParams.put("reason", reportReason.getText().toString());
                        makeNewReportCommentReq();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

    }

    private void makeNewReportCommentReq() {
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
        finish();

    }
}
