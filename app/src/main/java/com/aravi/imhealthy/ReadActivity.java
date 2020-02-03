package com.aravi.imhealthy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.w3c.dom.Text;

public class ReadActivity extends AppCompatActivity {
    Toolbar toolbar;
    CollapsingToolbarLayout collapse;

    ImageView mMainInage;
    TextView mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        toolbar= findViewById(R.id.toolbar);
        collapse= findViewById(R.id.collapseToolbar);
        mContent = findViewById(R.id.content_read);
        mMainInage = findViewById(R.id.image_read);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapse.setTitle("Name of Article");
        collapse.setCollapsedTitleTextColor(Color.parseColor("#FFFFFF"));
        collapse.setExpandedTitleColor(Color.parseColor("#FFFFFF"));
        collapse.setStatusBarScrimColor(Color.parseColor("#FFFFFF"));

        Glide.with(this).load(getIntent().getStringExtra("img")).into(mMainInage);
        mContent.setText(getIntent().getStringExtra("content"));
    }
}
