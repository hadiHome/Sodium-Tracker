package com.sodiumtracker.ui.chart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.sodiumtracker.R;
import com.sodiumtracker.database.AppDatabase;
import com.sodiumtracker.database.entity.Food;

import java.util.ArrayList;
import java.util.List;


public class AllDataChartFragment extends Fragment {

    AppDatabase db;
    LineChart lineChart;

    public AllDataChartFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        db = Room.databaseBuilder(
                getContext(),
                AppDatabase.class, "app_database"
        ).allowMainThreadQueries().build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_data_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChart = view.findViewById(R.id.chart);

        List<Food> foodList = db.foodDao().getAllGroupByDates();
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < foodList.size(); i++) {
            Food food = foodList.get(i);
            entries.add(new Entry(i, food.amount));
        }

        LineDataSet dataSet = new LineDataSet(entries, "amount(mg)"); // add entries to dataset
//        dataSet.setColor();
//        dataSet.setValueTextColor(...); // styling, ...

        dataSet.setLineWidth(2f);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(30f);
//        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                if (((int) value) < foodList.size()) {
//                    return (foodList.get((int) value).name);
//                } else {
//                    return "zz";
//                }
//            }
//        });
        xAxis.setValueFormatter(new MyCustomXAxisFormatter(foodList));

        YAxis left = lineChart.getAxisLeft();
        left.setDrawLabels(true); // no axis labels
        left.setDrawAxisLine(true); // no axis line
        left.setDrawGridLines(false); // no grid lines
        left.setDrawZeroLine(true); // draw a zero line
//        left.setValueFormatter(new MyCustomYAcisFormatter());

//        lineChart.getLegend().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false); // no right axis
        lineChart.getDescription().setEnabled(false);
        lineChart.getData().setHighlightEnabled(false);
        lineChart.invalidate();
    }
}