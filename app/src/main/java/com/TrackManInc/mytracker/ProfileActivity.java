package com.TrackManInc.mytracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.TrackManInc.mytracker.Model.Users;
import com.TrackManInc.mytracker.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private ImageView profileImageView;
    private TextView usernameTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileImageView = findViewById(R.id.profile_image_view);
        usernameTextView = findViewById(R.id.username_text_view);
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference UserRef = RootRef.child("Users").child(Prevalent.currentOnlineUser.getEmail());
        UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                /*if (snapshot.exists()) {
                    Users user = snapshot.getValue(Users.class);
                    usernameTextView.setText(user.getName());
                    if (user.getImage().equals("default")) {
                        profileImageView.setImageResource(R.drawable.profile);
                    } else {
                        Glide.with(ProfileActivity.this)
                                .load(user.getImage())
                                .apply(RequestOptions.circleCropTransform())
                                .into(profileImageView);
                    }
                }*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void update_UI(Users user){

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        finish();
    }
}