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

import com.sodiumtracker.MyPreferences;
import com.sodiumtracker.R;
import com.sodiumtracker.database.AppDatabase;
import com.sodiumtracker.database.entity.Food;
import com.sodiumtracker.utils.DatesUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChangeLimitAmountDialog extends Dialog implements
        View.OnClickListener {


    EditText amountLimitET;
    Button saveButton;

    public ChangeLimitAmountDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    public void onClick(View v) {


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_limit_amount);


        amountLimitET = findViewById(R.id.amountLimitET);
        saveButton = findViewById(R.id.saveButton);

        int limit = MyPreferences.getAmountLimit(getContext());
        amountLimitET.setText(limit + "");




        saveButton.setOnClickListener(view -> {

            String limitStr = amountLimitET.getText().toString();
            if (!limitStr.equals("")) {
                int limitNew = Integer.parseInt(limitStr);
                MyPreferences.setAmountLimit(limitNew, getContext());
               dismiss();
            } else {
                Toast.makeText(getContext(), R.string.please_fill_all_fields, Toast.LENGTH_SHORT).show();
            }

        });


    }








}
