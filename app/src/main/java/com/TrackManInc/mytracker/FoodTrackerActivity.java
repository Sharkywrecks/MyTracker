package com.TrackManInc.mytracker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;


public class FoodTrackerActivity extends AppCompatActivity {

    private RadioButton weekRadioButton, monthRadioButton, yearRadioButton;
    private BarChart barCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_tracker);
        setUpUIView();
    }

    private void setUpUIView(){
        weekRadioButton = findViewById(R.id.rbtnWeek);
        monthRadioButton = findViewById(R.id.rbtnMonth);
        yearRadioButton = findViewById(R.id.rbtnYear);
        barCalories = findViewById(R.id.barCalories);
        graphSettings();
    }

    private void graphSettings(){
        barCalories.setNoDataText("No Data Available");
        barCalories.setBackgroundColor(Color.WHITE);
        barCalories.setNoDataTextColor(Color.BLACK);
        barCalories.setBorderColor(Color.BLACK);
        barCalories.setDrawBorders(true);
        barCalories.setBorderWidth(3);

        barCalories.getXAxis().setDrawGridLines(false);
        barCalories.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        barCalories.getAxisLeft().setDrawGridLines(true);
        barCalories.getAxisLeft().setDrawLabels(true);
        barCalories.getAxisLeft().setAxisMinimum(0);

        barCalories.getAxisRight().setDrawGridLines(true);
        barCalories.getAxisRight().setDrawLabels(true);
        barCalories.getAxisRight().setAxisMinimum(0);

        barCalories.setTouchEnabled(true);
        barCalories.setPinchZoom(false);
        barCalories.setScaleEnabled(false);
        barCalories.setDragEnabled(true);

        Description chartDescription = new Description();
        chartDescription.setText("Calorie Tracker");
        chartDescription.setTextColor(Color.BLACK);
        chartDescription.setTextSize(20);
        chartDescription.setEnabled(false);
        barCalories.setDescription(chartDescription);
    }

    public void onRadioButtonClicked(View view){
        generateGraph(view.getId());
    }

    private void generateGraph(int id) {
        switch (id) {
        }
    }

}