package com.TrackManInc.mytracker;

import static android.graphics.Color.RED;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.TrackManInc.mytracker.Adapters.FoodVsMoneyAdapter;
import com.TrackManInc.mytracker.Model.FoodVsMoney;
import com.TrackManInc.mytracker.Model.Money;
import com.TrackManInc.mytracker.Model.Nutrients;
import com.TrackManInc.mytracker.Model.Users;
import com.TrackManInc.mytracker.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {
    private double calorieVal =0;
    private int calorieTarget = 0,moneyTarget =0 ;
    private TextView calorieProgress, moneyProgress,streakTV,usernameTV;
    private ProgressBar calorieBar,moneyBar;
    private int index = 0;
    private FoodVsMoneyAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<FoodVsMoney> foodVsMoneyArrayList = new ArrayList<>();
    private Dialog dialog;
    private int prevStreak=0;
    private String prevStreakDate="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                startDialog();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        usernameTV = navigationView.getHeaderView(0).findViewById(R.id.user_profile_name);
        usernameTV.setText(Prevalent.currentOnlineUser.getName());
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.nav_profile){
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                }else if(id==R.id.nav_settings){
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
                return true;
            }
        });
    }

    private void startDialog() {
        int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.50);

        dialog.show();
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        VideoView videoView = dialog.findViewById(R.id.streak_video);
        videoView.setVideoPath("android.resource://"+getPackageName()+"/"+R.raw.streak);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate = df.format(Calendar.getInstance().getTime());
        retrieveDialogueData(formattedDate);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        videoView.setSoundEffectsEnabled(false);
        videoView.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    private void retrieveData() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate;
        ArrayList<String> dayFoods;
        foodVsMoneyArrayList.clear();
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
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.goals_popup);
        dialog.setCancelable(true);
        calorieBar = dialog.findViewById(R.id.calorie_goal_bar);
        moneyBar = dialog.findViewById(R.id.money_goal_bar);
        calorieProgress = dialog.findViewById(R.id.calorieProgress);
        moneyProgress = dialog.findViewById(R.id.moneyProgress);
        streakTV = dialog.findViewById(R.id.streak_count);
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getEmail());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                if(user!=null) {
                    prevStreak = Integer.parseInt(user.getStreak());
                    prevStreakDate = user.getPrevious_date_streak();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fillRecyclerView() {
        super.onStart();
        adapter = new FoodVsMoneyAdapter(HomeActivity.this,HomeActivity.this,foodVsMoneyArrayList);
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
                if(index==1){
                    streakCheck();
                }
                if(index!=31) {
                    foodVsMoneyArrayList.get(index).setMoney("£" + totalMoney);
                    index++;
                }
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
    private void retrieveDialogueData(String formattedDate) {
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference nutrientRef = RootRef.child("User Foods").child(Prevalent.currentOnlineUser.getEmail()).child(formattedDate);
        nutrientRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double carbsVal=0;
                double proteinVal=0;
                double fatVal=0;
                double saltVal=0;
                double fibreVal=0;
                double amountVal=0;
                for(DataSnapshot nutrientDS:snapshot.getChildren()){
                    Nutrients usersNutrients = nutrientDS.getValue(Nutrients.class);
                    if(usersNutrients==null){return;}
                    String carbs,protein,fat,fibre,salt,amount;
                    carbs = checkRetrievedValue(usersNutrients.getCarbs());
                    protein = checkRetrievedValue(usersNutrients.getProtein());
                    fat = checkRetrievedValue(usersNutrients.getFat());
                    fibre = checkRetrievedValue(usersNutrients.getFiber());
                    salt = checkRetrievedValue(usersNutrients.getSalt());
                    amount = checkRetrievedValue(usersNutrients.getSalt());
                    carbsVal+=Double.parseDouble(carbs);
                    proteinVal += Double.parseDouble(protein);
                    fatVal += Double.parseDouble(fat);
                    saltVal += Double.parseDouble(salt);
                    fibreVal += Double.parseDouble(fibre);
                    amountVal+=Double.parseDouble(amount);
                    calorieVal = 4*proteinVal + 4*carbsVal + 9*fatVal;
                    setupProgressBar();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupProgressBar() {
        String money = foodVsMoneyArrayList.get(0).getMoney();
        int moneyInteger = (int)Double.parseDouble(money.substring(1));
        calorieBar.setMax(calorieTarget);
        moneyBar.setMax(moneyTarget);
        calorieBar.setProgress((int)calorieVal);
        moneyBar.setProgress(moneyInteger);
        streakTV.setText(Prevalent.currentOnlineUser.getStreak());
        calorieProgress.setText(calorieVal + "/" + calorieTarget + "kcal");
        moneyProgress.setText(money + "/" + moneyTarget );

        if (calorieVal >= calorieTarget) {
            calorieProgress.setTextColor(RED);
        }
        if (moneyInteger >= moneyTarget) {
            moneyProgress.setTextColor(RED);
        }
    }
    private boolean dateDifference(String date2){
        if(date2.equals("")||date2.equals(foodVsMoneyArrayList.get(0).getDate())){return true;}
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH,-1);
        String date1 = df.format(cal.getTime());
        cal.clear();
        if(date1.equals(date2)){
            return true;
        }
        return false;
    }
    private void streakCheck(){
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getEmail());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                if(user!=null){
                    if(!dateDifference(user.getPrevious_date_streak())){
                        HashMap<String,Object> userDataMap = new HashMap<>();
                        prevStreakDate = foodVsMoneyArrayList.get(0).getDate();
                        userDataMap.put("password",user.getPassword());
                        userDataMap.put("name",user.getName());
                        userDataMap.put("email",user.getEmail());
                        userDataMap.put("streak","0");
                        userDataMap.put("previous_date_streak",prevStreakDate);
                        Prevalent.currentOnlineUser.setStreak("0");
                        Prevalent.currentOnlineUser.setPrevious_date_streak(prevStreakDate);
                        userDataMap.put("lifetime_amount",user.getLifetime_amount());
                        userDataMap.put("image",user.getImage());

                        userRef.updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(HomeActivity.this,"Your streak went up",Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    if(foodVsMoneyArrayList.get(0).getFoodNames().size()==0){return;}
                    if(foodVsMoneyArrayList.get(0).getFoodNames().get(0)!=null
                            && foodVsMoneyArrayList.get(0).getMoney()!=null && !prevStreakDate.equals(foodVsMoneyArrayList.get(0).getDate())){
                        if(prevStreak==Integer.parseInt(Prevalent.currentOnlineUser.getStreak())){
                            HashMap<String,Object> userDataMap = new HashMap<>();
                            prevStreakDate = foodVsMoneyArrayList.get(0).getDate();
                            userDataMap.put("password",user.getPassword());
                            userDataMap.put("name",user.getName());
                            userDataMap.put("email",user.getEmail());
                            userDataMap.put("streak",""+(prevStreak+1));
                            userDataMap.put("previous_date_streak",prevStreakDate);
                            Prevalent.currentOnlineUser.setStreak(""+(prevStreak+1));
                            Prevalent.currentOnlineUser.setPrevious_date_streak(prevStreakDate);
                            userDataMap.put("lifetime_amount",user.getLifetime_amount());
                            userDataMap.put("image",user.getImage());
                            userRef.updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(HomeActivity.this,"Your streak went up",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
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
    protected void onRestart() {
        super.onRestart();
        index=0;
        retrieveData();
    }

}