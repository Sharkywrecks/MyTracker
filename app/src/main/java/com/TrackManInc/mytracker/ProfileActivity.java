package com.TrackManInc.mytracker;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.TrackManInc.mytracker.Model.Money;
import com.TrackManInc.mytracker.Model.Nutrients;
import com.TrackManInc.mytracker.Model.Users;
import com.TrackManInc.mytracker.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private ImageView profileImageView;
    private TextView usernameTextView, emailTextView, lifetimeAmountTextView, todayAmountTextView;
    private TextView calorieTextView, fatTextView, carbsTextView, proteinTextView, fibreTextView, saltTextView;
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
        setContentView(R.layout.activity_profile);

        //temp values//
        calorieTarget = 2500;
        proteinTarget = 55;
        carbsTarget = 333;
        fibreTarget = 30;
        saltTarget = 6;
        fatTarget = 97;

        setupUI();
    }

    private void setupUI(){
        profileImageView = findViewById(R.id.profile_image_view);
        usernameTextView = findViewById(R.id.username_text_view);
        emailTextView = findViewById(R.id.email_text_view);
        lifetimeAmountTextView = findViewById(R.id.lifetime_amount_tv);
        todayAmountTextView = findViewById(R.id.today_amount_tv);
        ImageButton settingsButton = findViewById(R.id.settings_button);
        ImageButton moneyButton = findViewById(R.id.money_button);
        ImageButton foodButton = findViewById(R.id.food_button);
        calorieTextView = findViewById(R.id.calorie_tv);
        fatTextView = findViewById(R.id.fat_tv);
        carbsTextView = findViewById(R.id.carbs_tv);
        proteinTextView = findViewById(R.id.protein_tv);
        fibreTextView = findViewById(R.id.fibre_tv);
        saltTextView = findViewById(R.id.salt_tv);
        Date date = new Date();
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        final int year = calender.get(Calendar.YEAR);
        final int month = calender.get(Calendar.MONTH) + 1;
        final int day = calender.get(Calendar.DAY_OF_MONTH);

        String dayStr = String.valueOf(day);
        String monthStr = String.valueOf(month);
        if(day<10){
            dayStr = "0"+day;
        }
        if(month<10){
            monthStr = "0"+month;
        }
        String dateHtml = year+"/"+monthStr+"/"+dayStr;

        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference UserRef = RootRef.child("Users").child(Prevalent.currentOnlineUser.getEmail());
        final DatabaseReference MoneyRef = RootRef.child("User Money").child(Prevalent.currentOnlineUser.getEmail()).child(dateHtml);;
        final DatabaseReference FoodRef = RootRef.child("User Foods").child(Prevalent.currentOnlineUser.getEmail()).child(dateHtml);;

        UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users user = snapshot.getValue(Users.class);
                    assert user != null;
                    usernameTextView.setText(user.getName());
                    emailTextView.setText(user.getEmail());
                    lifetimeAmountTextView.setText("Lifetime amount spent: £"+user.getLifetime_amount());
                    if (user.getImage().equals("default")) {
                        profileImageView.setImageResource(R.drawable.profile);
                    } else {
                        Picasso.get().load(user.getImage()).into(profileImageView);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        MoneyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Money money = snapshot.getValue(Money.class);
                String amount = money.getAmount();
                todayAmountTextView.setText("Amount spent today: £"+amount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FoodRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                }
                if (calorieVal >= calorieTarget) {
                    calorieTextView.setTextColor(RED);
                }
                if (proteinVal >= proteinTarget) {
                    proteinTextView.setTextColor(RED);
                }
                if (carbsVal >= carbsTarget) {
                    carbsTextView.setTextColor(RED);
                }
                if (fibreVal >= fibreTarget) {
                    fibreTextView.setTextColor(RED);
                }
                if (saltVal >= saltTarget) {
                    saltTextView.setTextColor(RED);
                }
                if (fatVal >= fatTarget) {
                    fatTextView.setTextColor(RED);
                }
                calorieTextView.setText("Calories: "+calorieVal+" kcal");
                fatTextView.setText("Fat: "+fatVal+"g");
                carbsTextView.setText("Carbohydrates: "+carbsVal+"g");
                proteinTextView.setText("Protein: "+proteinVal+"g");
                fibreTextView.setText("Fibre: "+fibreVal+"g");
                saltTextView.setText("Salt: "+saltVal+"g");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        settingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
        moneyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MoneyTrackerActivity.class);
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
                intent.putExtra("DATE", sdf.format(date));
                startActivity(intent);
            }
        });
        foodButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FoodTrackerActivity.class);
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
                intent.putExtra("DATE", sdf.format(date));
                startActivity(intent);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        finish();
    }
}