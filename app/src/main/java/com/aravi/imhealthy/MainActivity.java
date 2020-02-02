package com.aravi.imhealthy;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

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
        setSupportActionBar(mTitleBar);


        mLoadLayout.startShimmer();

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
                break;
            case R.id.main_notifications:
                break;
            default:
                break;
        }
        return true;
    }
}
