package com.sodiumtracker.ui.home.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sodiumtracker.R;
import com.sodiumtracker.activities.AddSodiumActivity;
import com.sodiumtracker.activities.AddSodiumDialog;
import com.sodiumtracker.database.entity.Food;
import com.sodiumtracker.ui.home.HomeFragment;

import java.sql.Ref;
import java.text.SimpleDateFormat;
import java.util.List;

public class RecyclerViewTodayAdapter extends RecyclerView.Adapter<RecyclerViewTodayAdapter.ViewHolder> {

    private List<Food> foods;
    Context context;
    Refresh mCallback;

    public interface Refresh {
        void refreshing();
    }

    public RecyclerViewTodayAdapter(List<Food> foods, HomeFragment homeFragment) {
        this.foods = foods;
        mCallback = (Refresh) homeFragment;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView foodName, amount ,time;

        public ViewHolder(View itemView) {
            super(itemView);
            this.foodName = itemView.findViewById(R.id.foodName);
            this.amount = itemView.findViewById(R.id.amount);
            this.time = itemView.findViewById(R.id.time);
//            this.date = itemView.findViewById(R.id.date);
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
        holder.foodName.setText(food.name);
        holder.amount.setText(food.amount + "mg");

        String myFormat = "hh:mm aa";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        holder.time.setText(sdf.format(food.date));


//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//        String shortTimeStr = sdf.format(food.date);
//
//
//        holder.date.setText(shortTimeStr);

        holder.itemView.setOnClickListener(view -> {
//            Intent intent = new Intent(context, AddSodiumActivity.class);
//            intent.putExtra("id", food.id);
//
//            context.startActivity(intent);
            String hadi = "hi";

            AddSodiumDialog cdd = new AddSodiumDialog(context, food.id, hadi);
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cdd.show();

            cdd.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cdd.setOnDismissListener(dialog -> {
                mCallback.refreshing();

            });


        });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }


}