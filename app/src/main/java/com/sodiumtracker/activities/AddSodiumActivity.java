package com.sodiumtracker.activities;

import static java.util.Calendar.HOUR_OF_DAY;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.sodiumtracker.MyPreferences;
import com.sodiumtracker.R;
import com.sodiumtracker.database.AppDatabase;
import com.sodiumtracker.database.entity.Food;
import com.sodiumtracker.utils.DatesUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddSodiumActivity extends AppCompatActivity {


    public Calendar myCalendar;
    public DatePickerDialog.OnDateSetListener date;
    int id = 0;
    Food food = null;

    public EditText nameEt, amountEt;
    public TextView dateET, txt1;
    boolean isUpdate = false;
    public AppDatabase db;
    public int foodId;
    public Button add, delete;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sodium);
        AdRequest adRequest = new AdRequest.Builder().build();
        db = Room.databaseBuilder(
                AddSodiumActivity.this,
                AppDatabase.class, "app_database"
        ).allowMainThreadQueries().build();


        if (MyPreferences.isAdsRemoved(AddSodiumActivity.this)) {

        } else {
            InterstitialAd.load(AddSodiumActivity.this, "ca-app-pub-9624523949741017/6296979680", adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                            Log.i("TAG", "onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.i("TAG", loadAdError.getMessage());
                            mInterstitialAd = null;
                        }
                    });
        }

        dateET = findViewById(R.id.dateET);
        nameEt = findViewById(R.id.nameEt);
        amountEt = findViewById(R.id.amountEt);
        txt1 = findViewById(R.id.txt1);
        add = findViewById(R.id.add);
        delete = findViewById(R.id.delete);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.foodId = extras.getInt("id");
//            this.foodId = id ;
            if (this.foodId != -1) {
                add.setText("Update");
                add.setText("Update");
                delete.setVisibility(View.VISIBLE);
                isUpdate = true;

                food = db.foodDao().getOne(foodId);
                nameEt.setText(food.name);
                amountEt.setText(food.amount + "");
            } else {
                delete.setVisibility(View.GONE);
                add.setText("Add");
                txt1.setText("Add");
            }
        }
//        Intent intent = null;
//                    id = intent.getIntExtra("id", 0);
//        Log.d("TAG", "onCreate: " + foodId);
//        if (this.foodId != -1) {
//            add.setText("Update");
//            add.setText("Update");
//            delete.setVisibility(View.VISIBLE);
//            isUpdate = true;
//
//            food = db.foodDao().getOne(foodId);
//            nameEt.setText(food.name);
//            amountEt.setText(food.amount + "");
//        } else {
//            delete.setVisibility(View.GONE);
//            add.setText("Add");
//            txt1.setText("Add");
//        }


        setDatePicker();

        dateET.setOnClickListener(v -> selectDate(v));

        add.setOnClickListener(v -> add(v));

        delete.setOnClickListener(v -> {
            db.foodDao().delete(food);
            finish();

        });

    }

    public void selectDate(View view) {
        new DatePickerDialog(AddSodiumActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void setDatePicker() {
        // add
        if (!isUpdate) {
            myCalendar = Calendar.getInstance();
        } else {
            myCalendar = DatesUtils.toCalendar(food.date);
        }
//        myCalendar.set(Calendar.HOUR_OF_DAY, 0);
//        myCalendar.set(Calendar.MINUTE, 0);
        myCalendar.set(Calendar.SECOND, 0);
        myCalendar.set(Calendar.MILLISECOND, 0);

        date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            selectTime();
            updateLabel();
        };
        updateLabel();
    }

    public void selectTime() {
        new TimePickerDialog(AddSodiumActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                myCalendar.set(Calendar.MINUTE, selectedMinute);
                updateLabel();
            }
        }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), false).show();//Yes 24 hour time)
    }

    private void updateLabel() {
//        String myFormat = "EEEE, MM/dd/yyyy";
        String myFormat = "EEEE, MM/dd/yyyy hh:mm aa";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
//        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aa");
        dateET.setText(sdf.format(myCalendar.getTime()));
    }


    public void add(View view) {
        showAd();

        String name = nameEt.getText().toString();
        if (name.equals("")) {
            name = "Random";
        }
        String amountStr = amountEt.getText().toString();
        Calendar calendar = myCalendar;
        Date date = calendar.getTime();

        if (!name.equals("") && !amountStr.equals("") && date != null) {
            int amount = Integer.parseInt(amountStr);

            if (!isUpdate) {
                Food food = new Food();
                food.name = name;
                food.amount = amount;
                food.date = date;
                db.foodDao().insertAll(food);
                Toast.makeText(AddSodiumActivity.this, R.string.successfully_added, Toast.LENGTH_SHORT).show();
                finish();
            } else {

                this.food.name = name;
                this.food.amount = amount;
                this.food.date = date;
                db.foodDao().updateFoods(this.food);
                Toast.makeText(AddSodiumActivity.this, R.string.successfully_updated, Toast.LENGTH_SHORT).show();
                finish();
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AddSodiumActivity.this);
            SharedPreferences.Editor ed = prefs.edit();
            ed.putString("dismiss", "1");
            ed.commit();


        } else {
            Toast.makeText(AddSodiumActivity.this, R.string.please_fill_all_fields, Toast.LENGTH_SHORT).show();
        }
    }

    public void showAd() {
        if (mInterstitialAd != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AddSodiumActivity.this);
            SharedPreferences.Editor ed = prefs.edit();
            ed.commit();
            mInterstitialAd.show(AddSodiumActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }
}