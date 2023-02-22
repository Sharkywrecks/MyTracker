package com.TrackManInc.mytracker.Adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.TrackManInc.mytracker.R;

public class FoodvsMoneyViewHolder extends RecyclerView.ViewHolder {

    TextView foodListTV, moneyTV,date1,date2;
    public FoodvsMoneyViewHolder(@NonNull View itemView) {
        super(itemView);
        foodListTV = itemView.findViewById(R.id.food_names);
        moneyTV = itemView.findViewById(R.id.total_day_money);
        date1 = itemView.findViewById(R.id.date1);
        date2 = itemView.findViewById(R.id.date2);
    }

}
