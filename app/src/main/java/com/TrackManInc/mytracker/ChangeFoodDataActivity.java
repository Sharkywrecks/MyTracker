package com.TrackManInc.mytracker;

import com.TrackManInc.mytracker.Filters.DecimalDigitsInputFilter;
import com.TrackManInc.mytracker.Model.Nutrients;
import com.TrackManInc.mytracker.Prevalent.Prevalent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ChangeFoodDataActivity extends AppCompatActivity {
    private String dateHtml,newDateHtml = null,date;
    private String foodName,carbAmount,proteinAmount,fatAmount,saltAmount,fiberAmount,quantity,servingSize;
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
        carbAmountET.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3)});
        proteinAmountET = findViewById(R.id.editProtein);
        proteinAmountET.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3)});
        fatsAmountET = findViewById(R.id.editFats);
        fatsAmountET.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3)});
        saltAmountET = findViewById(R.id.editSalt);
        saltAmountET.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3)});
        fiberAmountET = findViewById(R.id.editFiber);
        fiberAmountET.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3)});
        quantityAmountET = findViewById(R.id.editQuantity);
        quantityAmountET.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3)});
        servingSizeET = findViewById(R.id.editServingSize);
        servingSizeET.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3)});
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);
        setupETListeners();
    }
    private void setupETListeners() {
        foodNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                foodName = foodNameET.getText().toString();
            }
        });
        carbAmountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                StringBuilder temp = new StringBuilder(carbAmountET.getText().toString());
                char[] charTemp = temp.toString().toCharArray();
                temp = new StringBuilder();
                for (char c : charTemp) {
                    if (!Character.isDigit(c) && c!='.') {
                        break;
                    }
                    temp.append(c);
                }
                carbAmount = temp.toString();
            }
        });
        proteinAmountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                StringBuilder temp = new StringBuilder(proteinAmountET.getText().toString());
                char[] charTemp = temp.toString().toCharArray();
                temp = new StringBuilder();
                for (char c : charTemp) {
                    if (!Character.isDigit(c) && c!='.') {
                        break;
                    }
                    temp.append(c);
                }
                proteinAmount = temp.toString();
            }
        });
        fatsAmountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                StringBuilder temp = new StringBuilder(fatsAmountET.getText().toString());
                char[] charTemp = temp.toString().toCharArray();
                temp = new StringBuilder();
                for (char c : charTemp) {
                    if (!Character.isDigit(c) && c!='.') {
                        break;
                    }
                    temp.append(c);
                }
                fatAmount = temp.toString();
            }
        });
        saltAmountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                StringBuilder temp = new StringBuilder(saltAmountET.getText().toString());
                char[] charTemp = temp.toString().toCharArray();
                temp = new StringBuilder();
                for (char c : charTemp) {
                    if (!Character.isDigit(c) && c!='.') {
                        break;
                    }
                    temp.append(c);
                }
                saltAmount = temp.toString();
            }
        });
        fiberAmountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                StringBuilder temp = new StringBuilder(fiberAmountET.getText().toString());
                char[] charTemp = temp.toString().toCharArray();
                temp = new StringBuilder();
                for (char c : charTemp) {
                    if (!Character.isDigit(c) && c!='.') {
                        break;
                    }
                    temp.append(c);
                }
                fiberAmount = temp.toString();
            }
        });
        servingSizeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                StringBuilder temp = new StringBuilder(servingSizeET.getText().toString());
                char[] charTemp = temp.toString().toCharArray();
                temp = new StringBuilder();
                for (char c : charTemp) {
                    if (!Character.isDigit(c) && c!='.') {
                        break;
                    }
                    temp.append(c);
                }
                servingSize = temp.toString();
            }
        });
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(ChangeFoodDataActivity.this, R.style.DialogTheme,setListener,
                        year,month,day);
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
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

    private void saveInput(){
        if(checkNoInput("Carbohydrates",carbAmountET)|| checkNoInput("Protein",proteinAmountET)||
                checkNoInput("Fats",fatsAmountET)|| checkNoInput("Salt",saltAmountET)||
                checkNoInput("Fiber",fiberAmountET)|| checkNoInput("Date",dateET)||
                checkNoInput("Quantity",quantityAmountET)|| checkNoInput("Serving size",servingSizeET)){
            return;
        }
        if(newDateHtml==null){
            newDateHtml = dateHtml;
        }
        deleteInput();
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> userDataMap = new HashMap<>();
        userDataMap.put("carbs",carbAmount);
        userDataMap.put("protein",proteinAmount);
        userDataMap.put("fat",fatAmount);
        userDataMap.put("salt",saltAmount);
        userDataMap.put("fiber",fiberAmount);
        userDataMap.put("serving",servingSize);
        quantity = quantityAmountET.getText().toString();
        userDataMap.put("quantity",quantity);

        RootRef.child("User Foods").child(Prevalent.currentOnlineUser.getName()).child(newDateHtml).child(foodName).updateChildren(userDataMap)
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
    private String removeUnwantedCharacters(String foodName) {
        String s = "";
        for (char c:foodName.toCharArray()){
            if(c=='.'||c=='#'||c=='$'||c=='['||c==']'){
                s+="_";
                continue;
            }
            s+=String.valueOf(c);
        }
        return s;
    }
    private void deleteInput(){
        //Delete from database
        foodName = removeUnwantedCharacters(foodName);
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("User Foods").child(Prevalent.currentOnlineUser.getName()).child(dateHtml).child(foodName).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
        final DatabaseReference foodRef = rootRef.child("User Foods").child(Prevalent.currentOnlineUser.getName()).child(formattedDate).child(foodName);
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
                        fiberAmountET.setText(checkRetrievedValue(foodNutrient.getFiber()));
                        saltAmountET.setText(checkRetrievedValue(foodNutrient.getSalt()));
                        quantityAmountET.setText(checkRetrievedValue(foodNutrient.getQuantity()));
                        servingSizeET.setText(checkRetrievedValue(foodNutrient.getServing()));
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

