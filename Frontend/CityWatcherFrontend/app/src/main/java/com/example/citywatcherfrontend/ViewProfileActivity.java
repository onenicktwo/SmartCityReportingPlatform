package com.example.citywatcherfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.citywatcherfrontend.databinding.ActivityViewprofileBinding;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewProfileActivity extends CityWatcherActivity {

    private ImageView ivProfilePicture;
    private TextView tvUsername, tvEmail, tvRole;
    private Button btnEditProfile, btnLogout;
    private ListView lvFollowedIssues;

    int profileId;

    private ActivityViewprofileBinding binding;
    private ArrayList<IssueData> followedIssueArrayList = new ArrayList<>();
    private IssueListAdapter followedListAdapter;
    private IssueData followedIssue;
    private ObjectMapper mapper = new ObjectMapper();

    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        binding = ActivityViewprofileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        profileId = bundle.getInt("profileId");

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        lvFollowedIssues = findViewById(R.id.lvFollowedIssues);


        requestQueue = Volley.newRequestQueue(this);



        // Fetch profile data
        fetchUserProfile(profileId);

        // Fetch followed issues
        makeGetFollowedIssuesReq(profileId);

        // Allows only the profile owner to edit profile or logout
        if (!loggedIn || userId != profileId) {
            btnEditProfile.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
        }

        // Handle Edit Profile Button Click
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ViewProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Handle Logout Button Click
        btnLogout.setOnClickListener(v -> {
            // Clear session or token (if implemented)
            Toast.makeText(ViewProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ViewProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Followed Issue ListView click listener
        lvFollowedIssues.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ViewProfileActivity.this, IssueDetailsActivity.class);

                intent.putExtra("id", followedIssueArrayList.get(i).getId());
                intent.putExtra("reporter", followedIssueArrayList.get(i).getReporter().getUsername());
                intent.putExtra("reporterId", followedIssueArrayList.get(i).getReporter().getId());
                intent.putExtra("title", followedIssueArrayList.get(i).getTitle());
                intent.putExtra("category", followedIssueArrayList.get(i).getCategory());
                intent.putExtra("address", followedIssueArrayList.get(i).getAddress());
                intent.putExtra("status", followedIssueArrayList.get(i).getStatus());
                intent.putExtra("description", followedIssueArrayList.get(i).getDescription());

                startActivity(intent);
            }
        });
    }

    /**
     * Fetches the user profile data from the backend.
     */
    private void fetchUserProfile(int userId) {
        String url = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        // Parse the JSON response
                        JSONObject jsonObject = new JSONObject(response);
                        String username = jsonObject.getString("username");
                        String email = jsonObject.getString("email");
                        String role = jsonObject.getString("role");
                        String profileImagePath = jsonObject.optString("profileImagePath");

                        // Update UI elements
                        tvUsername.setText(username);
                        tvEmail.setText("Email: " + email);
                        tvRole.setText("Role: " + role);

                        // Load profile image if available
                        if (profileImagePath != null && !profileImagePath.isEmpty()) {
                            loadImage(profileImagePath);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ViewProfileActivity.this, "Error parsing profile data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(ViewProfileActivity.this, "Error fetching profile data", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    /**
     * Loads the profile image from a URL.
     */
    private void loadImage(String imageUrl) {
        ImageLoader imageLoader = VolleySingleton.getInstance(this).getImageLoader();
        imageLoader.get(imageUrl, ImageLoader.getImageListener(ivProfilePicture,
                R.drawable.default_pfp, // Placeholder image
                R.drawable.default_pfp_2)); // Error image
    }

    private void makeGetFollowedIssuesReq(int userId) {
        JsonArrayRequest jsonStringReq = new JsonArrayRequest(
                "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userId + "/followed-issues",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response){
                        Log.d("Volley Response", "Issues retrieved");

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                String jsonString = response.get(i).toString();
                                System.out.println(jsonString);
                                followedIssue = mapper.readValue(jsonString, IssueData.class);
                                followedIssueArrayList.add(followedIssue);
                                Log.d("Issue List", followedIssueArrayList.get(i).getTitle());

                            } catch (JSONException | JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        followedListAdapter = new IssueListAdapter(ViewProfileActivity.this, followedIssueArrayList);
                        binding.lvFollowedIssues.setAdapter(followedListAdapter);
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
