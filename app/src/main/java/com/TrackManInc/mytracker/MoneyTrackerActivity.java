package com.TrackManInc.mytracker;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MoneyTrackerActivity extends AppCompatActivity {

    private TextView chartTitle, xAxisTitle;
    private RadioButton weekRadioButton, monthRadioButton, yearRadioButton;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_tracker);
        setupUIView();
    }

    private void setupUIView() {
        weekRadioButton = findViewById(R.id.prev_week_radio_btn);
        monthRadioButton = findViewById(R.id.prev_month_radio_btn);
        yearRadioButton = findViewById(R.id.prev_year_radio_btn);
        barChart = findViewById(R.id.bar_chart);
        chartTitle = findViewById(R.id.chart_title);
        xAxisTitle = findViewById(R.id.xAxis_title);
        graphSettings();
        weekRadioButton.setChecked(true);
        generateGraph(R.id.prev_week_radio_btn); // defaults to week graph
    }

    private void graphSettings(){  // temporary settings for now
        barChart.setNoDataText("No Data.");
        barChart.setNoDataTextColor(Color.BLACK);
        barChart.setBorderColor(Color.BLACK);
        barChart.setDrawBorders(true);
        barChart.setBorderWidth(3);
        barChart.setDescription(null);
        barChart.getLegend().setEnabled(false);

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
    }

    public void onRadioButtonClicked(View view) {
        generateGraph(view.getId());
    }

    private void generateGraph(int id) {
        switch (id){
            case R.id.prev_week_radio_btn:
                monthRadioButton.setChecked(false);
                yearRadioButton.setChecked(false);
                BarDataSet weekData = findWeekData();
                addDataToGraph(weekData);
                break;
            case R.id.prev_month_radio_btn:
                weekRadioButton.setChecked(false);
                yearRadioButton.setChecked(false);
                BarDataSet monthData = findMonthData();
                addDataToGraph(monthData);
                break;
            case R.id.prev_year_radio_btn:
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
        barChart.setData(barData);
        barChart.invalidate();
        barChart.animateXY(500, 900, Easing.Linear, Easing.EaseOutCubic);
    }

    private String[] rightCircularShift(String[] arr, int startIdx){
        int length = arr.length;
        if (startIdx == 0) {
            return arr;
        }
        String[] firstPart = Arrays.copyOfRange(arr, 0, startIdx+1);
        String[] secondPart = Arrays.copyOfRange(arr, startIdx+1, length);
        String[] shiftedArr = new String[length];
        System.arraycopy(secondPart, 0, shiftedArr, 0, secondPart.length);
        System.arraycopy(firstPart, 0, shiftedArr, secondPart.length, firstPart.length);
        return shiftedArr;
    }

    private BarDataSet findWeekData(){ // previous 7 days
        final String[] daysOfWeek = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        final int length = daysOfWeek.length;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        String[] shiftedDays = rightCircularShift(daysOfWeek, day);
        ArrayList<BarEntry> testData = new ArrayList<>();
        for(int dayOfWeek = 0; dayOfWeek<length; dayOfWeek++){
            testData.add(new BarEntry(dayOfWeek, new Random().nextInt(20))); // add db data here
        }
        barChart.getXAxis().setLabelCount(shiftedDays.length);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(shiftedDays));
        chartTitle.setText(R.string.weekChartTitle);
        xAxisTitle.setText(R.string.weekChartXAxisTitle);
        return new BarDataSet(testData, "Test Data Set 1");
    }

    private BarDataSet findMonthData() { // Previous 4 weeks
        String[] weekStartDates = new String[4];
        Calendar cal = Calendar.getInstance();
        //https://stackoverflow.com/questions/10465487/get-next-week-and-previous-week-staring-and-ending-dates-in-java
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM", Locale.ENGLISH);
        for(int i=3;i>=0;i--){
            cal.add(Calendar.DATE, -7);
            Date weekStartDate = cal.getTime();
            weekStartDates[i] = formatter.format(weekStartDate);
        }
        ArrayList<BarEntry> testData = new ArrayList<>();
        for(int week = 0; week<4; week++){
            testData.add(new BarEntry(week, new Random().nextInt(20))); // add db data here
        }
        barChart.getXAxis().setLabelCount(weekStartDates.length);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(weekStartDates));
        chartTitle.setText(R.string.monthChartTitle);
        xAxisTitle.setText(R.string.monthChartXAxisTitle);
        return new BarDataSet(testData, "Test Data Set 2");
    }

    private BarDataSet findYearData(){ // previous 12 months
        final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        final int length = months.length;
        Calendar calendar = Calendar.getInstance();
        int monthNum = calendar.get(Calendar.MONTH);
        String[] shiftedMonths = rightCircularShift(months, monthNum);
        ArrayList<BarEntry> testData = new ArrayList<>();
        for(int month = 0; month<length; month++){
            testData.add(new BarEntry(month, new Random().nextInt(20))); // add db data here
        }
        barChart.getXAxis().setLabelCount(shiftedMonths.length);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(shiftedMonths));
        chartTitle.setText(R.string.yearChartTitle);
        xAxisTitle.setText(R.string.yearChartXAxisTitle);
        return new BarDataSet(testData, "Test Data Set 3");
    }

}