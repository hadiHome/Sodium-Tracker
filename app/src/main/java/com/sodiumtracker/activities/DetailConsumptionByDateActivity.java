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

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailConsumptionByDateActivity extends AppCompatActivity implements RecyclerViewTodayAdapter.Refresh {

    RecyclerView byDateRV;
    long startDateMilli;
    long endDateMilli;


    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_consumption_by_date);
        SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");

        Intent intent = getIntent();
        if (intent.hasExtra("startDateMilli") && intent.hasExtra("endDateMilli")) {
            startDateMilli = intent.getLongExtra("startDateMilli", 0);
            endDateMilli = intent.getLongExtra("endDateMilli", 0);
            if (startDateMilli == 0 || endDateMilli == 0) {
                finish();
            }
            setTitle(sdfDay.format(new Date(startDateMilli)));
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

        RecyclerViewTodayAdapter recyclerViewTodayAdapter = new RecyclerViewTodayAdapter(db.foodDao().getByDate(startDateMilli, endDateMilli), this);
        byDateRV.setAdapter(recyclerViewTodayAdapter);

    }

    @Override
    public void refreshing() {
        setLayout();
    }
}