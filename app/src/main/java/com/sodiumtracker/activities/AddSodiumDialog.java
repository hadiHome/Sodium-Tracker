package com.sodiumtracker.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Room;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.sodiumtracker.R;
import com.sodiumtracker.database.AppDatabase;
import com.sodiumtracker.database.entity.Food;
import com.sodiumtracker.utils.DatesUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddSodiumDialog extends Dialog implements
        android.view.View.OnClickListener {

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

    public AddSodiumDialog(@NonNull Context context, int foodId, String hadi) {
        super(context);
        this.foodId = foodId;
    }

    public AddSodiumDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected AddSodiumDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void onClick(View v) {


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_dialog);


        db = Room.databaseBuilder(
                getContext(),
                AppDatabase.class, "app_database"
        ).allowMainThreadQueries().build();

        dateET = findViewById(R.id.dateET);
        nameEt = findViewById(R.id.nameEt);
        amountEt = findViewById(R.id.amountEt);
        txt1 = findViewById(R.id.txt1);
        add = findViewById(R.id.add);
        delete = findViewById(R.id.delete);


        Intent intent = null;
        Log.d("TAG", "onCreate: " + foodId);
        if (this.foodId != -1) {
            add.setText("Update");
            txt1.setText("Update");
            delete.setVisibility(View.VISIBLE);
            isUpdate = true;
//            id = intent.getIntExtra("id", 0);
            food = db.foodDao().getOne(foodId);
            nameEt.setText(food.name);
            amountEt.setText(food.amount + "");
        } else {
            delete.setVisibility(View.GONE);
            add.setText("Add");
            txt1.setText("Add");
        }


        setDatePicker();

        dateET.setOnClickListener(v -> selectDate(v));

        add.setOnClickListener(v -> addItem(v));

        delete.setOnClickListener(v -> {
            db.foodDao().delete(food);
            dismiss();
        });


    }

    public void selectDate(View view) {
        new DatePickerDialog(getContext(), date, myCalendar
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
        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
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


    public void addItem(View view) {

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
                Toast.makeText(getContext(), R.string.successfully_added, Toast.LENGTH_SHORT).show();
            } else {

                this.food.name = name;
                this.food.amount = amount;
                this.food.date = date;
                db.foodDao().updateFoods(this.food);
                Toast.makeText(getContext(), R.string.successfully_updated, Toast.LENGTH_SHORT).show();
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor ed = prefs.edit();
            ed.putString("dismiss", "1");
            ed.commit();


            dismiss();
        } else {
            Toast.makeText(getContext(), R.string.please_fill_all_fields, Toast.LENGTH_SHORT).show();
        }
    }


}
