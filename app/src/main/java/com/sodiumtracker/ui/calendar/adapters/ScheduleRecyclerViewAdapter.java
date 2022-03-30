package com.sodiumtracker.ui.calendar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sodiumtracker.R;
import com.sodiumtracker.models.ScheduleModel;
import com.sodiumtracker.utils.DatesUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ScheduleRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleRecyclerViewAdapter.MyViewHolder> {

    private List<ScheduleModel> scheduleModels;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout parentItemLayout;
        //        public TextView homeworkInCaledarCircle, projectInCaledarCircle, examInCaledarCircle, quizInCaledarCircle;
        public TextView dateText, itemIsToday;

        public MyViewHolder(View view) {
            super(view);

            parentItemLayout = (LinearLayout) view.findViewById(R.id.parentItemLayout);
            dateText = (TextView) view.findViewById(R.id.dateText);
            itemIsToday = (TextView) view.findViewById(R.id.itemIsToday);

        }
    }

    public ScheduleRecyclerViewAdapter(List<ScheduleModel> scheduleModels, Context context) {
        this.scheduleModels = scheduleModels;
        this.context = context;
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


        } else {

        }
    }

    @Override
    public int getItemCount() {
        return scheduleModels.size();
    }

}
