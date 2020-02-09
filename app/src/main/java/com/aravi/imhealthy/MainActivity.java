package com.aravi.imhealthy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aravi.imhealthy.Account.AccountActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    Toolbar mTitleBar;
    ShimmerFrameLayout mLoadLayout;
    LinearLayoutManager manager;
    PostAdapter adapter;
    List<Item> items = new ArrayList<>();
    Boolean isScrolling = false;
    RelativeLayout mMainLayout;
    int currentItems, totalItems, scrollOutItems;
    String token = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressLoader);
        mLoadLayout = findViewById(R.id.shimmer_view_container);
        recyclerView = findViewById(R.id.postList);
        mTitleBar = findViewById(R.id.titlebar);
        mMainLayout = findViewById(R.id.main_layout);

        setSupportActionBar(mTitleBar);

        MobileAds.initialize(this, getResources().getString(R.string.mobileads_app_id));

        /*
        Drawable drawable = getResources().getDrawable(R.drawable.icon_transp);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Drawable finalDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 70, 70, true));
        mTitleBar.setLogo(finalDrawable);*/

        mLoadLayout.startShimmer();


        // Admob Banner------------------------------------------------------------------------
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.i("adInit", ":" + initializationStatus);
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        manager = new LinearLayoutManager(this);
        adapter = new PostAdapter(this, items);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    getData();
                }
            }
        });
        getData();

    }

    private void getData() {

        String url = BloggerAPI.url + "?key=" + BloggerAPI.key;
        if (token != "") {
            url = url + "&pageToken=" + token;
        }
        if (token == null) {
            return;
        }
        Call<PostList> postList = BloggerAPI.getService().getPostList(url);
        postList.enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                PostList list = response.body();
                token = list.getNextPageToken();
                items.addAll(list.getItems());
                adapter.notifyDataSetChanged();
                mLoadLayout.stopShimmer();
                mLoadLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {
                Toast.makeText(MainActivity.this, "NO Internet Connection...", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_challenge:
                Intent i = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(i);
                break;
            case R.id.main_settings:
                Intent s = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(s);
                break;

            case R.id.main_privacy:
                break;

            case R.id.main_feedback:
                break;
            default:
                break;
        }
        return true;
    }


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Snackbar.make(mMainLayout, "Do you really want to exit?", Snackbar.LENGTH_LONG)
                    .setAction("YES EXIT", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            System.exit(0);
                        }
                    })
                    .setActionTextColor(Color.WHITE)
                    .show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
}