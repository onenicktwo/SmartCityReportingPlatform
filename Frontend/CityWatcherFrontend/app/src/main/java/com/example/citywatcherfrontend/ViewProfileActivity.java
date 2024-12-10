package com.example.citywatcherfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewProfileActivity extends CityWatcherActivity {

    private ImageView ivProfilePicture;
    private TextView tvUsername, tvEmail, tvRole;
    private Button btnEditProfile, btnLogout;

    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewprofile);

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);

        requestQueue = Volley.newRequestQueue(this);

        // Assume userId is passed via Intent or stored in Session
        int userID = CityWatcherController.getInstance().getUserId();

        // Fetch profile data
        fetchUserProfile(userID);

        // Handle Edit Profile Button Click
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ViewProfileActivity.this, EditUserActivity.class);
            intent.putExtra("USER_ID", userId);
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
}
