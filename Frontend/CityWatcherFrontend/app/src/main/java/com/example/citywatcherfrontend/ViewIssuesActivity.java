package com.example.citywatcherfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.citywatcherfrontend.databinding.ActivityViewIssuesBinding;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ViewIssuesActivity extends CityWatcherActivity {

    private String URL;

    private ActivityViewIssuesBinding binding;
    private IssueListAdapter issueListAdapter;
    public JSONArray requestResponse;
    private ArrayList<IssueData> issueArrayList = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();

    IssueData issue;
    CommentData comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        URL = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userId + "/issues";

        binding = ActivityViewIssuesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeGetIssuesReq();
    }

    private void makeGetIssuesReq() {
        JsonArrayRequest jsonStringReq = new JsonArrayRequest(
                URL + "/search",
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response){
                        Log.d("Volley Response", "Issues retrieved");

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                String jsonString = response.get(i).toString();
                                issue = mapper.readValue(jsonString, IssueData.class);
                                issueArrayList.add(issue);
                            } catch (JSONException | JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        issueListAdapter = new IssueListAdapter(ViewIssuesActivity.this, issueArrayList);
                        binding.listissues.setAdapter(issueListAdapter);
                        binding.listissues.setClickable(true);
                        binding.listissues.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Intent intent = new Intent(ViewIssuesActivity.this, IssueDetailsActivity.class);
                                intent.putExtra("id", issueArrayList.get(i).getId());
                                intent.putExtra("title", issueArrayList.get(i).getTitle());
                                intent.putExtra("category", issueArrayList.get(i).getCategory());
                                intent.putExtra("latitude", issueArrayList.get(i).getLatitude());
                                intent.putExtra("longitude", issueArrayList.get(i).getLongitude());
                                intent.putExtra("status", issueArrayList.get(i).getStatus());
                                intent.putExtra("description", issueArrayList.get(i).getDescription());

                                startActivity(intent);
                            }
                        });
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
}