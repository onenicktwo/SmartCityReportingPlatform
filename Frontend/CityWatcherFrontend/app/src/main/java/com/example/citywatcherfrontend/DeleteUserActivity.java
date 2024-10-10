package com.example.citywatcherfrontend;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Bundle;
import android.view.View;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class DeleteUserActivity extends NavbarActivity {

    private EditText etUserId;
    private Button btnConfirmDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        etUserId = findViewById(R.id.etUserId);
        btnConfirmDelete = findViewById(R.id.btnConfirmDelete);

        btnConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userIdStr = etUserId.getText().toString();

                // Validate if the user ID is entered
                if (userIdStr.isEmpty()) {
                    Toast.makeText(DeleteUserActivity.this, "Please enter a User ID", Toast.LENGTH_SHORT).show();
                } else {
                    long userId = Long.parseLong(userIdStr);
                    deleteUser(userId);
                }
            }
        });

    }
    private void deleteUser(long userId) {
        String url = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/" + userId;

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> Toast.makeText(DeleteUserActivity.this, "User deleted successfully!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(DeleteUserActivity.this, "Error deleting user", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }
}
