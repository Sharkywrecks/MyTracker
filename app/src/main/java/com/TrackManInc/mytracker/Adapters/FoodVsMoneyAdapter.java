package com.TrackManInc.mytracker.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.TrackManInc.mytracker.FoodTrackerActivity;
import com.TrackManInc.mytracker.Model.FoodVsMoney;
import com.TrackManInc.mytracker.MoneyTrackerActivity;
import com.TrackManInc.mytracker.R;

import java.util.ArrayList;
import java.util.List;

public class FoodVsMoneyAdapter extends RecyclerView.Adapter<FoodvsMoneyViewHolder>{
    private Context context;
    private Activity activity;
    private ArrayList<FoodVsMoney> foodVsMoneyList;

    public FoodVsMoneyAdapter(Context context, Activity activity, ArrayList<FoodVsMoney> foodVsMoneyList) {
        this.context = context;
        this.foodVsMoneyList = foodVsMoneyList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public FoodvsMoneyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_vs_money_layout,parent,false);
        return new FoodvsMoneyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodvsMoneyViewHolder holder, int position) {
        FoodVsMoney foodVsMoney = foodVsMoneyList.get(position);
        holder.foodListTV.setText(listToLineBrokenString(foodVsMoney.getFoodNames()));
        holder.moneyTV.setText(foodVsMoney.getMoney());
        holder.date1.setText(foodVsMoney.getDate());
        holder.date2.setText(foodVsMoney.getDate());
        setupClickListener(holder,position);
    }

    private void setupClickListener(FoodvsMoneyViewHolder holder, int position) {
        holder.foodListTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FoodTrackerActivity.class);
                intent.putExtra("DATE",foodVsMoneyList.get(position).getDate());
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });
        holder.moneyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MoneyTrackerActivity.class);
                intent.putExtra("DATE",foodVsMoneyList.get(position).getDate());
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodVsMoneyList.size();
    }
    private String listToLineBrokenString(ArrayList<String> foodNames){
        String lineBrokenString = "";
        for(String foodName:foodNames){
            if(!lineBrokenString.equals("")){
                lineBrokenString = lineBrokenString +System.getProperty("line.separator");
            }
            lineBrokenString = lineBrokenString +"\u2022"+foodName ;
        }
        return lineBrokenString;
    }
}
