package com.example.citywatcherfrontend;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class VolunteerApplyActivity extends CityWatcherActivity{
    private EditText etName, etContactNumber, etReason;
    private Button btnSubmitApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_apply);

        etName = findViewById(R.id.et_name);
        etContactNumber = findViewById(R.id.et_contact_number);
        etReason = findViewById(R.id.et_reason);
        btnSubmitApplication = findViewById(R.id.btn_submit_application);

        btnSubmitApplication.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String contactNumber = etContactNumber.getText().toString().trim();
            String reason = etReason.getText().toString().trim();

            if (name.isEmpty() || contactNumber.isEmpty() || reason.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, "Application submitted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
