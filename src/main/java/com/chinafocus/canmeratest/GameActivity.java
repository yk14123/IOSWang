package com.chinafocus.canmeratest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class GameActivity extends AppCompatActivity {

    private MyAdapter myAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private InterstitialAd mInterstitialAd;

    private static final String TAG_TYPE = "tag_type";
    private int mCount;

    public static void startGameActivity(Context context, int count) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(TAG_TYPE, count);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
                    gameAgain();
                }
                return true;
            }
        });


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        initGoogleAdBanner();
        initGoogleAdInterstitial();

        mCount = getIntent().getIntExtra(TAG_TYPE, -1);
        recyclerView = findViewById(R.id.rv_home);
        gridLayoutManager = new GridLayoutManager(this, mCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        myAdapter = new MyAdapter(mCount * mCount, mInterstitialAd);
        recyclerView.setAdapter(myAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return true;
    }

    private void initGoogleAdInterstitial() {
        if (mInterstitialAd == null) {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-1017960427612207/1882136429");
        }
    }

    private void initGoogleAdBanner() {
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void gameAgain() {
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        myAdapter.clear();
        gridLayoutManager.setSpanCount(mCount);
        myAdapter = new MyAdapter(mCount * mCount, mInterstitialAd);
        recyclerView.setAdapter(myAdapter);
    }
}
