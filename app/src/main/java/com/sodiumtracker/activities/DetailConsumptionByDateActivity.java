package com.sodiumtracker.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.sodiumtracker.R;
import com.sodiumtracker.database.AppDatabase;
import com.sodiumtracker.ui.home.adapters.RecyclerViewTodayAdapter;

public class DetailConsumptionByDateActivity extends AppCompatActivity {

    RecyclerView byDateRV;
    long startDateMilli;
    long endDateMilli;


    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_consumption_by_date);

        Intent intent = getIntent();
        if (intent.hasExtra("startDateMilli") && intent.hasExtra("endDateMilli")) {
            startDateMilli = intent.getLongExtra("startDateMilli", 0);
            endDateMilli = intent.getLongExtra("endDateMilli", 0);
            if (startDateMilli == 0 || endDateMilli == 0) {
                finish();
            }
        } else {
            finish();
        }


        byDateRV = findViewById(R.id.byDateRV);

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class, "app_database"
        ).allowMainThreadQueries().build();

        byDateRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    @Override
    protected void onResume() {
        super.onResume();
        setLayout();
    }

    public void setLayout() {

        RecyclerViewTodayAdapter recyclerViewTodayAdapter = new RecyclerViewTodayAdapter(db.foodDao().getByDate(startDateMilli, endDateMilli));
        byDateRV.setAdapter(recyclerViewTodayAdapter);

    }
}