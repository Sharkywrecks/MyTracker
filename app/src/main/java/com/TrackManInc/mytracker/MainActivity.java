package com.TrackManInc.mytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.TrackManInc.mytracker.Model.Users;
import com.TrackManInc.mytracker.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button signUpButton,logInButton;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUIViews();
        setupPaper();
    }

    private void setupPaper() {
        Paper.init(this);
        String userEmailKey = Paper.book().read(Prevalent.userEmailKey);
        String userPasswordKey = Paper.book().read(Prevalent.userPasswordKey);

        if(userEmailKey!= "" && userPasswordKey!=""){
            if(!TextUtils.isEmpty(userEmailKey) && !TextUtils.isEmpty(userPasswordKey)){
                AllowAccess(userEmailKey,userPasswordKey);

                loadingBar.setTitle("Already logged in");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }

    private void setupUIViews() {
        signUpButton = findViewById(R.id.sign_up_button);
        logInButton = findViewById(R.id.log_in_button);
        loadingBar = new ProgressDialog(this);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
            }
        });
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    private void AllowAccess(final String email, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(email).exists()){
                    Users usersData = snapshot.child("Users").child(email).getValue(Users.class);

                    if(usersData.getEmail().equals(email)){
                        if(usersData.getPassword().equals(password)){
                            Toast.makeText(getApplicationContext(), "You are already logged in.", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Prevalent.currentOnlineUser = usersData;
                            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), "Password is incorrect", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Account with this "+email+" address does not exist.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}