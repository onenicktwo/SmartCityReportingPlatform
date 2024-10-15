package com.example.citywatcherfrontend;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends CityWatcherActivity implements View.OnClickListener {

    private Button btnAdminView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdminView = findViewById(R.id.btnAdminView);
        btnAdminView.setOnClickListener(this);




        btnAdminView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to AdminViewActivity
                Intent mainIntent = new Intent(MainActivity.this, AdminViewActivity.class);
                startActivity(mainIntent);
            }
        });


    }

    @Override
    public void onClick(View view) {

    }
}