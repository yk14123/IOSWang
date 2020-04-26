package com.chinafocus.canmeratest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    private MyAdapter myAdapter;
    private RecyclerView recyclerView;
    // TODO 4代表4X4，同理2代表2X2
    private int[] spans = {2, 3, 4, 5};
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_home);

        int ran = (int) (Math.random() * spans.length);
        gridLayoutManager = new GridLayoutManager(this, spans[ran]);
        recyclerView.setLayoutManager(gridLayoutManager);
        myAdapter = new MyAdapter(spans[ran] * spans[ran]);
        recyclerView.setAdapter(myAdapter);

    }

    public void recover(View view) {
        myAdapter.clear();
        int ran = (int) (Math.random() * spans.length);
        gridLayoutManager.setSpanCount(spans[ran]);
        myAdapter = new MyAdapter(spans[ran] * spans[ran]);
        recyclerView.setAdapter(myAdapter);
    }
}
