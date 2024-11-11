package com.example.citywatcherfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CitizenViewActivity extends CityWatcherActivity {

    private Button volunteerApplyBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizenview);

        volunteerApplyBtn = findViewById(R.id.vonunteerApplyBtn);

        volunteerApplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivty = new Intent(CitizenViewActivity.this, VolunteerApplyActivity.class);
                startActivity(mainActivty);
            }
        });
    }
}
