package com.example.citywatcherfrontend;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AdminViewActivity extends CityWatcherActivity {

    private Button editUserButton, deleteUserButton, chatButton, volunteerApplyButton, analyticsBtn;

    @Override
    /**
     * Creates the Admin view from the layout
     * @author Sam Hostetter
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminview);

        editUserButton = findViewById(R.id.editUserButton);
        deleteUserButton = findViewById(R.id.deleteUserButton);
        chatButton = findViewById(R.id.chatButton);
        volunteerApplyButton = findViewById(R.id.volunteerApplyButton);
        analyticsBtn = findViewById(R.id.analyticsBtn);


        editUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to edit user page
                Intent editUserIntent = new Intent(AdminViewActivity.this, EditUserActivity.class);
                startActivity(editUserIntent);
            }
        });

        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to delete user page or handle delete action
                Intent deleteUserIntent = new Intent(AdminViewActivity.this, DeleteUserActivity.class);
                startActivity(deleteUserIntent);
            }
        });
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to chatroom page or handle chat action
                Intent chatIntent = new Intent(AdminViewActivity.this, ChatStartActivity.class);
                startActivity(chatIntent);
            }
        });
        volunteerApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent mainIntent = new Intent(AdminViewActivity.this, VolunteerApplyActivity.class);
                //startActivity(mainIntent);


            }
        });
        analyticsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent analyticsIntent = new Intent(AdminViewActivity.this, AdminAnalyticsActivity.class);
                startActivity(analyticsIntent);
            }
        });
    }
}


