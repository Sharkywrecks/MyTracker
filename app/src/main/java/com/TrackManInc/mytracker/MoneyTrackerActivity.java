package com.TrackManInc.mytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MoneyTrackerActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton dayRadioButton, weekRadioButton, monthRadioButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_tracker);

        setupUIView();
    }

    private void setupUIView() {
        radioGroup = findViewById(R.id.radio_group);
        dayRadioButton = findViewById(R.id.day_radio_btn);
        weekRadioButton = findViewById(R.id.week_radio_btn);
        monthRadioButton = findViewById(R.id.month_radio_btn);
    }

}