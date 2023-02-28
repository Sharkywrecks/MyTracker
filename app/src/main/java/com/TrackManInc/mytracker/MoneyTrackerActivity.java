package com.TrackManInc.mytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.TrackManInc.mytracker.Model.FoodVsMoney;
import com.TrackManInc.mytracker.Model.Money;
import com.TrackManInc.mytracker.Prevalent.Prevalent;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class MoneyTrackerActivity extends AppCompatActivity {

    private float dayMoneyToday = 0;
    private TextView chartTitle, xAxisTitle;
    private RadioGroup radioGroup;
    private BarChart barChart;
    private int radioState = 0; // 0:week, 1:month, 2:year
    private ProgressDialog loadingBar;
    private Button addMoneyButton;
    private double[] weekAmountArray = new double[7];
    private double[] monthAmountArray = new double[28];
    private double[] yearAmountArray = new double[12];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_tracker);
        setupUIView();
        //TODO:Reset calendar date
    }

    private void setupUIView() {
        radioGroup = findViewById(R.id.radio_group);
        barChart = findViewById(R.id.bar_chart);
        chartTitle = findViewById(R.id.chart_title);
        xAxisTitle = findViewById(R.id.xAxis_title);
        loadingBar = new ProgressDialog(this);
        addMoneyButton=findViewById(R.id.add_money_btn);
        graphSettings();
        generateGraph(R.id.prev_week_radio_btn); // defaults to week graph
        addMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMoney();
            }
        });
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
        int radioBtnId = radioGroup.getCheckedRadioButtonId();
        generateGraph(radioBtnId);
    }

    private void generateGraph(int id) {
        if(id == R.id.prev_week_radio_btn){
            if(radioState == 0 && !barChart.isEmpty()){
                return;
            }
            radioState = 0;
            BarDataSet weekData = findWeekData();
            addDataToGraph(weekData);
        }else if(id == R.id.prev_month_radio_btn){
            if(radioState == 1 && !barChart.isEmpty()){
                return;
            }
            radioState = 1;
            BarDataSet monthData = findMonthData();
            addDataToGraph(monthData);
        }else if(id == R.id.prev_year_radio_btn){
            if(radioState == 2 && !barChart.isEmpty()){
                return;
            }
            radioState = 2;
            BarDataSet yearData = findYearData();
            addDataToGraph(yearData);
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
    private String calenderDate(Calendar calendar){
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        month = month+1;
        String d = String.valueOf(day);
        String m = String.valueOf(month);
        if(day<10){
            d = "0"+day;
        }
        if(month<10){
            m = "0"+month;
        }
        return year+"/"+m+"/"+d;
    }
    private void addMoney(){

        EditText moneyEnteredET = findViewById(R.id.money_entered);
        if(checkNoInput("Money used today.", moneyEnteredET)){
            return;
        }
        Calendar.getInstance().clear();
        Calendar calender = Calendar.getInstance();
        String dateHtml = calenderDate(calender);
        loadingBar.setTitle("Adding money");
        loadingBar.setMessage("Please wait. We are adding for "+dateHtml);
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        retrieveDaysMoney(dateHtml);
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference MoneyRef =  RootRef.child("User Money").child(Prevalent.currentOnlineUser.getEmail()).child(dateHtml);
        MoneyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totalMoney = Float.parseFloat(moneyEnteredET.getText().toString());
                HashMap<String,Object> userDataMap = new HashMap<>();
                totalMoney +=dayMoneyToday;
                userDataMap.put("amount",""+totalMoney);
                MoneyRef.updateChildren(userDataMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    toastMessage("Money added.");
                                    loadingBar.dismiss();
                                    finish();
                                }else{
                                    toastMessage("Network Error: Please try again after some time...");
                                    loadingBar.dismiss();
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void retrieveDaysMoney(String formattedDate) {
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference MoneyRef = RootRef.child("User Money").child(Prevalent.currentOnlineUser.getEmail()).child(formattedDate);
        MoneyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    dayMoneyToday = Float.parseFloat(snapshot.getValue(Money.class).getAmount());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void weekDataFromDB(){
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("User Money").child(Prevalent.currentOnlineUser.getEmail()).exists()) {
                    weekAmountArray = new double[7];
                    Calendar.getInstance().clear();
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                    String formattedDate;
                    String dayMoney;
                    for(int count = 6;count>=0;count--){
                        formattedDate = df.format(cal.getTime());
                        retrieveDaysMoney(formattedDate);

                        weekAmountArray[count] = dayMoneyToday;
                        cal.add(Calendar.DAY_OF_MONTH,-1);
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void monthDataFromDB(String date){
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("User Money").child(Prevalent.currentOnlineUser.getEmail()).exists()) {
                    monthAmountArray = new double[28];
                    Calendar.getInstance().clear();
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                    String formattedDate;
                    String dayMoney = "";
                    for(int count = 0;count<28;count++){
                        formattedDate = df.format(cal.getTime());
                        retrieveDaysMoney(formattedDate);
                        monthAmountArray[count] = Double.parseDouble(dayMoney);
                        cal.add(Calendar.DAY_OF_MONTH,-1);
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void yearDataFromDB(String date){
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("User Money").child(Prevalent.currentOnlineUser.getEmail()).exists()) {
                    yearAmountArray = new double[12];
                    Calendar.getInstance().clear();
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                    String formattedDate;
                    String dayMoney = "";
                    for(int month = 0;month<12;month++){
                        double monthSum = 0;
                        while(month == Calendar.MONTH){
                            formattedDate = df.format(cal.getTime());
                            retrieveDaysMoney(formattedDate);
                            monthSum += dayMoneyToday;
                            cal.add(Calendar.DAY_OF_MONTH,-1);
                        }
                        monthAmountArray[month] = monthSum;
                        cal.add(Calendar.DAY_OF_MONTH,-1);
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean checkNoInput(String money, EditText editText) {
        if(editText.getText().toString().equals("")){
            toastMessage("Enter a value for "+money);
            return true;
        }
        return false;
    }

    private void toastMessage(String message){
        Spannable centeredText = new SpannableString(message);
        centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),0,message.length()-1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        Toast.makeText(getApplicationContext(), centeredText, Toast.LENGTH_SHORT).show();
    }

    private BarDataSet findWeekData(){ // previous 7 days
        weekDataFromDB();
        final String[] daysOfWeek = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        final int length = daysOfWeek.length;
        Calendar.getInstance().clear();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        String[] shiftedDays = rightCircularShift(daysOfWeek, day);
        ArrayList<BarEntry> testData = new ArrayList<>();
        for(int dayOfWeek = 0; dayOfWeek<length; dayOfWeek++){
            testData.add(new BarEntry(dayOfWeek, (int)weekAmountArray[dayOfWeek])); // add db data here
        }
        barChart.getXAxis().setLabelCount(shiftedDays.length);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(shiftedDays));
        chartTitle.setText(R.string.weekChartTitle);
        xAxisTitle.setText(R.string.weekChartXAxisTitle);
        return new BarDataSet(testData, "Test Data Set 1");
    }

    private BarDataSet findMonthData() { // Previous 4 weeks
        String[] weekStartDates = new String[4];
        Calendar.getInstance().clear();
        Calendar cal = Calendar.getInstance();
        //https://stackoverflow.com/questions/10465487/get-next-week-and-previous-week-staring-and-ending-dates-in-java
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM", Locale.ENGLISH);
        for(int i=3;i>=0;i--){
            cal.add(Calendar.DATE, -7);
            Date weekStartDate = cal.getTime();
            weekStartDates[i] = formatter.format(weekStartDate);
        }
        int[] weeksTotal = new int[4];
        for(int week = 0; week<4;week++){
            int weekSum = 0;
            for(int day = week*7; day<(week*7)+7; day++) {
                weekSum+=monthAmountArray[day];
            }
            weeksTotal[week] = weekSum;
        }
        ArrayList<BarEntry> testData = new ArrayList<>();
        for(int week = 0; week<4; week++){
            testData.add(new BarEntry(week, weeksTotal[week])); // add db data here
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
        Calendar.getInstance().clear();
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