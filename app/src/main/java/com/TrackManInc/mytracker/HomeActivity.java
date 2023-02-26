package com.TrackManInc.mytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.TrackManInc.mytracker.Adapters.FoodVsMoneyAdapter;
import com.TrackManInc.mytracker.Model.FoodVsMoney;
import com.TrackManInc.mytracker.Model.Money;
import com.TrackManInc.mytracker.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<FoodVsMoney> foodVsMoneyArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupUIView();
        retrieveData();
        fillRecyclerView();
    }

    private void retrieveData() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate;
        ArrayList<String> dayFoods;
        String dayMoney = "";
        for(int count = 0;count<31;count++){
            formattedDate = df.format(cal.getTime());
            dayFoods = retrieveDaysFoods(formattedDate);
            dayMoney = retrieveDaysMoney(formattedDate);
            foodVsMoneyArrayList.add(new FoodVsMoney(formattedDate,dayMoney,dayFoods));
            cal.add(Calendar.DAY_OF_MONTH,-1);
        }
    }

    private void setupUIView() {
        recyclerView = findViewById(R.id.food_vs_money_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Button temp = findViewById(R.id.temp);

        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddFoodActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fillRecyclerView() {
        super.onStart();
        FoodVsMoneyAdapter adapter = new FoodVsMoneyAdapter(HomeActivity.this,foodVsMoneyArrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyItemRangeChanged(0,foodVsMoneyArrayList.size());
    }

    private String retrieveDaysMoney(String formattedDate) {
        final String[] totalMoney = {""};
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference MoneyRef = RootRef.child("User Money").child(Prevalent.currentOnlineUser.getEmail()).child(formattedDate);
        MoneyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Money userMoney = snapshot.getValue(Money.class);
                    totalMoney[0] = userMoney.getAmount();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return totalMoney[0];
    }

    private ArrayList<String> retrieveDaysFoods(String formattedDate) {
        ArrayList<String> foodList = new ArrayList<>();
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference FoodRef = RootRef.child("User Foods").child(Prevalent.currentOnlineUser.getEmail()).child(formattedDate);
        FoodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    foodList.add(ds.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return foodList;
    }
}