package com.TrackManInc.mytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class MoneyTrackerActivity extends AppCompatActivity {

    private RadioButton weekRadioButton, monthRadioButton, yearRadioButton;
    private LineChart lineChart;

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
        lineChart = findViewById(R.id.line_chart);
        graphSettings();
        generateGraph(R.id.week_radio_btn); // defaults to week graph
    }

    private void graphSettings(){  // temporary settings for now
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setNoDataText("No Data.");
        lineChart.setNoDataTextColor(Color.BLACK);
        lineChart.setDrawGridBackground(true);
        lineChart.setDrawBorders(true);
        lineChart.setBorderColor(Color.BLACK);
        lineChart.setBorderWidth(3);

        Description chartDescription = new Description();
        chartDescription.setText("Money Tracker");
        chartDescription.setTextColor(Color.BLACK);
        chartDescription.setTextSize(20);
        lineChart.setDescription(chartDescription);
    }

    public void onRadioButtonClicked(View view) {
        generateGraph(view.getId());
    }

    private void generateGraph(int id) {
        switch (id){
            case R.id.week_radio_btn:
                monthRadioButton.setChecked(false);
                yearRadioButton.setChecked(false);
                LineDataSet weekData = findWeekData();
                addDataToGraph(weekData);
                break;
            case R.id.month_radio_btn:
                weekRadioButton.setChecked(false);
                yearRadioButton.setChecked(false);
                LineDataSet monthData = findMonthData();
                addDataToGraph(monthData);
                break;
            case R.id.year_radio_btn:
                weekRadioButton.setChecked(false);
                monthRadioButton.setChecked(false);
                LineDataSet yearData = findYearData();
                addDataToGraph(yearData);
                break;
        }
    }

    private void addDataToGraph(LineDataSet lineDataSet){
        ArrayList<ILineDataSet> data = new ArrayList<>();
        data.add(lineDataSet);
        LineData linedata = new LineData(data);
        lineChart.setData(linedata);
        lineChart.invalidate();
    }

    private LineDataSet findWeekData(){ // will eventually add real data from db instead
        ArrayList<Entry> testData = new ArrayList<>();
        testData.add(new Entry(1,2));
        testData.add(new Entry(2,3));
        testData.add(new Entry(3,4));
        testData.add(new Entry(4,24));
        testData.add(new Entry(5,6));
        testData.add(new Entry(6,7));
        testData.add(new Entry(7,8));
        LineDataSet lineDataSet = new LineDataSet(testData, "Test Data Set 1");
        lineDataSet.setLineWidth(4);
        return lineDataSet;
    }

    private LineDataSet findMonthData(){ // will eventually add real data from db instead
        ArrayList<Entry> testData = new ArrayList<>();
        testData.add(new Entry(1,2));
        testData.add(new Entry(2,3));
        testData.add(new Entry(3,4));
        testData.add(new Entry(4,5));
        testData.add(new Entry(5,16));
        testData.add(new Entry(6,7));
        testData.add(new Entry(7,8));
        LineDataSet lineDataSet = new LineDataSet(testData, "Test Data Set 2");
        lineDataSet.setLineWidth(4);
        return lineDataSet;
    }

    private LineDataSet findYearData(){ // will eventually add real data from db instead
        ArrayList<Entry> testData = new ArrayList<>();
        testData.add(new Entry(1,2));
        testData.add(new Entry(2,10));
        testData.add(new Entry(3,4));
        testData.add(new Entry(4,5));
        testData.add(new Entry(5,6));
        testData.add(new Entry(6,7));
        testData.add(new Entry(7,8));
        LineDataSet lineDataSet = new LineDataSet(testData, "Test Data Set 3");
        lineDataSet.setLineWidth(4);
        return lineDataSet;
    }

}