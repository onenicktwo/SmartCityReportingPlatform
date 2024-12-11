package com.example.citywatcherfrontend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends CityWatcherActivity {

    private static final int REQUEST_IMAGE_PICK = 1;

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnProfileSave, btnSelectImage;
    private ImageView ivProfileImagePreview;

    private Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnProfileSave = findViewById(R.id.btnProfileSave);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        ivProfileImagePreview = findViewById(R.id.ivProfileImagePreview);

        loadUserProfile();

        btnSelectImage.setOnClickListener(v -> selectImage());
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

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                ivProfileImagePreview.setImageBitmap(selectedImageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to select image", Toast.LENGTH_SHORT).show();
            }
        }
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

        // Step 1: Update User Profile
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("username", username);
            userJson.put("email", email);
            userJson.put("password", password);
            userJson.put("role", role);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest profileUpdateRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                userJson,
                response -> {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    Log.d("EditProfileActivity", "Profile Update Response: " + response.toString());

                    // Step 2: Upload Profile Image (if selected)
                    if (selectedImageBitmap != null) {
                        uploadProfileImage();
                    }
                },
                error -> {
                    Log.e("EditProfileActivity", "Profile Update Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("EditProfileActivity", "Status Code: " + error.networkResponse.statusCode);
                    }
                    Toast.makeText(EditProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(profileUpdateRequest);
    }

    private void uploadProfileImage() {
        int userID = CityWatcherController.getInstance().getUserId();
        String url = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userID + "/image";

        if (selectedImageBitmap == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert the selected image to a Base64 string
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream); // Compress to JPEG
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        // Create the JSON payload
        JSONObject imageJson = new JSONObject();
        try {
            imageJson.put("imageBase64", base64Image);
            imageJson.put("fileName", "profile_" + userID + ".jpg"); // Example filename
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating image JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        // Make the request
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        Log.d("EditProfileActivity", "Image JSON: " + imageJson.toString());

        JsonObjectRequest imageUploadRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                imageJson,
                response -> {
                    Toast.makeText(EditProfileActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    Log.d("EditProfileActivity", "Image Upload Response: " + response.toString());
                },
                error -> {
                    Log.e("EditProfileActivity", "Image Upload Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("EditProfileActivity", "Status Code: " + error.networkResponse.statusCode);
                    }
                    Toast.makeText(EditProfileActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(imageUploadRequest);
    }



}

