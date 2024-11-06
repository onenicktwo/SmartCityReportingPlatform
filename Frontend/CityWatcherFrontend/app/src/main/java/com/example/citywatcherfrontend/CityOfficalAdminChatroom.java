package com.example.citywatcherfrontend;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.citywatcherfrontend.CityWatcherActivity;
import com.example.citywatcherfrontend.R;
import com.example.citywatcherfrontend.WebSocketListener;
import com.example.citywatcherfrontend.WebSocketManager;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;


public class CityOfficalAdminChatroom extends CityWatcherActivity implements WebSocketListener {

    private Button sendBtn;
    private EditText msgEtx;
    private TextView msgTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cityadminchatoom);

        String userID = getIntent().getStringExtra("USER_ID");

        /* initialize UI elements */
        sendBtn = (Button) findViewById(R.id.sendBtn);
        msgEtx = (EditText) findViewById(R.id.msgEdt);
        msgTv = (TextView) findViewById(R.id.tx1);

        /* connect this activity to the websocket instance */
        WebSocketManager.getInstance().setWebSocketListener(CityOfficalAdminChatroom.this);

        /* send button listener */
        sendBtn.setOnClickListener(v -> {
            try {
                // Create the JSON message to send
                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("senderId", userID);
                jsonMessage.put("content", msgEtx.getText().toString());

                // Send JSON message through WebSocket
                WebSocketManager.getInstance().sendMessage(jsonMessage.toString());

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("ExceptionSendMessage", e.getMessage());
            }
        });

    }


    @Override
    public void onWebSocketMessage(String message) {
        /**
         * In Android, all UI-related operations must be performed on the main UI thread
         * to ensure smooth and responsive user interfaces. The 'runOnUiThread' method
         * is used to post a runnable to the UI thread's message queue, allowing UI updates
         * to occur safely from a background or non-UI thread.
         */
        runOnUiThread(() -> {
            String s = msgTv.getText().toString();
            msgTv.setText(s + "\n"+message);
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {
            String s = msgTv.getText().toString();
            msgTv.setText(s + "---\nconnection closed by " + closedBy + "\nreason: " + reason);
        });
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}

    @Override
    public void onWebSocketError(Exception ex) {}
}

