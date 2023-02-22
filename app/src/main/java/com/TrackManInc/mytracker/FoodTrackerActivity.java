package com.TrackManInc.mytracker;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;


public class FoodTrackerActivity extends AppCompatActivity {

    private ProgressBar calorieBar, proteinBar, carbsBar;
    private Integer calorieVal, proteinVal, carbsVal, calorieTarget, proteinTarget, carbsTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_tracker);

        //temp values//
        calorieTarget = 2000;
        proteinTarget = 55;
        carbsTarget = 333;
        calorieVal = 1350;
        proteinVal = 20;
        carbsVal = 100;

        setUpUIView();
    }

    private void setUpUIView(){
        calorieBar = findViewById(R.id.calorieBar);
        proteinBar = findViewById(R.id.proteinBar);
        carbsBar = findViewById(R.id.carbsBar);

        calorieBar.setMax(calorieTarget);
        proteinBar.setMax(proteinTarget);
        carbsBar.setMax(carbsTarget);

        calorieBar.setProgress(calorieVal);
        proteinBar.setProgress(proteinVal);
        carbsBar.setProgress(carbsVal);

    }



}