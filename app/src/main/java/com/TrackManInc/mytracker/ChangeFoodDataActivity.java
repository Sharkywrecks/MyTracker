package com.TrackManInc.mytracker;

import com.TrackManInc.mytracker.Model.Nutrients;
import com.TrackManInc.mytracker.Prevalent.Prevalent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.View;
import android.widget.ArrayAdapter;
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
import android.widget.Button;


public class ChangeFoodDataActivity extends AppCompatActivity {

    private String date, foodName;
    private EditText carbAmountET, proteinAmountET, fatsAmountET, saltAmountET, fiberAmountET, foodNameET, dateET, quantityAmountET;
    private Button deleteButton, saveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_food_data);
        setUpUIView();
        foodName = getIntent().getStringExtra("FOOD_NAME");
        date = getIntent().getStringExtra("DATE");
        getData(date);
    }

    private void setUpUIView() {
        foodNameET = findViewById(R.id.food_name);
        dateET = findViewById(R.id.date);
        carbAmountET = findViewById(R.id.editCarbs);
        proteinAmountET = findViewById(R.id.editProtein);
        fatsAmountET = findViewById(R.id.editFats);
        saltAmountET = findViewById(R.id.editSalt);
        fiberAmountET = findViewById(R.id.editFiber);
        quantityAmountET = findViewById(R.id.quantity);
        deleteButton = findViewById(R.id.delete_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            //delete Food Item from database
            @Override
            public void onClick(View view) {deleteInput();}
        });
        saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void deleteInput(){
        //Delete from database
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("User Foods").child(Prevalent.currentOnlineUser.getEmail()).child(date).child(foodName).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void toastMessage(String message){
        Spannable centeredText = new SpannableString(message);
        centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),0,message.length()-1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        Toast.makeText(getApplicationContext(), centeredText, Toast.LENGTH_SHORT).show();
    }

    private void getData(String formattedDate) {
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference foodRef = rootRef.child("User Foods").child(Prevalent.currentOnlineUser.getEmail()).child(formattedDate);
            foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Nutrients foodNutrient = ds.getValue(Nutrients.class);
                        foodNameET.setText(foodName);
                        dateET.setText(date);
                        carbAmountET.setText(checkRetrievedValue(foodNutrient.getCarbs()));
                        proteinAmountET.setText(checkRetrievedValue(foodNutrient.getProtein()));
                        fatsAmountET.setText(checkRetrievedValue(foodNutrient.getFat()));
                        fiberAmountET.setText(checkRetrievedValue(foodNutrient.getFibre()));
                        saltAmountET.setText(checkRetrievedValue(foodNutrient.getSalt()));
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

