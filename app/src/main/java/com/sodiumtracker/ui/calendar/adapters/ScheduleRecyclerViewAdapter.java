package com.sodiumtracker.ui.calendar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sodiumtracker.R;
import com.sodiumtracker.database.AppDatabase;
import com.sodiumtracker.models.ScheduleModel;
import com.sodiumtracker.utils.DatesUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleRecyclerViewAdapter.MyViewHolder> {

    private List<ScheduleModel> scheduleModels;
    private Context context;
    private AppDatabase db;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout parentItemLayout;
        public TextView dateText, itemIsToday, resultSumTV;

        public MyViewHolder(View view) {
            super(view);

            parentItemLayout = view.findViewById(R.id.parentItemLayout);
            dateText = view.findViewById(R.id.dateText);
            resultSumTV = view.findViewById(R.id.resultSumTV);
            itemIsToday = view.findViewById(R.id.itemIsToday);

        }
    }

    public ScheduleRecyclerViewAdapter(List<ScheduleModel> scheduleModels, Context context, AppDatabase db) {
        this.scheduleModels = scheduleModels;
        this.context = context;
        this.db = db;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ScheduleModel scheduleModel = scheduleModels.get(position);
        if (scheduleModel.isDate()) {
            if (DatesUtils.checkIf2CalendarsSameDay(Calendar.getInstance(), scheduleModel.getCalendar())) {
                holder.dateText.setTextColor(context.getResources().getColor(R.color.gree));
            }
            DateFormat dateFormat = new SimpleDateFormat("dd");
            String dateText = dateFormat.format(scheduleModel.getCalendar().getTime());
            holder.dateText.setText(dateText);

            Date today = scheduleModel.getCalendar().getTime();
            long startOfTodayMilli = DatesUtils.atStartOfDay(today).getTime();
            long endOfTodayMilli = DatesUtils.atEndOfDay(today).getTime();
            int sum = db.foodDao().getTotalAmountByDate(startOfTodayMilli, endOfTodayMilli);
            holder.resultSumTV.setText(sum + "mg");

        } else {

        }
    }

    @Override
    public int getItemCount() {
        return scheduleModels.size();
    }

}
