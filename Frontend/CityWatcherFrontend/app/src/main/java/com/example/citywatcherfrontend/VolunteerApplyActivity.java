package com.example.citywatcherfrontend;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class VolunteerApplyActivity extends CityWatcherActivity {
    private EditText etName, etEmail, etReason;
    private Button btnSubmitApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_apply);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etReason = findViewById(R.id.et_reason);
        btnSubmitApplication = findViewById(R.id.btn_submit_application);

        btnSubmitApplication.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String reason = etReason.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || reason.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    submitApplication(name, email, reason);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid User ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void submitApplication(String name, String email, String reason) {
        int userId = CityWatcherController.getInstance().getUserId();
        String url = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userId;

        // Prepare JSON body
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("username", name);
            requestData.put("email", email);
            requestData.put("role", "VOLUNTEER");
            requestData.put("id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error preparing application data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a PUT request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, requestData,
                response -> Toast.makeText(VolunteerApplyActivity.this, "Application submitted successfully!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(VolunteerApplyActivity.this, "Error submitting application", Toast.LENGTH_SHORT).show());

        // Add request to the queue
        Volley.newRequestQueue(this).add(request);
    }
}
