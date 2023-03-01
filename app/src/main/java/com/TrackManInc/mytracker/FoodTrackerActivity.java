package com.TrackManInc.mytracker;

import static android.graphics.Color.RED;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.TrackManInc.mytracker.Model.Nutrients;
import com.TrackManInc.mytracker.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FoodTrackerActivity extends AppCompatActivity {

    private ProgressBar calorieBar, proteinBar, carbsBar, fibreBar, saltBar, fatBar;
    private TextView calorieProgress, proteinProgress, carbsProgress, fibreProgress, saltProgress, fatProgress;
    private int calorieVal=0;
    private int proteinVal=0;
    private int carbsVal=0;
    private int fibreVal=0;
    private int saltVal=0;
    private int fatVal=0;
    private int calorieTarget, proteinTarget, carbsTarget, fibreTarget, saltTarget, fatTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_tracker);

        //temp values//
        calorieTarget = 2500;
        proteinTarget = 55;
        carbsTarget = 333;
        fibreTarget = 30;
        saltTarget = 6;
        fatTarget = 97;

        setUpUIView();
        retrieveNutrients(getIntent().getStringExtra("DATE"));
        setupProgressBars();
    }

    private void setupProgressBars() {
        calorieBar.setMax(calorieTarget);
        proteinBar.setMax(proteinTarget);
        carbsBar.setMax(carbsTarget);
        fibreBar.setMax(fibreTarget);
        saltBar.setMax(saltTarget);
        fatBar.setMax(fatTarget);

        calorieBar.setProgress(calorieVal);
        proteinBar.setProgress(proteinVal);
        carbsBar.setProgress(carbsVal);
        fibreBar.setProgress(fibreVal);
        saltBar.setProgress(saltVal);
        fatBar.setProgress(fatVal);

        calorieProgress.setText(calorieVal + "/" + calorieTarget + "kcal");
        proteinProgress.setText(proteinVal + "/" + proteinTarget + "g");
        carbsProgress.setText(carbsVal + "/" + carbsTarget + "g");
        fibreProgress.setText(fibreVal + "/" + fibreTarget + "g");
        saltProgress.setText(saltVal + "/" + saltTarget + "g");
        fatProgress.setText(fatVal + "/" + fatTarget + "g");

        if (calorieVal >= calorieTarget) {
            calorieProgress.setTextColor(RED);
        }
        if (proteinVal >= proteinTarget) {
            proteinProgress.setTextColor(RED);
        }
        if (carbsVal >= carbsTarget) {
            carbsProgress.setTextColor(RED);
        }
        if (fibreVal >= fibreTarget) {
            fibreProgress.setTextColor(RED);
        }
        if (saltVal >= saltTarget) {
            saltProgress.setTextColor(RED);
        }
        if (fatVal >= fatTarget) {
            fatProgress.setTextColor(RED);
        }
    }

    private void setUpUIView(){
        calorieBar = findViewById(R.id.calorieBar);
        proteinBar = findViewById(R.id.proteinBar);
        carbsBar = findViewById(R.id.carbsBar);
        fatBar = findViewById(R.id.fatBar);
        fibreBar = findViewById(R.id.fibreBar);
        saltBar = findViewById(R.id.saltBar);
        calorieProgress = findViewById(R.id.calorieProgress);
        proteinProgress = findViewById(R.id.proteinProgress);
        carbsProgress = findViewById(R.id.carbsProgress);
        fatProgress = findViewById(R.id.fatProgress);
        fibreProgress = findViewById(R.id.fibreProgress);
        saltProgress = findViewById(R.id.saltProgress);


    }

    private void retrieveNutrients(String formattedDate) {
        final String[] totalMoney = {""};
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference nutrientRef = RootRef.child("User Foods").child(Prevalent.currentOnlineUser.getEmail()).child(formattedDate);
        nutrientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot nutrientDS:snapshot.getChildren()){
                    Nutrients usersNutrients = nutrientDS.getValue(Nutrients.class);
                    String carbs,protein,fat,fibre,salt;
                    carbs = checkRetrievedValue(usersNutrients.getCarbs());
                    protein = checkRetrievedValue(usersNutrients.getProtein());
                    fat = checkRetrievedValue(usersNutrients.getFat());
                    fibre = checkRetrievedValue(usersNutrients.getFibre());
                    salt = checkRetrievedValue(usersNutrients.getSalt());
                    carbsVal+=Integer.parseInt(carbs);
                    proteinVal += Integer.parseInt(protein);
                    fatVal += Integer.parseInt(fat);
                    saltVal += Integer.parseInt(salt);
                    fibreVal += Integer.parseInt(fibre);
                    calorieVal = 4*proteinVal + 4*carbsVal + 9*fatVal;
                    setupProgressBars();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String checkRetrievedValue(String data) {
        if(data==null){
            data = "0";
        }
        if(data.equals("")||data.equals("?")){
            data = "0";
        }
        return data;
    }
}