package com.example.citywatcherfrontend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.citywatcherfrontend.databinding.ActivityIssueDetailsBinding;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IssueDetailsActivity extends CityWatcherActivity {

    private int issueId;
    private int reporterId;
    private int commentId;
    private String URL;

    private ActivityIssueDetailsBinding binding;
    private CommentListAdapter commentListAdapter;
    private ArrayList<CommentData> commentArrayList = new ArrayList<CommentData>();
    private ObjectMapper mapper = new ObjectMapper();
    private JSONObject requestParams = new JSONObject();

    private CommentData comment;
    private Button buttonEditComment;

    // Initialize activity variables
    private ImageView issueDetailsImage;
    private TextView issueDetailsTitle;
    private TextView issueDetailsCategory;
    private TextView issueDetailsReporter;
    private TextView issueDetailsLocation;
    private TextView issueDetailsStatus;
    private TextView issueDetailsDescription;
    private TextView issueDetailsComment;
    private Button buttonEditIssue;
    private Button buttonDeleteIssue;
    private EditText addComment;
    private Button buttonAddComment;
    private ListView listComments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        binding = ActivityIssueDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        URL = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userId + "/issues";
        issueId = bundle.getInt("id");
        reporterId = bundle.getInt("reporterId");

        issueDetailsImage = findViewById(R.id.issueDetailsImage);
        issueDetailsCategory = findViewById(R.id.issueDetailsCategory);
        issueDetailsTitle = findViewById(R.id.issueDetailsTitle);
        issueDetailsReporter = findViewById(R.id.issueDetailsReporter);
        issueDetailsLocation = findViewById(R.id.issueDetailsLocation);
        issueDetailsStatus = findViewById(R.id.issueDetailsStatus);
        issueDetailsDescription = findViewById(R.id.issueDetailsDescription);
        buttonEditIssue = findViewById(R.id.buttonEditIssue);
        buttonDeleteIssue = findViewById(R.id.buttonDeleteIssue);
        addComment = findViewById(R.id.editAddComment);
        buttonAddComment = findViewById(R.id.buttonAddComment);
        listComments = findViewById(R.id.listComments);

        makeGetCommentsReq();

        // TODO Set image
        issueDetailsTitle.setText(bundle.getString("title"));
        issueDetailsCategory.setText(bundle.getString("category"));
        // TODO Set reporter
        issueDetailsReporter.setText(bundle.getString("reporter"));
        // TODO Set location
        issueDetailsLocation.setText(bundle.getString("address"));


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
        issueDetailsLocation.setText(bundle.getString("address"));

        if (loggedIn) {
            if (CityWatcherController.getInstance().getUserId() != reporterId) {
                buttonEditIssue.setVisibility(View.GONE);
                buttonDeleteIssue.setVisibility(View.GONE);
            }
        } else {
            buttonEditIssue.setVisibility(View.GONE);
            buttonDeleteIssue.setVisibility(View.GONE);
            addComment.setVisibility(View.GONE);
            buttonAddComment.setVisibility(View.GONE);
        }

        // TODO Set buttons for admin view

        buttonEditIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IssueDetailsActivity.this, UpdateIssueActivity.class);
                intent.putExtra("id", issueId);
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

        buttonAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!addComment.getText().toString().isEmpty()) {
                    try {
                        requestParams.put("content", addComment.getText().toString());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    makeNewCommentReq();
                }
            }
        });
    }



    private void makeGetCommentsReq() {
        JsonArrayRequest jsonStringReq = new JsonArrayRequest(
                URL + "/" + issueId + "/comments",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response){
                        Log.d("Volley Response", "Comments retrieved");

                        for (int i = response.length() - 1; i >= 0; i--) {
                            try {
                                String jsonString = response.get(i).toString();
                                comment = mapper.readValue(jsonString, CommentData.class);
                                commentArrayList.add(comment);
                            } catch (JSONException | JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        commentListAdapter = new CommentListAdapter(IssueDetailsActivity.this, commentArrayList);
                        binding.listComments.setAdapter(commentListAdapter);
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
                headers.put("Content-Type", "application/json");
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonStringReq);
    }

    private void makeNewCommentReq() {
    JsonObjectRequest jsonObjReq = new JsonObjectRequest(
            Request.Method.POST,
            URL + "/" + issueId + "/comments",
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
        Intent intent = new Intent(IssueDetailsActivity.this, ViewIssuesActivity.class);
        startActivity(intent);
    };

    public void editComment(int i){
            Intent intent = new Intent(IssueDetailsActivity.this, EditCommentActivity.class);
            Bundle bundle = new Bundle();
            CommentData comment = commentArrayList.get(i);
            bundle.putInt("userID", comment.getUser().getId());
            bundle.putInt("issueID", comment.getIssueId());
            bundle.putInt("commentID", comment.getId());
            intent.putExtras(bundle);
            startActivity(intent);
    };

    public void makeDeleteCommentReq(int i) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.DELETE,
                URL + "/" + issueId + "/comments/" + commentArrayList.get(i).getId(),
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

    public void reportComment(int i) {
        Intent intent = new Intent(IssueDetailsActivity.this, ReportCommentActivity.class);
        Bundle bundle = new Bundle();
        CommentData comment = commentArrayList.get(i);
        bundle.putInt("userID", comment.getUser().getId());
        bundle.putInt("issueID", issueId);
        bundle.putInt("commentID", comment.getId());
        intent.putExtras(bundle);
        startActivity(intent);
    }

}