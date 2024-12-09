package com.example.citywatcherfrontend;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.citywatcherfrontend.databinding.ActivityViewIssuesBinding;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ViewIssuesActivity extends CityWatcherActivity implements OnMapReadyCallback {

    private String URL;

    private ActivityViewIssuesBinding binding;
    private IssueListAdapter issueListAdapter;
    public JSONArray requestResponse;
    private ArrayList<IssueData> issueArrayList = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();

    IssueData issue;
    CommentData comment;

    private SupportMapFragment mapFragment;
    private MarkerOptions markerOptions;

    private ConstraintLayout issuePopupContainer;
    private ImageView issuePopupImage;
    private TextView issuePopupTitle;
    private TextView issuePopupCategory;
    private TextView issuePopupReporter;
    private TextView issuePopupLocation;
    private TextView issuePopupStatus;
    private Button buttonIssuePopupDetails;
    private ImageButton buttonIssuePopupExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        URL = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userId + "/issues";
        Log.d("URL", URL);

        binding = ActivityViewIssuesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        issuePopupContainer = findViewById(R.id.viewIssuesPopupContainer);
        issuePopupImage = findViewById(R.id.viewIssuesPopUpImage);
        issuePopupTitle = findViewById(R.id.viewIssuesPopupTitle);
        issuePopupCategory = findViewById(R.id.viewIssuesPopupCategory);
        issuePopupReporter = findViewById(R.id.viewIssuesPopupReporter);
        issuePopupLocation = findViewById(R.id.viewIssuesPopupLocation);
        issuePopupStatus = findViewById(R.id.viewIssuesPopupStatus);
        buttonIssuePopupDetails = findViewById(R.id.buttonViewIssuesPopupDetails);
        buttonIssuePopupExit = findViewById(R.id.buttonViewIssuesPopupExit);

        issuePopupContainer.setVisibility(View.GONE);

        buttonIssuePopupExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                issuePopupContainer.setVisibility(View.GONE);
            }
        });

        makeGetIssuesReqMap();

    }

    private void makeGetIssuesReqMap() {
        JsonArrayRequest jsonStringReq = new JsonArrayRequest(
                URL + "/search",
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response){
                        Log.d("Volley Response", "Issues retrieved");

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                String jsonString = response.get(i).toString();
                                System.out.println(jsonString);
                                issue = mapper.readValue(jsonString, IssueData.class);
                                issueArrayList.add(issue);
                                Log.d("Issue List", issueArrayList.get(i).getTitle());

                            } catch (JSONException | JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        mapFragment.getMapAsync((OnMapReadyCallback) ViewIssuesActivity.this);
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng camerraLatLng = new LatLng(42.0308, -93.6319);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camerraLatLng, 12f));

        HashMap<Marker, Integer> markerMap = new HashMap<Marker, Integer>();

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                issuePopupContainer.setVisibility(View.VISIBLE);

                int i = markerMap.get(marker);

                // issueImage.setImageResource();
                issuePopupReporter.setText(issueArrayList.get(i).getReporter().getUsername());
                issuePopupTitle.setText(issueArrayList.get(i).getTitle());
                issuePopupCategory.setText(issueArrayList.get(i).getCategory());
                issuePopupLocation.setText(issueArrayList.get(i).getAddress());

                String status = issueArrayList.get(i).getStatus();
                if (status.equals("REPORTED")) {
                    issuePopupStatus.setText("Reported");
                    issuePopupStatus.setTextColor(Color.RED);
                } else if (status.equals("UNDER_REVIEW")) {
                    issuePopupStatus.setText("Under Review");
                    issuePopupStatus.setTextColor(Color.YELLOW);
                } else {
                    issuePopupStatus.setText("Completed");
                    issuePopupStatus.setTextColor(Color.GREEN);
                }

                buttonIssuePopupDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ViewIssuesActivity.this, IssueDetailsActivity.class);

                        intent.putExtra("id", issueArrayList.get(i).getId());
                        intent.putExtra("reporter", issueArrayList.get(i).getReporter().getUsername());
                        intent.putExtra("reporterId", issueArrayList.get(i).getReporter().getId());
                        intent.putExtra("title", issueArrayList.get(i).getTitle());
                        intent.putExtra("category", issueArrayList.get(i).getCategory());
                        intent.putExtra("address", issueArrayList.get(i).getAddress());
                        intent.putExtra("status", issueArrayList.get(i).getStatus());
                        intent.putExtra("description", issueArrayList.get(i).getDescription());

                        startActivity(intent);

                    }
                });
                return false;
            }
        });

        Log.d("size", String.valueOf(issueArrayList.size()));
        for (int issueIndex = 0; issueIndex < issueArrayList.size(); issueIndex++) {
            IssueData issue = issueArrayList.get(issueIndex);
            Log.d("Issue", issue.getTitle());
            LatLng latlng = new LatLng(issue.getLatitude(), issue.getLongitude());
            markerOptions = new MarkerOptions();
            markerOptions.title(issue.getTitle());
            Log.d("Marker Title", markerOptions.getTitle());
            markerOptions.position(latlng);
            if (issue.getStatus().equals("UNDER_REVIEW")) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            } else if (issue.getStatus().equals("COMPLETED")) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }
            markerMap.put(googleMap.addMarker(markerOptions), issueIndex);
        }

    }
}