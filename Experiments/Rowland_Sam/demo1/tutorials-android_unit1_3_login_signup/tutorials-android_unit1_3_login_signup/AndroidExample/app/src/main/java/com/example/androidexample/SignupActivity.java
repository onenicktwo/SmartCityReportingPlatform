package com.example.androidexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText usernameEditText;  // define username edittext variable
    private EditText passwordEditText;  // define password edittext variable
    private EditText confirmEditText;   // define confirm edittext variable
    private Button loginButton;         // define login button variable
    private Button signupButton;        // define signup button variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        /* initialize UI elements */
        toolbar = (Toolbar) findViewById(R.id.toolbar_signup);
        usernameEditText = findViewById(R.id.signup_username_edt);  // link to username edtext in the Signup activity XML
        passwordEditText = findViewById(R.id.signup_password_edt);  // link to password edtext in the Signup activity XML
        confirmEditText = findViewById(R.id.signup_confirm_edt);    // link to confirm edtext in the Signup activity XML
        loginButton = findViewById(R.id.signup_login_btn);    // link to login button in the Signup activity XML
        signupButton = findViewById(R.id.signup_signup_btn);  // link to signup button in the Signup activity XML

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Sign Up");

        /* click listener on login button pressed */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when login button is pressed, use intent to switch to Login Activity */
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);  // go to LoginActivity
            }
        });

        /* click listener on signup button pressed */
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* grab strings from user inputs */
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirm = confirmEditText.getText().toString();
                boolean validPassword = true;
                boolean validPasswordLength = false;
                boolean containsNumber = false;

                if (password.length() >= 8) {
                    validPasswordLength = true;
                }

                String[] nums = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
                for (int i = 0; i < nums.length; i++) {
                    if (password.contains(nums[i])) {
                        containsNumber = true;
                    }
                }

                if (!password.equals(confirm)) {
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    if (!validPasswordLength) {
                        Toast.makeText(SignupActivity.this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
                        validPassword = false;
                    }

                    if (!containsNumber) {
                        Toast.makeText(SignupActivity.this, "Password must contain a number", Toast.LENGTH_SHORT).show();
                        validPassword = false;
                    }

                    if (validPassword) {
                        Toast.makeText(SignupActivity.this, "Signing up", Toast.LENGTH_SHORT).show();
                    }
                }

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
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.menu_signup) {
            Intent intent = new Intent(SignupActivity.this, SignupActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}