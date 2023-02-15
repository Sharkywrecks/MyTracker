package com.TrackManInc.mytracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class FoodTrackerActivity extends AppCompatActivity {

    private RadioButton weekRadioButton, monthRadioButton, yearRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_tracker);
        setUpUIView();


        Button btn = findViewById(R.id.btnApply);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setUpUIView(){
        weekRadioButton = findViewById(R.id.rbtnWeek);
        monthRadioButton = findViewById(R.id.rbtnMonth);
        yearRadioButton = findViewById(R.id.rbtnYear);
    }

}