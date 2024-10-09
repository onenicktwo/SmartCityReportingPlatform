package com.example.citywatcherfrontend;

import static com.example.citywatcherfrontend.IssueStatus.REPORTED;
import static com.example.citywatcherfrontend.IssueStatus.UNDER_REVIEW;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class IssueListAdapter extends ArrayAdapter<Issue> {
    public IssueListAdapter(@NonNull Context context, ArrayList<Issue> issueArrayList) {
        super(context, R.layout.list_item_issue, issueArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Issue issue = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_issue, parent, false);
        }

        ImageView issueImage = view.findViewById(R.id.issueViewImage);
        TextView issueTitle = view.findViewById(R.id.issueViewTitle);
        TextView issueLocation = view.findViewById(R.id.issueViewLocation);
        TextView issueStatus = view.findViewById(R.id.issueViewStatus);


        // issueImage.setImageResource();
        issueTitle.setText(issue.getTitle());
        issueLocation.setText("Location");

        IssueStatus status = issue.getStatus();
        if (status == REPORTED) {
            issueStatus.setText("Reported");
            issueStatus.setTextColor(Color.RED);
        } else if (status == UNDER_REVIEW) {
            issueStatus.setText("Under Review");
            issueStatus.setTextColor(Color.YELLOW);
        } else {
            issueStatus.setText("Completed");
            issueStatus.setTextColor(Color.GREEN);
        }

        return view;
    }
}
