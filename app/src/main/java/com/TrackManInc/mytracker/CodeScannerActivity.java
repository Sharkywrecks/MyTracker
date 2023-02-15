package com.TrackManInc.mytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ErrorCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class CodeScannerActivity extends AppCompatActivity {


    private String scannedCodeValue = null;
    private String previousScannedCodeValue = "none";
    private static final int CAMERA_REQUEST_CODE = 101;
    private CodeScanner mCodeScanner;
    private org.jsoup.nodes.Document document = null;

    EditText expiryDateText,foodNameEditText;
    DatePickerDialog.OnDateSetListener setListener;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_scanner);

        initDatePicker();
        setupPermissionsCamera();
        setupCodeScanner();
        checkForEditNote();
    }
    private void checkForEditNote() {
        //Option to edit data
    }
    public void saveInput(View view){
        //Save to database
        finish();
    }

    public void deleteInput(View view){
        //Delete from database
        finish();
    }
    private void initDatePicker(){
        Calendar calender = Calendar.getInstance();
        final int year = calender.get(Calendar.YEAR);
        final int month = calender.get(Calendar.MONTH);
        final int day = calender.get(Calendar.DAY_OF_MONTH);

        expiryDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CodeScannerActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,setListener,
                        year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
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
                expiryDateText.setText(date);
            }
        };
    }
    private void setupCodeScanner(){
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setCamera(mCodeScanner.CAMERA_BACK);
        mCodeScanner.setFormats(mCodeScanner.ALL_FORMATS);
        mCodeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        mCodeScanner.setScanMode(ScanMode.CONTINUOUS);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setFlashEnabled(false);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scannedCodeValue = result.getText();
                        if(!Objects.equals(scannedCodeValue, previousScannedCodeValue)){
                            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.VISIBLE);
                            TextView tvtextView = (TextView)findViewById(R.id.tv_textView);
                            tvtextView.setText(scannedCodeValue);
                            description_webscrape dw = new description_webscrape();
                            dw.execute();
                        }
                        previousScannedCodeValue = scannedCodeValue;
                    }
                });
            }
        });
        mCodeScanner.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(@NonNull Throwable thrown) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Main","Camera initialization error: "+thrown.getMessage());
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
    private void setupPermissionsCamera(){
        int permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA);

        if(permission!= PackageManager.PERMISSION_GRANTED){
            makeRequest();
        }
    }
    private void makeRequest(){
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You need the camera permission to be able to use the scanner", Toast.LENGTH_SHORT);
                } else {
                    //successful
                }
        }
    }
    private void toastFailMessage(){
        if(foodNameEditText.getText().toString().equals("") || document==null) {
            String text = "Could not find scanned code. Check connection to internet";
            Spannable centeredText = new SpannableString(text);
            centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),0,text.length()-1,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            Toast.makeText(CodeScannerActivity.this, centeredText, Toast.LENGTH_SHORT).show();
        }
    }
    private class description_webscrape extends AsyncTask<Void,Void,Void>{

        String foodName;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //org.jsoup.nodes.Document document = null;

            try {
                document = Jsoup.connect(("https://world.openfoodfacts.org/product/"+scannedCodeValue)).get();
                if(document!=null) {
                    Element elements = document.getElementsByTag("h1").first();
                    //org.jsoup.select.Elements elements = document.getElementsByTag("<b>");
                    foodName = elements.text();

                } else{
                    document = Jsoup.connect(("https://upcitemdb.com/upc/" + scannedCodeValue)).get();
                    if(document!=null) {
                        //org.jsoup.select.Elements elements = document.getElementsByClass("detailtitle");
                        Element elements = document.getElementsByTag("span").first();
                        foodName = elements.text();

                    }
                }
            } catch (IOException e) {
                //failed
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            //if(!foodNameEditText.getText().toString().equals(foodName)) {
            foodNameEditText.setText(foodName);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);
            //}
            if(foodNameEditText.getText().toString().equals("")){
                toastFailMessage();
            }
        }
    }
}