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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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
        weekRadioButton = findViewById(R.id.prev_week_radio_btn);
        monthRadioButton = findViewById(R.id.prev_month_radio_btn);
        yearRadioButton = findViewById(R.id.prev_year_radio_btn);
        barChart = findViewById(R.id.bar_chart);
        graphSettings();
        weekRadioButton.setChecked(true);
        generateGraph(R.id.prev_week_radio_btn); // defaults to week graph
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

    private void addChartDescription(String description){
        Description chartDescription = new Description();
        chartDescription.setText(description);
        chartDescription.setTextColor(Color.BLACK);
        chartDescription.setTextSize(20);
        chartDescription.setEnabled(true);
        barChart.setDescription(chartDescription);
    }


    private void addDataToGraph(BarDataSet barDataSet){
        BarData barData = new BarData();
        barData.addDataSet(barDataSet);
        barChart.setData(barData);
        barChart.invalidate();
        barChart.animateXY(500, 900, Easing.Linear, Easing.EaseOutCubic);
    }

    private BarDataSet findWeekData(){ // previous 7 days
        final String[] daysOfWeek = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        final int length = daysOfWeek.length;
        String[] shiftedDays = new String[length];
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        for(int i = 0; i<length-(day+1);i++){
            shiftedDays[i] = daysOfWeek[day+1+i];
        }
        int count = 0;
        for(int i = length-(day+1); i<length;i++){
            shiftedDays[i] = daysOfWeek[count];
            count++;
        }
        ArrayList<BarEntry> testData = new ArrayList<>();
        for(int dayOfWeek = 0; dayOfWeek<length; dayOfWeek++){
            testData.add(new BarEntry(dayOfWeek, new Random().nextInt(20)));
        }
        barChart.getXAxis().setLabelCount(shiftedDays.length);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(shiftedDays));
        addChartDescription("Previous 7 Days");
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
        for(int dayOfWeek = 0; dayOfWeek<4; dayOfWeek++){
            testData.add(new BarEntry(dayOfWeek, new Random().nextInt(20)));
        }
        barChart.getXAxis().setLabelCount(weekStartDates.length);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(weekStartDates));
        addChartDescription("Previous 4 Weeks");
        return new BarDataSet(testData, "Test Data Set 2");
    }

    private BarDataSet findYearData(){ // previous 12 months
        final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        final int length = months.length;
        String[] shiftedMonths = new String[length];
        Calendar calendar = Calendar.getInstance();
        int monthNum = calendar.get(Calendar.MONTH);
        for(int i = 0; i<length-(monthNum+1);i++){
            shiftedMonths[i] = months[monthNum+1+i];
        }
        int count = 0;
        for(int i = length-(monthNum+1); i<length;i++){
            shiftedMonths[i] = months[count];
            count++;
        }
        ArrayList<BarEntry> testData = new ArrayList<>();
        for(int month = 0; month<length; month++){
            testData.add(new BarEntry(month, new Random().nextInt(20)));
        }
        barChart.getXAxis().setLabelCount(shiftedMonths.length);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(shiftedMonths));
        addChartDescription("Previous 7 Days");
        addChartDescription("Previous 12 Months");
        return new BarDataSet(testData, "Test Data Set 3");
    }

}