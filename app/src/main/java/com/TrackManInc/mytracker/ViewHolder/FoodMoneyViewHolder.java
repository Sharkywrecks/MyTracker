package com.TrackManInc.mytracker.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.TrackManInc.mytracker.Interface.ItemClickListener;
import com.TrackManInc.mytracker.R;


public class FoodMoneyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView foodNamesTV,totalDayMoneyTV;
    private ItemClickListener itemClickListener;

    public FoodMoneyViewHolder(View itemView) {
        super(itemView);

        setupUIViews();
    }

    private void setupUIViews() {
        foodNamesTV = itemView.findViewById(R.id.food_names);
        totalDayMoneyTV = itemView.findViewById(R.id.total_day_money);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
