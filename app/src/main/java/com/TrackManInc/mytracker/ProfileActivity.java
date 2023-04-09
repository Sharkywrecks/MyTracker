package com.TrackManInc.mytracker;

import static android.graphics.Color.RED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.TrackManInc.mytracker.Model.Money;
import com.TrackManInc.mytracker.Model.Nutrients;
import com.TrackManInc.mytracker.Model.Users;
import com.TrackManInc.mytracker.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
                    Picasso.get().load(user.getImage()).into(profileImageView);
                    byte[] bytes=Base64.decode(user.getImage(),Base64.DEFAULT);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    profileImageView.setImageBitmap(bitmap);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        MoneyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String amount = "0.00";
                if(snapshot.exists()){
                    Money userMoney = snapshot.getValue(Money.class);
                    if(userMoney!=null){
                        if(!userMoney.getAmount().equals("")){
                            amount = userMoney.getAmount();
                        }
                    }
                }
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
                    fibre = checkRetrievedValue(usersNutrients.getFiber());
                    salt = checkRetrievedValue(usersNutrients.getSalt());
                    carbsVal+=Double.parseDouble(carbs);
                    proteinVal += Double.parseDouble(protein);
                    fatVal += Double.parseDouble(fat);
                    saltVal += Double.parseDouble(salt);
                    fibreVal += Double.parseDouble(fibre);
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

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProfilePicture();
            }
        });
    }

    private void setProfilePicture(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri;
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference UserRef =  RootRef.child("Users").child(Prevalent.currentOnlineUser.getEmail());
        if(resultCode == RESULT_OK && requestCode == 1 && data != null){
            uri = data.getData();
            Picasso.get().load(uri).into(profileImageView);
            DatabaseReference imageRef = UserRef.child("image");
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                String image_base64 = bitmapToBase64(bitmap);//"data:image/jpeg;base64," + bitmapToBase64(bitmap);
                imageRef.setValue(image_base64)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                toastMessage("Profile image updated.");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                toastMessage("Failed to update profile image.");
                            }
                        });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void toastMessage(String message){
        Spannable centeredText = new SpannableString(message);
        centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),0,message.length()-1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        Toast.makeText(getApplicationContext(), centeredText, Toast.LENGTH_SHORT).show();
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