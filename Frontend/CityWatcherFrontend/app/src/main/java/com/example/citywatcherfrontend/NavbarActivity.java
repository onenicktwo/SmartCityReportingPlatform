package com.example.citywatcherfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NavbarActivity extends AppCompatActivity {

    // Initialize activity variables
    protected ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = getSupportActionBar();
        toolbar.setTitle("CityWatcher");
    }

    // method to inflate the options menu when
    // the user opens the menu for the first time
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {

        getMenuInflater().inflate(R.menu.menu_navbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // methods to control the operations that will
    // happen when user clicks on the action buttons
    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {
        // TODO Uncomment Intents and remove Toast calls after activities have been made
        int itemId = item.getItemId();
        if (itemId == R.id.navbar_menu_createIssue) {
            Toast.makeText(this, "Creating Issue", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CreateIssueActivity.class);
            startActivity(intent);

        } else if (itemId == R.id.navbar_menu_viewIssues) {
            Toast.makeText(this, "Viewing Issues", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.navbar_profile_login) {
            Toast.makeText(this, "Logging In", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(this, LoginActivity.class);
            // startActivity(intent);
        } else if (itemId == R.id.navbar_profile_signUp) {
            Toast.makeText(this, "Signing Up", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(this, SignupActivity.class);
            //startActivity(intent);
        } else if (itemId == R.id.navbar_profile_viewProfile) {
            Toast.makeText(this, "Viewing Profile", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(this, ViewProfileActivity.class);
            //startActivity(intent)
        } else if (itemId == R.id.navbar_profile_logOut) {
            Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}