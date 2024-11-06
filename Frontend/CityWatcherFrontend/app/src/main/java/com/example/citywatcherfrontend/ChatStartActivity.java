package com.example.citywatcherfrontend;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;



import org.java_websocket.handshake.ServerHandshake;



public class ChatStartActivity extends CityWatcherActivity implements WebSocketListener {
    private Button connectBtn;
    private EditText serverEtx, usernameEtx, useridEtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroomstart);

        /* initialize UI elements */
        connectBtn = (Button) findViewById(R.id.connectBtn);
        serverEtx = (EditText) findViewById(R.id.serverEdt);
        usernameEtx = (EditText) findViewById(R.id.unameEdt);
        useridEtx = (EditText) findViewById(R.id.uidEdt);

        /* connect button listener */
        connectBtn.setOnClickListener(view -> {
            String serverUrl = serverEtx.getText().toString() + usernameEtx.getText().toString();

            // Establish WebSocket connection and set listener
            WebSocketManager.getInstance().connectWebSocket(serverUrl);
            WebSocketManager.getInstance().setWebSocketListener(ChatStartActivity.this);

            String userID = useridEtx.getText().toString();

            Intent intent = new Intent(this, CityOfficalAdminChatroom.class);
            intent.putExtra("USER_ID", userID);
            startActivity(intent);
        });
    }


    @Override
    public void onWebSocketMessage(String message) {}

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {}

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}

    @Override
    public void onWebSocketError(Exception ex) {}
}






