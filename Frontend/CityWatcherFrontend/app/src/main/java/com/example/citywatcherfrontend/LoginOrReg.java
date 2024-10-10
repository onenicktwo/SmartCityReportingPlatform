package com.example.citywatcherfrontend;

/*
Author @Sam Hostetter
 */
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class LoginOrReg extends CityWatcherActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginorreg_activity);


        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnSkipLogin = findViewById(R.id.btnSkipLogin);

        // Set click listener for Login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginActivity
                Intent loginIntent = new Intent(LoginOrReg.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        // Set click listener for Register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to RegisterActivity
                Intent registerIntent = new Intent(LoginOrReg.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
        // Set click listener for Skip Login button
        btnSkipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent mainIntent = new Intent(LoginOrReg.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });
        }

    }

