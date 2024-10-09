package com.example.citywatcherfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.citywatcherfrontend.databinding.ActivityViewIssuesBinding;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ViewIssuesActivity extends NavbarActivity {

    User user = new User();
    private String URL;


    ActivityViewIssuesBinding binding;
    IssueListAdapter issueListAdapter;
    JSONArray requestResponse;
    ArrayList<Issue> issueArrayList = new ArrayList<>();
    ObjectMapper mapper = new ObjectMapper();

    Issue issue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        URL = "https://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + user.getId() + "/issues";

        binding = ActivityViewIssuesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeGetIssuesReq();

        for (int i = 0; i < requestResponse.length(); i++) {
            try {
                String jsonString = requestResponse.get(i).toString();
                issueArrayList.add(mapper.readValue(jsonString, Issue.class));
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
                startActivity(intent);
            }
        });
    }

    private void makeGetIssuesReq() {
        StringRequest jsonStringReq = new StringRequest(
                URL + "/search",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        Log.d("Volley Response", response);
                        try {
                            requestResponse = new JSONArray(response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonStringReq);
    }
}