package com.sodiumtracker.ui.shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.sodiumtracker.MyPreferences;
import com.sodiumtracker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {

    BillingClient billingClient;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;

    Button remove_ads_button;
    TextView remove_ads_title_TV;

    public ShopFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static ShopFragment newInstance(String param1, String param2) {
        ShopFragment fragment = new ShopFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        remove_ads_button = view.findViewById(R.id.remove_ads_button);
        remove_ads_title_TV = view.findViewById(R.id.remove_ads_title_TV);

        PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchases) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                // Handle any other error codes.
            }
        };

        billingClient = BillingClient.newBuilder(getActivity())
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();


        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    List<String> skuList = new ArrayList<>();
                    skuList.add("remove_ads");
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                                    setButtons(list);
                                }
                            }
                    );
                    billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, (billingResult12, list) -> {
                        for (int i = 0; i < list.size(); i++) {
                            handlePurchase(list.get(i));
                        }
                    });

                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });

    }


    void handlePurchase(Purchase purchase) {
        // Purchase retrieved from BillingClient#queryPurchasesAsync or your PurchasesUpdatedListener.
//        Purchase purchase = ...;

        List<String> skus = purchase.getSkus();
        String jsonPurchase = purchase.getOriginalJson();
        final JSONObject objPurchase;
        String productId = "";

        String orderId = "";
        String packageName = "";
        String purchaseTime = "";
        String purchaseState = "";
        String purchaseToken = "";
        String quantity = "";
        String acknowledged = "";

        try {
            objPurchase = new JSONObject(jsonPurchase);
            productId = objPurchase.getString("productId");
            orderId = objPurchase.getString("orderId");
            packageName = objPurchase.getString("packageName");
            purchaseTime = objPurchase.getString("purchaseTime");
            purchaseState = objPurchase.getString("purchaseState");
            purchaseToken = objPurchase.getString("purchaseToken");
            quantity = objPurchase.getString("quantity");
            acknowledged = objPurchase.getString("acknowledged");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        }

        if (productId.equals("remove_ads")) {

            MyPreferences.removeAds(getContext());

            return;
        }


    }

    public void setButtons(List<SkuDetails> skuDetailsList) {

        for (int i = 0; i < skuDetailsList.size(); i++) {


            SkuDetails skuDetail = skuDetailsList.get(i);

            String productId = skuDetail.getSku();
            String title = skuDetail.getTitle();
            String description = skuDetail.getDescription();
            String price = skuDetail.getPrice();

            if (productId.equals("remove_ads")) {
                getActivity().runOnUiThread(() -> {
                    remove_ads_button.setVisibility(View.VISIBLE);
                    remove_ads_title_TV.setVisibility(View.VISIBLE);

                    remove_ads_button.setText(price);
                    String shortTitle = title.substring(0, title.lastIndexOf(" ("));
                    remove_ads_title_TV.setText(shortTitle);
                    remove_ads_button.setOnClickListener(v -> {

                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetail)
                                .build();
                        int responseCode = billingClient.launchBillingFlow(getActivity(), billingFlowParams).getResponseCode();
                        switch (responseCode) {
                            case BillingClient.BillingResponseCode.OK:

                                break;
                        }
                    });
                });

            }
        }

    }
}