package com.sodiumtracker.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.sodiumtracker.database.AppDatabase;
import com.sodiumtracker.databinding.FragmentHomeBinding;
import com.sodiumtracker.ui.home.adapters.RecyclerViewTodayAdapter;
import com.sodiumtracker.utils.DatesUtils;

import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    AppDatabase db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        db = Room.databaseBuilder(
                getContext(),
                AppDatabase.class, "app_database"
        ).allowMainThreadQueries().build();

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.todayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @Override
    public void onResume() {
        super.onResume();

        Date today = Calendar.getInstance().getTime();
        long startOfTodayMilli = DatesUtils.atStartOfDay(today).getTime();
        long endOfTodayMilli = DatesUtils.atEndOfDay(today).getTime();
        int sum = db.foodDao().getTotalAmountByDate(startOfTodayMilli, endOfTodayMilli);

        binding.totalAmountTv.setText("total: " + sum);

        RecyclerViewTodayAdapter recyclerViewTodayAdapter = new RecyclerViewTodayAdapter(db.foodDao().getByDate(startOfTodayMilli,endOfTodayMilli));
        binding.todayRecyclerView.setAdapter(recyclerViewTodayAdapter);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}