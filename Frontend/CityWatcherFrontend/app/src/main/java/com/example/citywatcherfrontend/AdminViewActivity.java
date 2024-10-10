package com.example.citywatcherfrontend;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AdminViewActivity extends CityWatcherActivity {

    private Button editUserButton, deleteUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminview);

        editUserButton = findViewById(R.id.editUserButton);
        deleteUserButton = findViewById(R.id.deleteUserButton);


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


    }
}
