package com.sodiumtracker.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.sodiumtracker.R;
import com.sodiumtracker.database.AppDatabase;
import com.sodiumtracker.models.ScheduleModel;
import com.sodiumtracker.ui.calendar.adapters.ScheduleRecyclerViewAdapter;
import com.sodiumtracker.utils.DatesUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment implements View.OnClickListener {

    Calendar dateToShow, firstDayOfMonthCal;
    String monthText;
    TextView monthTextTextView;
    int numberOfDays;
    RecyclerView scheduleRecyclerView;
    ScheduleRecyclerViewAdapter scheduleRecyclerViewAdapter;
    ImageView goBackOneMonth, goForwardOneMonth;

    AppDatabase db;

    public CalendarFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = Room.databaseBuilder(
                getContext(),
                AppDatabase.class, "app_database"
        ).allowMainThreadQueries().build();

        monthTextTextView = view.findViewById(R.id.monthTextTextView);
        goBackOneMonth = view.findViewById(R.id.goBackOneMonth);
        goForwardOneMonth = view.findViewById(R.id.goForwardOneMonth);
        scheduleRecyclerView = view.findViewById(R.id.scheduleRecyclerView);
        goBackOneMonth.setOnClickListener(this);
        goForwardOneMonth.setOnClickListener(this);
        dateToShow = Calendar.getInstance();
        firstDayOfMonthCal = Calendar.getInstance();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        scheduleRecyclerView.setLayoutManager(layoutManager);
        scheduleRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public void onResume() {
        super.onResume();
        setScheduleView();
    }

    public void setScheduleView() {
        firstDayOfMonthCal.set(dateToShow.get(Calendar.YEAR), dateToShow.get(Calendar.MONTH), 1);
        numberOfDays = dateToShow.getActualMaximum(Calendar.DAY_OF_MONTH);
        monthText = DatesUtils.getMonthText(dateToShow);
        monthTextTextView.setText(monthText);
        int firstDayOfMonth = firstDayOfMonthCal.get(Calendar.DAY_OF_WEEK);
        List<ScheduleModel> scheduleModels = new ArrayList<ScheduleModel>();
        for (int i = 0; i < firstDayOfMonth - 1; i++) {
            ScheduleModel scheduleModel = new ScheduleModel();
            scheduleModel.setDate(false);
            scheduleModels.add(scheduleModel);
        }
        for (int i = 0; i < numberOfDays; i++) {
            ScheduleModel scheduleModel = new ScheduleModel();
            Calendar calendar = Calendar.getInstance();
            calendar.set(dateToShow.get(Calendar.YEAR), dateToShow.get(Calendar.MONTH), i + 1);
            scheduleModel.setCalendar(calendar);
            scheduleModels.add(scheduleModel);

        }

        scheduleRecyclerViewAdapter = new ScheduleRecyclerViewAdapter(scheduleModels, getContext(),db);
        scheduleRecyclerView.setAdapter(scheduleRecyclerViewAdapter);


    }

    @Override
    public void onClick(View v) {

        if (v == goBackOneMonth) {
            dateToShow.add(Calendar.MONTH, -1);
            setScheduleView();
        } else if (v == goForwardOneMonth) {
            dateToShow.add(Calendar.MONTH, +1);
            setScheduleView();
        }
    }
}