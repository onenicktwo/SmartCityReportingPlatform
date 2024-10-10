package com.example.citywatcherfrontend;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class IssueDetailsActivity extends CityWatcherActivity {

    // Initialize activity variables
    private ImageView issueDetailsImage;
    private TextView issueDetailsTitle;
    private TextView issueDetailsCategory;
    private TextView issueDetailsLocation;
    private TextView issueDetailsStatus;
    private TextView issueDetailsDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.activity_issue_details);

        issueDetailsImage = findViewById(R.id.issueDetailsImage);
        issueDetailsCategory = findViewById(R.id.issueDetailsCategory);
        issueDetailsTitle = findViewById(R.id.issueDetailsTitle);
        issueDetailsLocation = findViewById(R.id.issueDetailsLocation);
        issueDetailsStatus = findViewById(R.id.issueDetailsStatus);
        issueDetailsDescription = findViewById(R.id.issueDetailsDescription);

        // TODO Set image
        issueDetailsTitle.setText(bundle.getString("title"));
        issueDetailsCategory.setText(bundle.getString("category"));
        // TODO Set reporter
        issueDetailsCategory.setText("Category");
        // TODO Set location
        issueDetailsLocation.setText("Location");


        String status = bundle.getString("status");
        if (status.equals("REPORTED")) {
            issueDetailsStatus.setText("Reported");
            issueDetailsStatus.setTextColor(Color.RED);
        } else if (status.equals("UNDER_REVIEW")) {
            issueDetailsStatus.setText("Under Review");
            issueDetailsStatus.setTextColor(Color.YELLOW);
        } else {
            issueDetailsStatus.setText("Completed");
            issueDetailsStatus.setTextColor(Color.GREEN);
        }

        issueDetailsDescription.setText(bundle.getString("description"));

        // TODO Set buttons for admin view
        // TODO Comments
    }
}