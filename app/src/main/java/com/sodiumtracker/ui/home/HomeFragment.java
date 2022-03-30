package com.sodiumtracker.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.room.Room;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.sodiumtracker.MainActivity;
import com.sodiumtracker.MyPreferences;
import com.sodiumtracker.R;
import com.sodiumtracker.activities.AddSodiumDialog;
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
    AdView mAdView;

    AppDatabase db;
    private InterstitialAd mInterstitialAd;

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


        ((MainActivity)getActivity()).setFragmentRefreshListener(new MainActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh() {



                binding.todayRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);





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