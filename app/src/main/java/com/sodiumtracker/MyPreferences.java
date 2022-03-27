package com.sodiumtracker;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferences {

    public static void setAmountLimit(int amount, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sodium", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("amountlimit", amount).commit();
    }

    public static int getAmountLimit(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sodium", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("amountlimit", 500);
    }


}
