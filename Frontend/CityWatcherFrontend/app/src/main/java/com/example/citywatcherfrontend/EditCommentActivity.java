package com.example.citywatcherfrontend;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;


public class EditCommentActivity extends CityWatcherActivity {

    private EditText etEditCommentText;
    private Button btnSaveCommentChanges;
    private long issueId;
    private long commentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editcomment);

        etEditCommentText = findViewById(R.id.etEditCommentText);
        btnSaveCommentChanges = findViewById(R.id.btnSaveCommentChanges);

        Bundle bundle = getIntent().getExtras();

        // Extract values from the bundle
         issueId = bundle.getInt("issueID");
         commentId = bundle.getInt("commentID");


        if (commentId != -1 && userId != -1 && issueId != -1) {
            fetchCommentDetails(userId, issueId, commentId); // Fetch the comment details
        } else {
            Toast.makeText(EditCommentActivity.this, "Invalid IDs", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if IDs are not valid
        }

        btnSaveCommentChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveCommentChanges(userId, issueId, commentId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fetchCommentDetails(long userId, long issueId, long commentId) {
        String url = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/"
                + userId + "/issues/" + issueId + "/comments/" + commentId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        etEditCommentText.setText(response.getString("content"));  // Use "content" instead of "text"
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();  // Log the error
                    Toast.makeText(EditCommentActivity.this, "Error fetching comment", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void saveCommentChanges(long userId, long issueId, long commentId) throws JSONException {
        String updatedCommentText = etEditCommentText.getText().toString();

        if (updatedCommentText.isEmpty()) {
            Toast.makeText(EditCommentActivity.this, "Comment text cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://coms-3090-026.class.las.iastate.edu:8080/citywatcher/users/"
                + userId + "/issues/" + issueId + "/comments/" + commentId;

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("content", updatedCommentText);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> Toast.makeText(EditCommentActivity.this, "Comment updated successfully!", Toast.LENGTH_SHORT).show(),
                error -> {
                    error.printStackTrace();  // Log the error
                    Toast.makeText(EditCommentActivity.this, "Error updating comment", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
        finish();
    }
}
