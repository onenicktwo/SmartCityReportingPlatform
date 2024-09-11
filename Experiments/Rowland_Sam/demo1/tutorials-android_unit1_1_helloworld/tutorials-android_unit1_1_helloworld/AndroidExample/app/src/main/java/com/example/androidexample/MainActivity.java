package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;   // define message textview variable
    private SeekBar seekBarFontSize;
    private TextView textViewFontSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);             // link to Main activity XML

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt);      // link to message textview in the Main activity XML
        seekBarFontSize = findViewById(R.id.seekBarFontSize);
        textViewFontSize = findViewById(R.id.textViewFontSize);
        messageText.setText("Hello World");
        seekBarFontSize.setProgress(30);
        messageText.setTextSize(seekBarFontSize.getProgress());
        textViewFontSize.setText("Font Size: " + seekBarFontSize.getProgress());


        seekBarFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewFontSize.setText("Font Size: " + String.valueOf(i));
                messageText.setTextSize(Float.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}