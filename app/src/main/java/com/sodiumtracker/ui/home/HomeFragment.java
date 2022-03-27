package com.sodiumtracker.ui.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.room.Room;

import com.sodiumtracker.MyPreferences;
import com.sodiumtracker.activities.ChangeLimitAmountActivity;
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

        binding.todayRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

    }

    @Override
    public void onResume() {
        super.onResume();

        Date today = Calendar.getInstance().getTime();
        long startOfTodayMilli = DatesUtils.atStartOfDay(today).getTime();
        long endOfTodayMilli = DatesUtils.atEndOfDay(today).getTime();
        int sum = db.foodDao().getTotalAmountByDate(startOfTodayMilli, endOfTodayMilli);

        int limit = MyPreferences.getAmountLimit(getContext());
        binding.amountLimitTv.setText("" + limit);
        binding.amountLimitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ChangeLimitAmountActivity.class));
            }
        });
        binding.totalAmountTv.setText("" + sum);
        int remaining = limit - sum;
        binding.remainingAmountTv.setText("" + remaining);

        int progress = 0;
        if (limit > 0) {
            progress = (sum * 100) / limit;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.progressIndicator.setProgress(progress, true);
        } else {
            binding.progressIndicator.setProgress(progress);
        }

        RecyclerViewTodayAdapter recyclerViewTodayAdapter = new RecyclerViewTodayAdapter(db.foodDao().getByDate(startOfTodayMilli, endOfTodayMilli));
        binding.todayRecyclerView.setAdapter(recyclerViewTodayAdapter);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}