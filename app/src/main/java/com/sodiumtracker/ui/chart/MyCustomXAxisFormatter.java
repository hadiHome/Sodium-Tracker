package com.sodiumtracker.ui.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.sodiumtracker.database.entity.Food;

import java.text.SimpleDateFormat;
import java.util.List;

public class MyCustomXAxisFormatter extends IndexAxisValueFormatter {

    List<Food> foodList;

    public MyCustomXAxisFormatter(List<Food> foodList) {
        this.foodList = foodList;
    }

    @Override
    public String getFormattedValue(float value) {
        if (Math.ceil(value) == value) {
            String dateWithFormat;
            try {
                dateWithFormat = (new SimpleDateFormat("MM-dd")).format(foodList.get((int) value).date);
            } catch (Exception e) {
                return "";
            }
            return dateWithFormat;
        } else {
            return "";
        }

    }

}
