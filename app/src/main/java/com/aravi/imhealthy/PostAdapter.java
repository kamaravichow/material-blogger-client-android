package com.aravi.imhealthy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

import retrofit2.http.Url;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Item> items;


    public PostAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.main_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        final Item item = items.get(position);
        holder.postTitle.setText(item.getTitle());

        Document document = Jsoup.parse(item.getContent());

        Elements elements = document.select("img");

        if (!elements.get(0).attr("src").isEmpty()){
            holder.postImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(elements.get(0).attr("src")).into(holder.postImage);
        }
        else {
            holder.postImage.setVisibility(View.GONE);
        }

        //int POSITION = position;


        String dpUrl = item.getAuthor().getImage().getUrl();

        holder.postSource.setText(item.getAuthor().getDisplayName());

        holder.postPublished.setText(item.getUpdated());
        holder.postCount.setText(Integer.toString(position + 1));

        holder.postOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] reports = {"Share", "Go to article", "Report an issue"};


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("More options :");
                builder.setItems(reports, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 1) {
                        }

                        if (which == 2) {
                        }

                        if (which == 3) {
                        }

                    }
                });
                builder.show();
            }
        });

        /*

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("url", item.getUrl());
                context.startActivity(intent);
            }
        });

        */

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        TextView postTitle;
        TextView postSource;
        TextView postCount;
        TextView postPublished;
        ImageView postOptions;

        public PostViewHolder(View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.mainImage);
            postTitle = itemView.findViewById(R.id.tv_title);
            postSource = itemView.findViewById(R.id.tv_source);
            postCount = itemView.findViewById(R.id.tv_postCount);
            postPublished = itemView.findViewById(R.id.tv_published_date);
            postOptions = itemView.findViewById(R.id.postOptions);
        }
    }
}