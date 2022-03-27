package com.sodiumtracker.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sodiumtracker.MyPreferences;
import com.sodiumtracker.R;

public class ChangeLimitAmountActivity extends AppCompatActivity {

    EditText amountLimitET;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_limit_amount);


        amountLimitET = findViewById(R.id.amountLimitET);
        saveButton = findViewById(R.id.saveButton);

        int limit = MyPreferences.getAmountLimit(getApplicationContext());
        amountLimitET.setText(limit + "");

        saveButton.setOnClickListener(view -> {

            String limitStr = amountLimitET.getText().toString();
            if (!limitStr.equals("")) {
                int limitNew = Integer.parseInt(limitStr);
                MyPreferences.setAmountLimit(limitNew, getApplicationContext());
                finish();
            } else {
                Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show();
            }

        });
    }
}