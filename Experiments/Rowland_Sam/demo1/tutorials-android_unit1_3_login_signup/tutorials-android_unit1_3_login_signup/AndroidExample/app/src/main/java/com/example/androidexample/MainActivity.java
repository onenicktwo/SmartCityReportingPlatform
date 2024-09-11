package com.example.androidexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActionBar actionBar;
    private TextView messageText;   // define message textview variable
    private TextView usernameText;  // define username textview variable
    private Button loginButton;     // define login button variable
    private Button signupButton;    // define signup button variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML



        /* initialize UI elements */
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        messageText = findViewById(R.id.main_msg_txt);      // link to message textview in the Main activity XML
        usernameText = findViewById(R.id.main_username_txt);// link to username textview in the Main activity XML
        loginButton = findViewById(R.id.main_login_btn);    // link to login button in the Main activity XML
        signupButton = findViewById(R.id.main_signup_btn);  // link to signup button in the Main activity XML

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Home");

        /* extract data passed into this activity from another activity */
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            messageText.setText("Home Page");
            usernameText.setVisibility(View.INVISIBLE);             // set username text invisible initially
        } else {
            messageText.setText("Welcome");
            usernameText.setText(extras.getString("USERNAME")); // this will come from LoginActivity
            loginButton.setVisibility(View.INVISIBLE);              // set login button invisible
            signupButton.setVisibility(View.INVISIBLE);             // set signup button invisible
        }

        /* click listener on login button pressed */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when login button is pressed, use intent to switch to Login Activity */
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        /* click listener on signup button pressed */
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    // method to inflate the options menu when
    // the user opens the menu for the first time
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {

        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // methods to control the operations that will
    // happen when user clicks on the action buttons
    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {

        int itemId = item.getItemId();
        if (itemId == R.id.action_add) {
            Toast.makeText(this, "Add Clicked", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.action_menu) {
            Toast.makeText(this, "Menu Clicked", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.menu_login) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.menu_signup) {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}