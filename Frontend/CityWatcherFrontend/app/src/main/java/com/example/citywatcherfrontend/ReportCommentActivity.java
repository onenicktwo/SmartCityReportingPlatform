package com.example.citywatcherfrontend;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class ReportCommentActivity extends CityWatcherActivity{
    EditText reportType;
    EditText reportReason;
    Button buttonReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_comment);

    }
}
