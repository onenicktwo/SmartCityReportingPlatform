package com.example.citywatcherfrontend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class IssueDetailsActivity extends CityWatcherActivity {

    private int issueId;
    private String URL;

    // Initialize activity variables
    private ImageView issueDetailsImage;
    private TextView issueDetailsTitle;
    private TextView issueDetailsCategory;
    private TextView issueDetailsLocation;
    private TextView issueDetailsStatus;
    private TextView issueDetailsDescription;
    private TextView issueDetailsComment;
    private Button buttonEditIssue;
    private Button buttonDeleteIssue;
    private Button buttonEditComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.activity_issue_details);

        URL = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userId + "/issues";
        issueId = bundle.getInt("id");

        issueDetailsImage = findViewById(R.id.issueDetailsImage);
        issueDetailsCategory = findViewById(R.id.issueDetailsCategory);
        issueDetailsTitle = findViewById(R.id.issueDetailsTitle);
        issueDetailsLocation = findViewById(R.id.issueDetailsLocation);
        issueDetailsStatus = findViewById(R.id.issueDetailsStatus);
        issueDetailsDescription = findViewById(R.id.issueDetailsDescription);
        issueDetailsComment = findViewById(R.id.issueDetailsComment);
        buttonEditIssue = findViewById(R.id.buttonEditIssue);
        buttonDeleteIssue = findViewById(R.id.buttonDeleteIssue);
        buttonEditComment = findViewById(R.id.buttonEditComment);

        // TODO Set image
        issueDetailsTitle.setText(bundle.getString("title"));
        issueDetailsCategory.setText(bundle.getString("category"));
        // TODO Set reporter
        issueDetailsCategory.setText("Category");
        // TODO Set location
        issueDetailsLocation.setText("Location");


        String status = bundle.getString("status");
        if (status.equals("REPORTED")) {
            issueDetailsStatus.setText("Reported");
            issueDetailsStatus.setTextColor(Color.RED);
        } else if (status.equals("UNDER_REVIEW")) {
            issueDetailsStatus.setText("Under Review");
            issueDetailsStatus.setTextColor(Color.YELLOW);
        } else {
            issueDetailsStatus.setText("Completed");
            issueDetailsStatus.setTextColor(Color.GREEN);
        }

        issueDetailsDescription.setText(bundle.getString("description"));

        fetchDetailIssue(issueId);

        // TODO Set buttons for admin view
        // TODO Comments

        buttonEditIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IssueDetailsActivity.this, UpdateIssueActivity.class);
                intent.putExtra("id", issueId);
                startActivity(intent);
            }
        });

        buttonEditComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IssueDetailsActivity.this, EditCommentActivity.class);
                intent.putExtra("userID", userId);
                intent.putExtra("issueID", issueId);
                startActivity(intent);
            }
        });

        buttonDeleteIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                        Request.Method.DELETE,
                        URL + "/" + issueId,
                        null,
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
                Intent intent = new Intent(IssueDetailsActivity.this, ViewIssuesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchDetailIssue(int issueId) {
        String requestUrl = URL + "/" + issueId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray commentsArray = response.getJSONArray("comments");
                            if (commentsArray.length() > 0) {
                                JSONObject comment = commentsArray.getJSONObject(0);
                                String commentContent = comment.getString("content");
                                issueDetailsComment.setText(commentContent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("IssueDetails", "Error parsing JSON response", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("IssueDetails", "Error fetching issue details", error);
                    }
                }
        );

        // Add request to Volley queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

}