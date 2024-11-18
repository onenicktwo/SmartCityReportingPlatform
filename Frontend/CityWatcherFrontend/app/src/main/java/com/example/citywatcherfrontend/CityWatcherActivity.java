package com.example.citywatcherfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;



public abstract class CityWatcherActivity extends AppCompatActivity implements WebSocketListener {

    protected final String MAPS_KEY = "AIzaSyBWM0V9EhmOR0jCz7NYo9xDcmOW6Ni-trc";

    protected int userId = CityWatcherController.getInstance().getUserId();
    protected boolean loggedIn = CityWatcherController.getInstance().isLoggedIn();
    protected String serverURL;

    // Initialize activity variables
    protected ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = getSupportActionBar();
        toolbar.setTitle("CityWatcher");

        if (CityWatcherController.getInstance().isConnected()) {
            WebSocketManager.getInstance().setWebSocketListener(this);
        }
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
        int itemId = item.getItemId();
        if (itemId == R.id.navbar_menu_createIssue) {
            Intent intent;
            if (CityWatcherController.getInstance().isLoggedIn()) {
                intent = new Intent(this, CreateIssueActivity.class);
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
            startActivity(intent);

        } else if (itemId == R.id.navbar_menu_viewIssues) {
            Intent intent = new Intent(this, ViewIssuesActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.navbar_profile_login) {
             Intent intent = new Intent(this, LoginActivity.class);
             startActivity(intent);
        } else if (itemId == R.id.navbar_profile_signUp) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.navbar_profile_viewProfile) {
            Toast.makeText(this, "Viewing Profile", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(this, ViewProfileActivity.class);
            //startActivity(intent)
        } else if (itemId == R.id.navbar_profile_logOut) {
            Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        Log.d("WebSocket", "Opened");
    }

    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(() -> {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    String notificationType = jsonObject.get("notificationType").toString();
                    switch (notificationType) {
                        case "UPDATE": {
                            String issueTitle = jsonObject.get("title").toString();
                            String issueStatus = jsonObject.get("status").toString();
                            Toast.makeText(CityWatcherActivity.this, "Issue" + "\"" + issueTitle + "\"" + "updated to" + issueStatus, Toast.LENGTH_LONG).show();
                            break;
                        }
                        case "ASSIGNMENT": {
                            String issueTitle = jsonObject.get("title").toString();
                            Toast.makeText(CityWatcherActivity.this, "You have been assigned to issue \"" + issueTitle + "\"", Toast.LENGTH_LONG).show();
                            break;
                        }
                        case "COMMENT": {
                            String issueTitle = jsonObject.get("title").toString();
                            Toast.makeText(CityWatcherActivity.this, "New comment in issue \"" + issueTitle + "\"", Toast.LENGTH_LONG).show();
                            break;
                        }
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Log.d("WebSocket", String.valueOf(code));
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Log.d("WebSocket Error", ex.toString());
    }
}