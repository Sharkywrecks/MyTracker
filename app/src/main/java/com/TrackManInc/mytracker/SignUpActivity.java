package com.TrackManInc.mytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.TrackManInc.mytracker.Filters.TextInputFilter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private EditText usernameET,emailET,passwordET;
    private Button signUpButton;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        setupUIView();
    }
    private void setupUIView(){
        usernameET = findViewById(R.id.username);
        usernameET.setFilters(new InputFilter[]{new TextInputFilter(20)});
        emailET = findViewById(R.id.email);
        emailET.setFilters(new InputFilter[]{new TextInputFilter(100)});
        passwordET = findViewById(R.id.password);
        passwordET.setFilters(new InputFilter[]{new TextInputFilter(100)});
        signUpButton = findViewById(R.id.sign_up_button);
        loadingBar = new ProgressDialog(this);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }
    private void createAccount(){
        String username=usernameET.getText().toString();
        String email=emailET.getText().toString();
        String password=passwordET.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Please enter your name...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter your email...", Toast.LENGTH_SHORT).show();
        }else if(!usernameIsValid(username)){
            Toast.makeText(this, "'.', '$', '#', '[', ']' are not valid characters", Toast.LENGTH_SHORT).show();
        } else if(!emailIsValid(email)){
            Toast.makeText(this, "Please use a valid email...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your password...", Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Please wait. We are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validateUser(username,email,password);
        }
    }

    private boolean usernameIsValid(String username) {
        if (username.contains("#") || username.contains(".") || username.contains("$") || username.contains("[") || username.contains("]")){
            return false;
        }
        return true;
    }


    private boolean emailIsValid(String email){
        Pattern pat = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");//("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])\n");
        Matcher m = pat.matcher(email);
        return m.find();
    }
    private void validateUser(String name,String email,String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.child("Users").child(name).exists())){
                    HashMap<String,Object> userDataMap = new HashMap<>();
                    userDataMap.put("password",password);
                    userDataMap.put("name",name);
                    userDataMap.put("email",email);
                    userDataMap.put("streak","0");
                    userDataMap.put("previous_date_streak","");
                    userDataMap.put("lifetime_amount","0.00");
                    userDataMap.put("image","");
                    userDataMap.put("age","25");
                    userDataMap.put("gender","1");
                    userDataMap.put("height","1.80");
                    userDataMap.put("weight","75");
                    userDataMap.put("money","100");
                    userDataMap.put("calorie","2500");

                    RootRef.child("Users").child(name).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }else{
                    Toast.makeText(getApplicationContext(), "The username "+name+" already exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(getApplicationContext(), "Please try again using another.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}