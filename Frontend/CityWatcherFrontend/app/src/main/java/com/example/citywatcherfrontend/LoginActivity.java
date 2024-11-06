package com.example.citywatcherfrontend;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/*
Author @Sam Hostetter
 */

public class LoginActivity extends CityWatcherActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    loginUser(username, password);
                }


            }
        });

    }

    private void loginUser(String username, String password) {

        String url = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/";


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray usersArray = new JSONArray(response);

                            // Loop through users and check if the username and password match
                            for (int i = 0; i < usersArray.length(); i++) {
                                JSONObject user = usersArray.getJSONObject(i);
                                if (user.getString("username").equals(username) && user.getString("password").equals(password)) {
                                    CityWatcherController.getInstance().setLoggedIn(true);
                                    CityWatcherController.getInstance().setUserId(user.getInt("id"));
                                    break;
                                }

                            }

                            if (CityWatcherController.getInstance().isLoggedIn()) {
                                serverURL = "http://coms-3090-026.class.las.iastate.edu:8080/ws/issues/" + CityWatcherController.getInstance().getUserId();
                                WebSocketManager.getInstance().connectWebSocket(serverURL);
                                WebSocketManager.getInstance().setWebSocketListener(LoginActivity.this);
                                CityWatcherController.getInstance().setConnected(true);

                                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                Intent homeIntent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(homeIntent);
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}





