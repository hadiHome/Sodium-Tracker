package com.sodiumtracker.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
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

public class HomeFragment extends Fragment implements RecyclerViewTodayAdapter.Refresh {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    AdView mAdView;

    private ReviewManager manager;
    private ReviewInfo reviewInfo;
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

        //////Review
        manager = ReviewManagerFactory.create(getContext());
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                Toast.makeText(getContext(), "review  start ", Toast.LENGTH_SHORT);
                Log.d("TAG", "onCreateView: start" + task.toString());

                reviewInfo = task.getResult();

            } else {

                Toast.makeText(getContext(), "review failed to start ", Toast.LENGTH_SHORT);
                Log.d("TAG", "onCreateView: failed");
                // There was some problem, log or handle the error code.
//                @ReviewErrorCode int reviewErrorCode = ((TaskException) task.getException()).getErrorCode();
            }
        });


        ((MainActivity) getActivity()).setFragmentRefreshListener(new MainActivity.FragmentRefreshListener() {
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

        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView = view.findViewById(R.id.adView);
        if (MyPreferences.isAdsRemoved(getContext())) {
            mAdView.setVisibility(View.GONE);
        } else {
            mAdView.loadAd(adRequest);
        }

        binding.todayRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        if (MyPreferences.isAdsRemoved(getContext())) {

        } else {
            InterstitialAd.load(getActivity(), "ca-app-pub-9624523949741017/9650668273", adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                            Log.i("TAG", "onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.i("TAG", loadAdError.getMessage());
                            mInterstitialAd = null;
                        }
                    });
        }


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                Intent intent = new Intent(getApplicationContext(), AddSodiumActivity.class);
//                startActivity(intent);


                AddSodiumDialog cdd = new AddSodiumDialog(getActivity(), -1, "hi");
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.show();

                cdd.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                cdd.setOnDismissListener(dialog -> {
                    Task<Void> flow = manager.launchReviewFlow(getActivity(), reviewInfo);
                    flow.addOnCompleteListener(task -> {
                        Log.d("TAG", "onClick: " + reviewInfo.toString());
                        Toast.makeText(getContext(), "start ", Toast.LENGTH_SHORT);
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                    });

                    if (MyPreferences.isAdsRemoved(getContext())) {

                    } else {
                        showAd();
                    }
                    setLayout();


                    ///////////

                });


            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        setLayout();

    }

    public void setLayout() {
        Date today = Calendar.getInstance().getTime();
        long startOfTodayMilli = DatesUtils.atStartOfDay(today).getTime();
        long endOfTodayMilli = DatesUtils.atEndOfDay(today).getTime();
        int sum = db.foodDao().getTotalAmountByDate(startOfTodayMilli, endOfTodayMilli);


        int limit = MyPreferences.getAmountLimit(getContext());
        binding.amountLimitTv.setText("" + limit);
        binding.limitLayout.setOnClickListener(new View.OnClickListener() {
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

        RecyclerViewTodayAdapter recyclerViewTodayAdapter = new RecyclerViewTodayAdapter(db.foodDao().getByDate(startOfTodayMilli, endOfTodayMilli), this);
        binding.todayRecyclerView.setAdapter(recyclerViewTodayAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void showAd() {
        if (mInterstitialAd != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor ed = prefs.edit();
            ed.commit();
            mInterstitialAd.show(getActivity());
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    @Override
    public void refreshing() {
        setLayout();
    }
}