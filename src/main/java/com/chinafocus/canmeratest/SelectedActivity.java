package com.chinafocus.canmeratest;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SelectedActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected);

        Toolbar toolbar = findViewById(R.id.toolbar);
        //点击左边返回按钮监听事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.bt_two).setOnClickListener(this);
        findViewById(R.id.bt_three).setOnClickListener(this);
        findViewById(R.id.bt_four).setOnClickListener(this);
        findViewById(R.id.bt_five).setOnClickListener(this);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_two:
                GameActivity.startGameActivity(this, 2);
                break;
            case R.id.bt_three:
                GameActivity.startGameActivity(this, 3);
                break;
            case R.id.bt_four:
                GameActivity.startGameActivity(this, 4);
                break;
            case R.id.bt_five:
                GameActivity.startGameActivity(this, 5);
                break;
        }
    }
}
