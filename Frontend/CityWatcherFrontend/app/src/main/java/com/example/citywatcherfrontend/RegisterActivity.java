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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

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

        // Load default profile image and save it as a file
        Bitmap defaultProfileImage = getDefaultProfileImage();
        if (defaultProfileImage == null) {
            Toast.makeText(this, "Default profile image could not be loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert Bitmap to File
        File imageFile = saveBitmapToFile(defaultProfileImage);

        // Create a JSON object for user details
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("username", username);
            userJson.put("email", email);
            userJson.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Define the server endpoint
        String url = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/register";

        // Create a multipart request
        RegisterActivityMulti multipartRequest = new RegisterActivityMulti(
                url, // URL
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "UTF-8");
                                Log.e("RegisterError", "Response: " + responseBody);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(RegisterActivity.this, "Registration Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },

        new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle success response
                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                    }
                },
                imageFile, // Pass the image file
                new HashMap<String, String>() {{
                    put("user", userJson.toString()); // Add user JSON as a string
                }}
        );

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multipartRequest);
    }

    /**
     * Converts the Bitmap into a File.
     */
    private File saveBitmapToFile(Bitmap bitmap) {
        File tempFile = new File(getCacheDir(), "profile_image.jpg");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    /**
     * Loads a default profile image from the resources (optional).
     */
    private Bitmap getDefaultProfileImage() {
        // Returning null here because you are saving an image from Bitmap as file
        return BitmapFactory.decodeResource(getResources(), R.drawable.default_pfp);
    }
}
