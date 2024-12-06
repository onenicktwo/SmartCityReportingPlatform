package com.example.citywatcherfrontend;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class EditUserActivity extends AppCompatActivity {

    private EditText etUserId, etEditUsername, etEditEmail, etEditPassword, etUserRole;
    private Button btnFetchUser, btnSaveChanges;

    @Override
    /**
     * Creates the Edit User view from the layout
     * @Author Sam Hostetter
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edituser);

        etUserId = findViewById(R.id.etUserId);
        etEditUsername = findViewById(R.id.etEditUsername);
        etEditEmail = findViewById(R.id.etEditEmail);
        etEditPassword = findViewById(R.id.etEditPassword);
        etUserRole = findViewById(R.id.etUserRole);
        btnFetchUser = findViewById(R.id.btnFetchUser);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        btnFetchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userIdStr = etUserId.getText().toString();

                // Validate if the user ID is entered
                if (userIdStr.isEmpty()) {
                    Toast.makeText(EditUserActivity.this, "Please enter a User ID", Toast.LENGTH_SHORT).show();
                } else {
                    long userId = Long.parseLong(userIdStr); // Convert to long
                    fetchUserDetails(userId);
                }
            }
        });

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveUserChanges();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * Fetches user details by sending a GET request to the server.
     * @author Sam Hostetter
     * @param userId User ID to fetch for
     */

    private void fetchUserDetails(long userId) {
        String url = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        etEditUsername.setText(response.getString("username"));
                        etEditEmail.setText(response.getString("email"));
                        etEditPassword.setText(response.getString("password"));
                        etUserRole.setText(response.getString("role"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(EditUserActivity.this, "Error fetching user details", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }

    /**
     * Saves user changes by sending a PUT request to the server.
     * @author Sam Hostetter
     * @throws JSONException
     */
    private void saveUserChanges() throws JSONException {
        String userIdStr = etUserId.getText().toString();
        String newUsername = etEditUsername.getText().toString();
        String newEmail = etEditEmail.getText().toString();
        String newPassword = etEditPassword.getText().toString();
        String newRole = etUserRole.getText().toString();

        if (userIdStr.isEmpty() || newUsername.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(EditUserActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        long userId = Long.parseLong(userIdStr);
        String url = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userId;

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", newUsername);
        jsonBody.put("email", newEmail);
        jsonBody.put("password", newPassword);
        jsonBody.put("role", newRole);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> Toast.makeText(EditUserActivity.this, "User updated successfully!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(EditUserActivity.this, "Error updating user", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }
}
