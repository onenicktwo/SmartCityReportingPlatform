package com.example.citywatcherfrontend;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends CityWatcherActivity implements View.OnClickListener {

    private Button strBtn, jsonObjBtn, jsonArrBtn, imgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        /* button click listeners */

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

    }
}