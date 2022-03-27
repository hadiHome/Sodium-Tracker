package com.sodiumtracker.ui.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sodiumtracker.R;
import com.sodiumtracker.activities.AddSodiumActivity;
import com.sodiumtracker.database.entity.Food;

import java.util.List;

public class RecyclerViewTodayAdapter extends RecyclerView.Adapter<RecyclerViewTodayAdapter.ViewHolder> {

    private List<Food> foods;
    Context context;

    public RecyclerViewTodayAdapter(List<Food> foods) {
        this.foods = foods;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView foodName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.foodName = itemView.findViewById(R.id.foodName);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.today_food_item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Food food = foods.get(position);
        holder.foodName.setText(food.name+" - "+ food.amount + " - "+ food.date);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddSodiumActivity.class);
            intent.putExtra("id",food.id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }


}