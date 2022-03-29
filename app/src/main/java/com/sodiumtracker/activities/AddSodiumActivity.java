package com.sodiumtracker.activities;

import static java.util.Calendar.HOUR_OF_DAY;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.sodiumtracker.R;
import com.sodiumtracker.database.AppDatabase;
import com.sodiumtracker.database.entity.Food;
import com.sodiumtracker.utils.DatesUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddSodiumActivity extends AppCompatActivity {

    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    int id = 0;
    Food food = null;

    EditText nameEt, amountEt, dateET;
    boolean isUpdate = false;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sodium);

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class, "app_database"
        ).allowMainThreadQueries().build();

        dateET = findViewById(R.id.dateET);
        nameEt = findViewById(R.id.nameEt);
        amountEt = findViewById(R.id.amountEt);

        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            setTitle("Update");
            isUpdate = true;
            id = intent.getIntExtra("id", 0);
            food = db.foodDao().getOne(id);
            nameEt.setText(food.name);
            amountEt.setText(food.amount + "");
        } else {
            setTitle("Add");
        }


        setDatePicker();
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

        String name = nameEt.getText().toString();
        String amountStr = amountEt.getText().toString();
        Date date = myCalendar.getTime();

        if (!name.equals("") && !amountStr.equals("") && date != null) {
            int amount = Integer.parseInt(amountStr);

            if (!isUpdate) {
                Food food = new Food();
                food.name = name;
                food.amount = amount;
                food.date = date;
                db.foodDao().insertAll(food);
                Toast.makeText(this, getString(R.string.successfully_added), Toast.LENGTH_SHORT).show();
            } else {

                this.food.name = name;
                this.food.amount = amount;
                this.food.date = date;
                db.foodDao().updateFoods(this.food);
                Toast.makeText(this, getString(R.string.successfully_updated), Toast.LENGTH_SHORT).show();
            }

            showAd();
            finish();
        } else {
            Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show();
        }
    }

    public void showAd() {

    }
}