package com.TrackManInc.mytracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import com.TrackManInc.mytracker.Adapters.FoodVsMoneyAdapter;
import com.TrackManInc.mytracker.Model.FoodVsMoney;
import com.TrackManInc.mytracker.Model.Money;
import com.TrackManInc.mytracker.Prevalent.Prevalent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {
    private int index = 0;
    private FoodVsMoneyAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<FoodVsMoney> foodVsMoneyArrayList = new ArrayList<>();
    private  NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Paper.init(this);
        setupDrawerNav();
        setupUIView();
        retrieveData();
        fillRecyclerView();
    }

    private void setupDrawerNav() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with goals activity intent or pop up", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        toggle.syncState();
        navigationView = drawer.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Toast.makeText(HomeActivity.this,"Works",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        //TODO:Fix menu item click

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        System.out.println("Reached");
        if(id==R.id.nav_settings){
            return true;
        }else if(id==R.id.nav_logout){
            Paper.book().destroy();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return super.onOptionsItemSelected(item);
    }

    private void retrieveData() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate;
        ArrayList<String> dayFoods;
        for(int count = 0;count<31;count++){
            formattedDate = df.format(cal.getTime());
            dayFoods = retrieveDaysFoods(formattedDate);
            retrieveDaysMoney(formattedDate);
            foodVsMoneyArrayList.add(new FoodVsMoney(formattedDate,"£0.00",dayFoods));
            cal.add(Calendar.DAY_OF_MONTH,-1);
        }
        Calendar.getInstance().clear();
    }

    private void setupUIView() {
        recyclerView = findViewById(R.id.food_vs_money_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fillRecyclerView() {
        super.onStart();
        adapter = new FoodVsMoneyAdapter(HomeActivity.this,foodVsMoneyArrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void retrieveDaysMoney(String formattedDate) {
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference MoneyRef = RootRef.child("User Money").child(Prevalent.currentOnlineUser.getEmail()).child(formattedDate);
        MoneyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String totalMoney = "0.00";
                if(snapshot.exists()){
                    Money userMoney = snapshot.getValue(Money.class);
                    if(userMoney!=null){
                        if(!userMoney.getAmount().equals("")){
                            totalMoney = userMoney.getAmount();
                        }
                    }
                }
                foodVsMoneyArrayList.get(index).setMoney("£"+totalMoney);
                index++;
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private ArrayList<String> retrieveDaysFoods(String formattedDate) {
        ArrayList<String> foodList = new ArrayList<>();
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference FoodRef = RootRef.child("User Foods").child(Prevalent.currentOnlineUser.getEmail()).child(formattedDate);
        FoodRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    foodList.add(ds.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return foodList;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onRestart() {
        super.onRestart();
        index=0;
        retrieveData();
    }
}