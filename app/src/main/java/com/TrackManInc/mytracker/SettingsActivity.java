package com.TrackManInc.mytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.TrackManInc.mytracker.Model.Users;
import com.TrackManInc.mytracker.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Spinner gender;
    private EditText name, email,password, age, height, weight, calorie, money;
    private Button save, discard;
    private static final String[] paths = {"Female", "Male"};

    private HashMap<String, String> savedState = new HashMap<String, String>();
    private HashMap<String, String> currentState = new HashMap<String, String>();

    private boolean changed;

    private final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference UserRef = RootRef.child("Users").child(Prevalent.currentOnlineUser.getEmail());

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupUIView();
    }


    private void setupUIView(){
        gender = (Spinner)findViewById(R.id.gender_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingsActivity.this, android.R.layout.simple_spinner_item,paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);
        gender.setOnItemSelectedListener(this);

        name = findViewById(R.id.nameET);
        email = findViewById(R.id.email_ET);
        email.setFocusable(false);
        email.setKeyListener(null);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingsActivity.this,"Contact admins to change",Toast.LENGTH_SHORT).show();
            }
        });
        password = findViewById(R.id.password_ET);
        age = findViewById(R.id.age_ET);
        height = findViewById(R.id.height_ET);
        weight = findViewById(R.id.weight_ET);
        calorie = findViewById(R.id.calorie_ET);
        money = findViewById(R.id.money_ET);

        save = findViewById(R.id.save_btn);
        discard = findViewById(R.id.discard_btn);

        UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    savedState.clear();
                    Users user = snapshot.getValue(Users.class);
                    assert user != null;
                    name.setText(user.getName());
                    email.setText(user.getEmail());
                    password.setText(user.getPassword());
                    age.setText(user.getAge());
                    gender.setSelection(user.getGender());//0:female, 1:male
                    height.setText(user.getHeight());
                    weight.setText(user.getWeight());
                    calorie.setText(user.getCalorie());
                    money.setText(user.getMoney());
                    savedState.put("name", user.getName());
                    savedState.put("email", user.getEmail());
                    savedState.put("password", user.getPassword());
                    savedState.put("age", user.getAge());
                    savedState.put("gender", Integer.toString(user.getGender()));
                    savedState.put("height", user.getHeight());
                    savedState.put("weight", user.getWeight());
                    savedState.put("calorie", user.getCalorie());
                    savedState.put("money", user.getMoney());
                    currentState.putAll(savedState);
                    compareMaps();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentState.replace("name", editable.toString());
                compareMaps();
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentState.replace("password", editable.toString());
                compareMaps();
            }
        });

        age.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentState.replace("age", editable.toString());
                compareMaps();
            }
        });

        height.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentState.replace("height", editable.toString());
                compareMaps();
            }
        });

        weight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentState.replace("weight", editable.toString());
                compareMaps();
            }
        });

        calorie.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentState.replace("calorie", editable.toString());
                compareMaps();
            }
        });

        money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentState.replace("money", editable.toString());
                compareMaps();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedState.putAll(currentState);
                Prevalent.currentOnlineUser.setCalorie(savedState.get("calorie"));
                Prevalent.currentOnlineUser.setMoney(savedState.get("money"));
                Prevalent.currentOnlineUser.setWeight(savedState.get("weight"));
                Prevalent.currentOnlineUser.setHeight(savedState.get("height"));
                Prevalent.currentOnlineUser.setAge(savedState.get("age"));
                Prevalent.currentOnlineUser.setPassword(savedState.get("password"));
                Prevalent.currentOnlineUser.setName(savedState.get("name"));
                if(save.isEnabled()){
                    for (String key : savedState.keySet()) {
                        UserRef.child(key).setValue(savedState.get(key));
                    }
                    compareMaps();
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    finish();
                }
            }
        });

        discard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(discard.isEnabled()){
                    name.setText(savedState.get("name"));
                    email.setText(savedState.get("email"));
                    password.setText(savedState.get("password"));
                    age.setText(savedState.get("age"));
                    gender.setSelection(Integer.parseInt(Objects.requireNonNull(savedState.get("gender"))));//0:female, 1:male
                    height.setText(savedState.get("height"));
                    weight.setText(savedState.get("weight"));
                    calorie.setText(savedState.get("calorie"));
                    money.setText(savedState.get("money"));
                    currentState.putAll(savedState);
                }
                compareMaps();
            }
        });

    }

    private void compareMaps(){
        changed = !savedState.equals(currentState);
        if(changed){
            save.setEnabled(true);
            discard.setEnabled(true);
        }else{
            save.setEnabled(false);
            discard.setEnabled(false);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0: //female
                currentState.replace("gender","0");
                compareMaps();
                break;
            case 1: //male
                currentState.replace("gender","1");
                compareMaps();
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        finish();
    }
}