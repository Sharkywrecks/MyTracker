package com.TrackManInc.mytracker;

import com.TrackManInc.mytracker.Model.Nutrients;
import com.TrackManInc.mytracker.Prevalent.Prevalent;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChangeFoodDataActivity extends AppCompatActivity {
    private String dateHtml,newDateHtml = null, foodName,date;
    private EditText carbAmountET, proteinAmountET, fatsAmountET, saltAmountET, fiberAmountET, foodNameET, dateET, quantityAmountET,servingSizeET;
    private DatePickerDialog.OnDateSetListener setListener;
    private Button deleteButton,saveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_food_data);
        setUpUIView();
        foodName = getIntent().getStringExtra("FOOD_NAME");
        dateHtml = getIntent().getStringExtra("DATE");
        initDatePicker();
        getData(dateHtml);
    }

    private void setUpUIView() {
        foodNameET = findViewById(R.id.food_name);
        foodNameET.setFocusable(false);
        foodNameET.setKeyListener(null);
        dateET = findViewById(R.id.editDate);
        dateET.setFocusable(false);
        dateET.setKeyListener(null);
        carbAmountET = findViewById(R.id.editCarbs);
        proteinAmountET = findViewById(R.id.editProtein);
        fatsAmountET = findViewById(R.id.editFats);
        saltAmountET = findViewById(R.id.editSalt);
        fiberAmountET = findViewById(R.id.editFiber);
        quantityAmountET = findViewById(R.id.editQuantity);
        servingSizeET = findViewById(R.id.editServingSize);
        saveButton = findViewById(R.id.save_input_button);
        deleteButton = findViewById(R.id.delete_input_button);
    }
    private void initDatePicker(){
        final int year = Integer.parseInt(dateHtml.substring(0,4));
        final int month = Integer.parseInt(dateHtml.substring(5,7));
        final int day = Integer.parseInt(dateHtml.substring(8));
        setListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String day = String.valueOf(dayOfMonth);
                String m = String.valueOf(month);
                if(dayOfMonth<10){
                    day = "0"+dayOfMonth;
                }
                if(month<10){
                    m = "0"+month;
                }
                date = day+"/"+m+"/"+year;
                newDateHtml = year+"/"+m+"/"+day;
                dateET.setText(date);
            }
        };
        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(ChangeFoodDataActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,setListener,
                        year,month,day);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteInput();}
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInput();}
        });
    }
    private boolean checkNoInput(String nutrient,EditText editText) {
        if(editText.getText().toString().equals("") || editText.getText().toString().equals("?")
                || editText.getText().toString().equals("g")|| editText.getText().toString().equals("?g")){
            toastMessage("Enter a value for "+nutrient);
            return true;
        }
        return false;
    }

    public void saveInput(){
        if(checkNoInput("Carbohydrates",carbAmountET)|| checkNoInput("Protein",proteinAmountET)||
                checkNoInput("Fats",fatsAmountET)|| checkNoInput("Salt",saltAmountET)||
                checkNoInput("Fiber",fiberAmountET)|| checkNoInput("Date",dateET)||
                checkNoInput("Quantity",quantityAmountET)|| checkNoInput("Serving size",servingSizeET)){
            return;
        }
        if(newDateHtml!=null){
            deleteInput();
        }else{
            newDateHtml = dateHtml;
        }
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> userDataMap = new HashMap<>();
        userDataMap.put("carbs",carbAmountET.getText().toString());
        userDataMap.put("protein",proteinAmountET.getText().toString());
        userDataMap.put("fat",fatsAmountET.getText().toString());
        userDataMap.put("salt",saltAmountET.getText().toString());
        userDataMap.put("fiber",fiberAmountET.getText().toString());
        userDataMap.put("serving",servingSizeET.getText().toString());
        userDataMap.put("quantity",quantityAmountET.getText().toString());

        RootRef.child("User Foods").child(Prevalent.currentOnlineUser.getEmail()).child(newDateHtml).child(foodName).updateChildren(userDataMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            toastMessage("Food item changed.");
                            finish();
                        }else{
                            toastMessage("Network Error: Please try again after some time...");
                        }
                    }
                });
        finish();
    }

    private void toastMessage(String message){
        Spannable centeredText = new SpannableString(message);
        centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),0,message.length()-1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        Toast.makeText(getApplicationContext(), centeredText, Toast.LENGTH_SHORT).show();
    }
    private void deleteInput(){
        //Delete from database
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("User Foods").child(Prevalent.currentOnlineUser.getEmail()).child(dateHtml).child(foodName).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    toastMessage("Food item deleted.");
                    finish();
                }else{
                    toastMessage("Network Error: Please try again after some time...");
                }
            }
        });
    }
    private void getData(String formattedDate) {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference foodRef = rootRef.child("User Foods").child(Prevalent.currentOnlineUser.getEmail()).child(formattedDate).child(foodName);
            foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Nutrients foodNutrient = snapshot.getValue(Nutrients.class);
                    if(foodNutrient!=null) {
                        foodNameET.setText(foodName);
                        if(dateET.getText().toString().equals("")){
                            dateET.setText(dateHtml.substring(8)+"/"+dateHtml.substring(5,7)+"/"+dateHtml.substring(0,4));
                        }
                        carbAmountET.setText(checkRetrievedValue(foodNutrient.getCarbs()));
                        proteinAmountET.setText(checkRetrievedValue(foodNutrient.getProtein()));
                        fatsAmountET.setText(checkRetrievedValue(foodNutrient.getFat()));
                        fiberAmountET.setText(checkRetrievedValue(foodNutrient.getFibre()));
                        saltAmountET.setText(checkRetrievedValue(foodNutrient.getSalt()));
                        quantityAmountET.setText(checkRetrievedValue(foodNutrient.getAmount()));
                        servingSizeET.setText(checkRetrievedValue(foodNutrient.getServingSize()));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ChangeFoodDataActivity.this, "Failed to read data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private String checkRetrievedValue(String data) {
        if (data == null) {
            data = "0";
        }
        if (data.equals("") || data.equals("?")) {
            data = "0";
        }
        return data;
    }
}

