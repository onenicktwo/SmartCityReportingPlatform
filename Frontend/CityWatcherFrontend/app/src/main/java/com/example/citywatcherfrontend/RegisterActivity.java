package com.example.citywatcherfrontend;

/* Author @Sam Hostetter */

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.toolbox.Volley;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import com.android.volley.RequestQueue;
import android.content.Intent;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends CityWatcherActivity {

    private EditText etUsername, etEmail, etPassword;
    private Button btnRegister;
    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        textView2 = findViewById(R.id.textView2);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    /**
     * Registers a new user by sending a POST request to the server.
     */
    private void registerUser() {
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.length() < 8) {
            Toast.makeText(RegisterActivity.this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the JSON string for the user object
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("username", username);
            userJson.put("email", email);
            userJson.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RegisterActivity.this, "Failed to create user data.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare the request
        MultipartRequest multipartRequest = new MultipartRequest(
                "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/register",
                null,
                response -> {
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                },
                error -> {
                    Toast.makeText(RegisterActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

/// Add the user JSON as a form field
        multipartRequest.addPart(new MultipartRequest.FormPart("user", userJson.toString()));

// Add the default image as a file field
        byte[] defaultImageBytes = getDefaultImageBytes();
        if (defaultImageBytes != null) {
            multipartRequest.addPart(new MultipartRequest.FilePart("image", "image/jpg", "default_pfp.jpg", defaultImageBytes));
        }

// Add the request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(multipartRequest);
    }

    private byte[] getDefaultImageBytes() {
        try {
            // Load the image from drawable resources
            InputStream inputStream = getResources().openRawResource(R.drawable.default_pfp);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;

            // Read the image data into the byte array
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            inputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null if there's an error
        }
    }
}
