package com.example.citywatcherfrontend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends CityWatcherActivity {

    private EditText etUsername, etEmail, etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(view -> registerUser());
    }

    /**
     * Registers a new user by sending a POST request to the server.
     */
    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate user input
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.length() < 8) {
            Toast.makeText(RegisterActivity.this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user JSON
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("username", username);
            userJson.put("email", email);
            userJson.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating user data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert the Bitmap to a byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = getDefaultProfileImage();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageByteArray = stream.toByteArray();

        // Define file and JSON parts
        Map<String, String> jsonParts = new HashMap<>();
        jsonParts.put("user", userJson.toString()); // Send the user as a JSON string

        Map<String, byte[]> fileParts = new HashMap<>();
        fileParts.put("image", imageByteArray); // Use "image" as the key to match the backend

        // Define the server endpoint
        String url = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/register";

        // Create a multipart request
        MultipartRequest multipartRequest = new MultipartRequest(
                url,
                response -> {
                    // Handle success
                    String jsonString = new String(response.data); // Convert response to string
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                },
                error -> {
                    Log.e("Volley Error", "Error: " + error.toString());
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                },
                jsonParts, // JSON data
                fileParts  // File data
        ) {
            @Override
            protected void deliverResponse(String response) {
                // Handle the response if necessary
            }
        };

        // Add the request to the request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multipartRequest);
    }


    private Bitmap getDefaultProfileImage() {
        return BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_3);
    }
    }
