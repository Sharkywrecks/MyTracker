package com.TrackManInc.mytracker;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.TrackManInc.mytracker.Model.Nutrients;
import com.TrackManInc.mytracker.Model.Users;
import com.TrackManInc.mytracker.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FoodTrackerActivity extends AppCompatActivity {

    private ProgressBar calorieBar, proteinBar, carbsBar, fibreBar, saltBar;
    private int calorieVal=0;
    private int proteinVal=0;
    private int carbsVal=0;
    private int fibreVal=0;
    private int saltVal=0;
    private int fatVal=0;
    private int calorieTarget, proteinTarget, carbsTarget, fibreTarget, saltTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_tracker);

        //temp values//
        calorieTarget = 2500;
        proteinTarget = 55;
        carbsTarget = 333;
        calorieVal = 1350;
        proteinVal = 20;
        carbsVal = 100;

        setUpUIView();
    }

    private void setUpUIView(){
        calorieBar = findViewById(R.id.calorieBar);
        proteinBar = findViewById(R.id.proteinBar);
        carbsBar = findViewById(R.id.carbsBar);

        calorieBar.setMax(calorieTarget);
        proteinBar.setMax(proteinTarget);
        carbsBar.setMax(carbsTarget);

        calorieBar.setProgress(calorieVal);
        proteinBar.setProgress(proteinVal);
        carbsBar.setProgress(carbsVal);

    }

    private String retrieveNutrients(String formattedDate) {
        final String[] totalMoney = {""};
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference nutrientRef = RootRef.child("User Foods").child(Prevalent.currentOnlineUser.getEmail()).child(formattedDate);
        nutrientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot nutrientDS:snapshot.getChildren()){
                    Nutrients usersNutrients = nutrientDS.getValue(Nutrients.class);
                    carbsVal+=Integer.parseInt(usersNutrients.getCarbohydrates());
                    proteinVal += Integer.parseInt(usersNutrients.getProtein());
                    fatVal += Integer.parseInt(usersNutrients.getFat());
                    saltVal += Integer.parseInt(usersNutrients.getSalt());
                    fibreVal += Integer.parseInt(usersNutrients.getFibre());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        })
    }

}