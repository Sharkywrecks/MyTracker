package com.TrackManInc.mytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.TrackManInc.mytracker.Model.FoodVsMoney;
import com.TrackManInc.mytracker.Model.Money;
import com.TrackManInc.mytracker.Model.Users;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
public class MoneyTrackerActivity extends AppCompatActivity {
    private TextView chartTitle, xAxisTitle;
    private EditText moneyEnteredET, dateET;
    private String dateHtml;
    private RadioGroup radioGroup;
    private BarChart barChart;
    private int radioState = 0; // 0:week, 1:month, 2:year
    //private ProgressDialog loadingBar;
    private DatePickerDialog.OnDateSetListener setListener;
    private Button addMoneyButton;
    private ArrayList<Double> weekAmountArray = new ArrayList<>();
    private ArrayList<Double> monthAmountArray = new ArrayList<>();
    private ArrayList<Double> monthAmountWeekArray = new ArrayList<>();
    private ArrayList<Double> yearAmountArray = new ArrayList<>();
    private ArrayList<Double> yearAmountMonthArray = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_money_tracker);
        setupUIView();
    }

    private void setupUIView() {
        dateET = findViewById(R.id.date);
        dateET.setFocusable(false);
        dateET.setKeyListener(null);
        moneyEnteredET = findViewById(R.id.money_entered);
        radioGroup = findViewById(R.id.radio_group);
        barChart = findViewById(R.id.bar_chart);
        chartTitle = findViewById(R.id.chart_title);
        xAxisTitle = findViewById(R.id.xAxis_title);
        //loadingBar = new ProgressDialog(this);
        addMoneyButton=findViewById(R.id.add_money_btn);
        graphSettings();
        initDatePicker();
        generateGraph(R.id.prev_week_radio_btn); // defaults to week graph
        addMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMoney();
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
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        generateGraph(radioBtnId);
    }


    private void initDatePicker(){
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        try {
            calender.setTime(Objects.requireNonNull(sdf.parse(getIntent().getStringExtra("DATE"))));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        final int year = calender.get(Calendar.YEAR);
        final int month = calender.get(Calendar.MONTH) + 1;
        final int day = calender.get(Calendar.DAY_OF_MONTH);

        String date;
        String dayStr = String.valueOf(day);
        String monthStr = String.valueOf(month);
        if(day<10){
            dayStr = "0"+day;
        }
        if(month<10){
            monthStr = "0"+month;
        }
        date = dayStr+"/"+monthStr+"/"+year;
        dateHtml = year+"/"+monthStr+"/"+dayStr;
        dateET.setText(date);
        setListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String date;
                String day = String.valueOf(dayOfMonth);
                String m = String.valueOf(month);
                if(dayOfMonth<10){
                    day = "0"+dayOfMonth;
                }
                if(month<10){
                    m = "0"+month;
                }
                date = day+"/"+m+"/"+year;
                dateHtml = year+"/"+m+"/"+day;
                dateET.setText(date);
            }
        };
        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(MoneyTrackerActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,setListener,
                        year,month-1,day);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
    }

    private void generateGraph(int id) {
        if(id == R.id.prev_week_radio_btn){
            if(radioState == 0 && !barChart.isEmpty()){
                return;
            }
            radioState = 0;
            weekDataFromDB();
        }else if(id == R.id.prev_month_radio_btn){
            if(radioState == 1 && !barChart.isEmpty()){
                return;
            }
            radioState = 1;
            monthDataFromDB();
        }else if(id == R.id.prev_year_radio_btn){
            if(radioState == 2 && !barChart.isEmpty()){
                return;
            }
            radioState = 2;
            yearDataFromDB();
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
    private void getMoney(){
        if(checkNoInput("Money used today.", moneyEnteredET)){
            return;
        }
        Calendar.getInstance().clear();
        /*loadingBar.setTitle("Adding money");
        loadingBar.setMessage("Please wait. We are adding for "+dateHtml);
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();*/

        retrieveDaysMoney(dateHtml,5);
    }
    private void addMoney(Double dayMoney){
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference UserRef =  RootRef.child("Users").child(Prevalent.currentOnlineUser.getEmail());
        final DatabaseReference MoneyRef =  RootRef.child("User Money").child(Prevalent.currentOnlineUser.getEmail()).child(dateHtml);
        MoneyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalMoney = Double.parseDouble(moneyEnteredET.getText().toString());
                HashMap<String,Object> userDataMap = new HashMap<>();
                totalMoney +=dayMoney;
                userDataMap.put("amount",""+String.format("%.2f",totalMoney));
                MoneyRef.updateChildren(userDataMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    toastMessage("Money added.");
                                    //loadingBar.dismiss();
                                    finish();
                                }else{
                                    toastMessage("Network Error: Please try again after some time...");
                                    //loadingBar.dismiss();
                                }
                                generateGraph(radioState);
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                String amount = user.getLifetime_amount();
                double amountDbl = Double.parseDouble(amount);
                double totalMoney = Double.parseDouble(moneyEnteredET.getText().toString());
                double newAmount = amountDbl + totalMoney;
                HashMap<String,Object> userDataMap = new HashMap<>();
                userDataMap.put("lifetime_amount",""+ String.format("%.2f",newAmount));
                UserRef.updateChildren(userDataMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void retrieveDaysMoney(String formattedDate, int arrayNum) {
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference MoneyRef = RootRef.child("User Money").child(Prevalent.currentOnlineUser.getEmail()).child(formattedDate);
        MoneyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double moneyDouble=0.00;
                if (snapshot.exists()) {
                    Money money = snapshot.getValue(Money.class);
                    if(money!=null){
                        if(!money.getAmount().equals("")){
                            moneyDouble = Double.parseDouble(money.getAmount());
                        }
                    }
                }
                switch(arrayNum){
                    case 0:
                        weekAmountArray.add(moneyDouble);
                        BarDataSet weekDataset = findWeekData();
                        weekDataset.setGradientColor(Color.DKGRAY, Color.LTGRAY);
                        addDataToGraph(weekDataset);
                        break;
                    case 1:
                        monthAmountArray.add(moneyDouble);
                        BarDataSet monthDataset = findMonthData();
                        monthDataset.setGradientColor(Color.DKGRAY, Color.LTGRAY);
                        addDataToGraph(monthDataset);
                        break;
                    case 2:
                        yearAmountArray.add(moneyDouble);
                        BarDataSet yearDataset = findYearData();
                        yearDataset.setGradientColor(Color.DKGRAY, Color.LTGRAY);
                        addDataToGraph(yearDataset);
                        break;
                    case 5:
                        addMoney(moneyDouble);
                        break;
                }
                //onRadioButtonClicked(null);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void weekDataFromDB(){
        weekAmountArray = new ArrayList<>();
        Calendar.getInstance().clear();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate;
        for(int count = 0;count<7;count++){
            formattedDate = df.format(cal.getTime());
            retrieveDaysMoney(formattedDate,0);
            cal.add(Calendar.DAY_OF_MONTH,-1);
        }
    }

    private void monthDataFromDB(){
        monthAmountArray = new ArrayList<>();
        Calendar.getInstance().clear();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate;
        for(int count = 0;count<28;count++){
            formattedDate = df.format(cal.getTime());
            retrieveDaysMoney(formattedDate,1);
            cal.add(Calendar.DAY_OF_MONTH,-1);
        }
    }

    private void yearDataFromDB(){
        yearAmountArray = new ArrayList<>();
        Calendar.getInstance().clear();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate;
        for(int i = 0;i<365;i++){
            formattedDate = df.format(cal.getTime());
            retrieveDaysMoney(formattedDate,2);
            cal.add(Calendar.DAY_OF_MONTH,-1);
        }
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
        //weekDataFromDB();
        final String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        final int length = daysOfWeek.length;
        Calendar.getInstance().clear();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK)-2;
        String[] shiftedDays = rightCircularShift(daysOfWeek, day);
        ArrayList<BarEntry> testData = new ArrayList<>();
        if(weekAmountArray.size()==7){
            Collections.reverse(weekAmountArray);
            //Collections.rotate(weekAmountArray,day);
        }
        for(int dayOfWeek = 0; dayOfWeek<weekAmountArray.size(); dayOfWeek++){
            testData.add(new BarEntry(dayOfWeek, (float) Double.parseDouble(String.valueOf(weekAmountArray.get(dayOfWeek))))); // add db data here
        }
        barChart.getXAxis().setLabelCount(shiftedDays.length);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(shiftedDays));
        chartTitle.setText(R.string.weekChartTitle);
        xAxisTitle.setText(R.string.weekChartXAxisTitle);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        return new BarDataSet(testData, "Week Data");
    }

    private BarDataSet findMonthData() { // Previous 4 weeks
        String[] weekStartDates = {"Week 4","Week 3", "Week 2", "Current"};
        Calendar.getInstance().clear();
        Calendar cal = Calendar.getInstance();
        //https://stackoverflow.com/questions/10465487/get-next-week-and-previous-week-staring-and-ending-dates-in-java
        int weekDay = Calendar.DAY_OF_WEEK;
        if(monthAmountArray.size()== 28){
            for(int i =0;i<4;i++){
                Double sum = 0.00;
                for(int j =0;j<weekDay;j++){
                    sum+=monthAmountArray.remove(0);
                    cal.add(Calendar.DAY_OF_WEEK,-1);
                }
                monthAmountWeekArray.add(sum);
                weekDay = Calendar.DAY_OF_WEEK;
            }
            monthAmountArray.clear();
        }
        ArrayList<BarEntry> testData = new ArrayList<>();
        Collections.reverse(monthAmountWeekArray);
        for(int week = 0; week<monthAmountWeekArray.size(); week++){
            testData.add(new BarEntry(week, Float.parseFloat(monthAmountWeekArray.get(week).toString()))); // add db data here
        }
        monthAmountWeekArray.clear();
        barChart.getXAxis().setLabelCount(weekStartDates.length);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(weekStartDates));
        chartTitle.setText(R.string.monthChartTitle);
        xAxisTitle.setText(R.string.monthChartXAxisTitle);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        return new BarDataSet(testData, "Month Data");
    }

    private BarDataSet findYearData(){ // previous 12 months
        final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        Calendar.getInstance().clear();
        Calendar cal = Calendar.getInstance();
        int monthNum = cal.get(Calendar.MONTH);
        String[] shiftedMonths = rightCircularShift(months, monthNum);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        int monthDay = Integer.parseInt(df.format(cal.getTime()).substring(8,10));
        if(yearAmountArray.size()== 365){
            for(int i =0;i<12;i++){
                Double sum = 0.00;
                for(int j =0;j<monthDay;j++){
                    sum+=yearAmountArray.remove(0);
                    cal.add(Calendar.DATE,-1);
                }
                yearAmountMonthArray.add(sum);
                monthDay = Integer.parseInt(df.format(cal.getTime()).substring(8,10));
            }
            yearAmountArray.clear();
        }
        ArrayList<BarEntry> testData = new ArrayList<>();
        Collections.reverse(yearAmountMonthArray);
        for(int month = 0; month<yearAmountMonthArray.size(); month++){
            testData.add(new BarEntry(month,Float.parseFloat(yearAmountMonthArray.get(month).toString()) )); // add db data here
        }
        yearAmountMonthArray.clear();
        barChart.getXAxis().setLabelCount(shiftedMonths.length);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(shiftedMonths));
        chartTitle.setText(R.string.yearChartTitle);
        xAxisTitle.setText(R.string.yearChartXAxisTitle);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        return new BarDataSet(testData, "Year Data");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        finish();
    }
}