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


    public static void removeAds(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sodium", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("removeads", 1).commit();
    }

    public static boolean isAdsRemoved(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sodium", Context.MODE_PRIVATE);
        int boolInt = sharedPreferences.getInt("removeads", 0);
        if (boolInt == 1) {
            return true;
        }
        return false;

    }


}
