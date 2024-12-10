package com.example.citywatcherfrontend;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class EditProfileActivity extends CityWatcherActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnProfileSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnProfileSave = findViewById(R.id.btnProfileSave);


        // Populate fields with existing user data
        loadUserProfile();

        btnProfileSave.setOnClickListener(v -> saveProfileChanges());

    }

    private void loadUserProfile() {
        int userID = CityWatcherController.getInstance().getUserId();
        String url = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userID;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        etUsername.setText(response.getString("username"));
                        etEmail.setText(response.getString("email"));
                        etPassword.setText(response.getString("password"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(EditProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(EditProfileActivity.this, "Error loading profile", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    private void saveProfileChanges() {
        int userID = CityWatcherController.getInstance().getUserId();
        String url = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userID;

        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(EditProfileActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(EditProfileActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject userJson = new JSONObject();
        try {
            userJson.put("username", username);
            userJson.put("email", email);
            userJson.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                userJson,
                response -> Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(EditProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }
}
