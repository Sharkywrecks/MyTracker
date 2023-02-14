package com.TrackManInc.mytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Random;

public class MoneyTrackerActivity extends AppCompatActivity {

    private RadioButton weekRadioButton, monthRadioButton, yearRadioButton;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_tracker);
        setupUIView();
    }

    private void setupUIView() {
        weekRadioButton = findViewById(R.id.week_radio_btn);
        monthRadioButton = findViewById(R.id.month_radio_btn);
        yearRadioButton = findViewById(R.id.year_radio_btn);
        barChart = findViewById(R.id.bar_chart);
        graphSettings();
        weekRadioButton.setChecked(true);
        generateGraph(R.id.week_radio_btn); // defaults to week graph
    }

    private void graphSettings(){  // temporary settings for now
        barChart.setNoDataText("No Data.");
        barChart.setBackgroundColor(Color.WHITE);
        barChart.setNoDataTextColor(Color.BLACK);
        barChart.setBorderColor(Color.BLACK);
        barChart.setDrawBorders(true);
        barChart.setBorderWidth(3);

        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);


        barChart.getAxisLeft().setDrawGridLines(true);
        barChart.getAxisLeft().setDrawLabels(true);
        barChart.getAxisLeft().setAxisMinimum(0);

        barChart.getAxisRight().setDrawGridLines(true);
        barChart.getAxisRight().setDrawLabels(true);
        barChart.getAxisRight().setAxisMinimum(0);

        barChart.setTouchEnabled(true);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setDragEnabled(true);

        Description chartDescription = new Description();
        chartDescription.setText("Money Tracker");
        chartDescription.setTextColor(Color.BLACK);
        chartDescription.setTextSize(20);
        chartDescription.setEnabled(false); // hidden
        barChart.setDescription(chartDescription);
    }

    public void onRadioButtonClicked(View view) {
        generateGraph(view.getId());
    }

    private void generateGraph(int id) {
        switch (id){
            case R.id.week_radio_btn:
                monthRadioButton.setChecked(false);
                yearRadioButton.setChecked(false);
                BarDataSet weekData = findWeekData();
                addDataToGraph(weekData);
                break;
            case R.id.month_radio_btn:
                weekRadioButton.setChecked(false);
                yearRadioButton.setChecked(false);
                BarDataSet monthData = findMonthData();
                addDataToGraph(monthData);
                break;
            case R.id.year_radio_btn:
                weekRadioButton.setChecked(false);
                monthRadioButton.setChecked(false);
                BarDataSet yearData = findYearData();
                addDataToGraph(yearData);
                break;
        }
    }

    private void addDataToGraph(BarDataSet barDataSet){
        BarData barData = new BarData();
        barData.addDataSet(barDataSet);
        barChart.animateXY(500, 900, Easing.Linear, Easing.EaseOutCubic);
        barChart.setData(barData);
        barChart.invalidate();
    }

    private BarDataSet findWeekData(){ // will eventually add real data from db instead
        final String[] daysOfWeek = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        barChart.getXAxis().setLabelCount(daysOfWeek.length);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(daysOfWeek));

        ArrayList<BarEntry> testData = new ArrayList<>();
        for(int dayOfWeek = 0; dayOfWeek<7; dayOfWeek++){
            testData.add(new BarEntry(dayOfWeek, new Random().nextInt(20)));
        }

        return new BarDataSet(testData, "Test Data Set 1");
    }

    private BarDataSet findMonthData(){ // will eventually add real data from db instead
        ArrayList<BarEntry> testData = new ArrayList<>();
        testData.add(new BarEntry(1,2));
        testData.add(new BarEntry(2,3));
        testData.add(new BarEntry(3,4));
        testData.add(new BarEntry(4,24));
        testData.add(new BarEntry(5,6));
        testData.add(new BarEntry(6,7));
        testData.add(new BarEntry(7,8));
        return new BarDataSet(testData, "Test Data Set 2");
    }

    private BarDataSet findYearData(){ // will eventually add real data from db instead
        final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        barChart.getXAxis().setLabelCount(months.length);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(months));
        ArrayList<BarEntry> testData = new ArrayList<>();

        for(int month = 0; month<12; month++){
            testData.add(new BarEntry(month,new Random().nextInt(20)));
        }

        return new BarDataSet(testData, "Test Data Set 3");
    }

}