package com.example.citywatcherfrontend;

import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class AdminAnalyticsActivity extends CityWatcherActivity {

    private TextView tvStatusStats, tvOfficialWorkload, tvLocationStats, tvResponseTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminstats);

        tvStatusStats = findViewById(R.id.tvStatusStats);
        tvOfficialWorkload = findViewById(R.id.tvOfficialWorkload);

        fetchStatusStats();
        fetchOfficialWorkloadStats();
    }

    private void fetchStatusStats() {
        String url = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/analytics/status-stats";


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {

                    String stats = parseStatusStats(response);
                    tvStatusStats.setText(stats);
                },
                error -> tvStatusStats.setText("Error fetching status stats: " + error.getMessage())
        );

        Volley.newRequestQueue(this).add(request);
    }

    private String parseStatusStats(JSONObject response) {
        StringBuilder builder = new StringBuilder();

        try {
            int totalIssues = response.getInt("totalIssues");
            JSONObject issuesByStatus = response.getJSONObject("issuesByStatus");
            JSONObject percentageByStatus = response.getJSONObject("percentageByStatus");
            double averageResolutionTime = response.getDouble("averageResolutionTime");

            builder.append("Total Issues: ").append(totalIssues).append("\n\n");

            builder.append("Issues by Status:\n");
            for (Iterator<String> it = issuesByStatus.keys(); it.hasNext(); ) {
                String status = it.next();
                builder.append("- ").append(status).append(": ").append(issuesByStatus.getInt(status)).append("\n");
            }

            builder.append("\nPercentage by Status:\n");
            for (Iterator<String> it = percentageByStatus.keys(); it.hasNext(); ) {
                String status = it.next();
                builder.append("- ").append(status).append(": ").append(percentageByStatus.getDouble(status)).append("%\n");
            }

            builder.append("\nAverage Resolution Time: ").append(averageResolutionTime).append(" hours");
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error parsing status stats.";
        }

        return builder.toString();
    }




    private void fetchOfficialWorkloadStats() {
        String url = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/analytics/official-workload";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    String workload = parseOfficialWorkloadStats(response);
                    tvOfficialWorkload.setText(workload);
                },
                error -> tvOfficialWorkload.setText("Error fetching workload stats: " + error.getMessage())
        );

        Volley.newRequestQueue(this).add(request);
    }

    private String parseOfficialWorkloadStats(JSONArray response) {
        StringBuilder builder = new StringBuilder();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject official = response.getJSONObject(i);
                builder.append(official.getString("officialUsername")) // Changed from "officialName" to "officialUsername"
                        .append(": ")
                        .append(official.getInt("resolvedIssues"))
                        .append(" tasks completed\n")
                        .append(official.getDouble("averageResolutionTime")) // Changed from getInt to getDouble
                        .append(" Average time taken\n\n"); // Added newline characters for better formatting
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error parsing workload stats.";
        }
        return builder.toString();
    }

    private void fetchLocationStats() {
        String url = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/analytics/location-stats";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {

                    String stats = parseLocationStats(response);
                    TextView tvLocationStats = findViewById(R.id.tvLocationStats);
                    tvLocationStats.setText(stats);
                },
                error -> {
                    TextView tvLocationStats = findViewById(R.id.tvLocationStats);
                    tvLocationStats.setText("Error fetching location stats: " + error.getMessage());
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private String parseLocationStats(JSONArray response) {
        StringBuilder builder = new StringBuilder();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject location = response.getJSONObject(i);
                builder.append("Region: ").append(location.getString("region"))
                        .append("\nIssues: ").append(location.getInt("issueCount"))
                        .append("\n\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error parsing location stats.";
        }
        return builder.toString();
    }




    private void fetchResponseTimeStats() {
        String url = "https://coms-3090-026.class.las.iastate.edu:8080/citywatcher/analytics/response-time-stats";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    String stats = parseResponseTimeStats(response);
                    TextView tvResponseTimeStats = findViewById(R.id.tvResponseTime);
                    tvResponseTimeStats.setText(stats);
                },
                error -> {
                    TextView tvResponseTimeStats = findViewById(R.id.tvResponseTime);
                    tvResponseTimeStats.setText("Error fetching response time stats: " + error.getMessage());
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private String parseResponseTimeStats(JSONObject response) {
        try {
            double averageResponseTime = response.getDouble("averageResponseTime");
            double medianResponseTime = response.getDouble("medianResponseTime");
            return "Average Response Time: " + averageResponseTime + " hours\n" +
                    "Median Response Time: " + medianResponseTime + " hours";
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error parsing response time stats.";
        }
    }



}
