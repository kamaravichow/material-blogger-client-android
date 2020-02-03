package com.aravi.imhealthy;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.mapzen.speakerbox.Speakerbox;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Locale;

import static android.view.WindowManager.LayoutParams;

public class ReadActivity extends AppCompatActivity {

    public static boolean onStartedSpeaking = false;

    ImageView mMainInage;
    TextView mContent, mTitle;
    ImageView mShare, mSpeak, mBrowser;
    Speakerbox speakerbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_read);

        mContent = findViewById(R.id.content_read);
        mTitle = findViewById(R.id.title_read);
        mMainInage = findViewById(R.id.image_read);
        mShare = findViewById(R.id.share_read);
        mSpeak = findViewById(R.id.tts_read);
        mBrowser = findViewById(R.id.web_read);

        speakerbox = new Speakerbox(getApplication());
        mBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getIntent().getStringExtra("url"))));

            }
        });
        mSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Document document = Jsoup.parse(getIntent().getStringExtra("content"));
                if (onStartedSpeaking){
                    speakerbox.stop();
                }else {
                    speakerbox.play(document.text());
                    onStartedSpeaking  = true;
                }

            }
        });


        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareApp();
            }
        });


        Glide.with(this).load(getIntent().getStringExtra("img")).into(mMainInage);

        mTitle.setText(getIntent().getStringExtra("title"));
        //Document document = Jsoup.parse();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mContent.setText(Html.fromHtml(getIntent().getStringExtra("content"), Html.FROM_HTML_MODE_COMPACT));
        } else {
            mContent.setText(Html.fromHtml(getIntent().getStringExtra("content")));
        }
        Linkify.addLinks(mContent, Linkify.WEB_URLS);

    }

    private void ShareApp(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Hey ! I just found an awesome article on I'm healthy app click the link to download now : https://imhealthy.page.link/main";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share Article");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (onStartedSpeaking){
            speakerbox.stop();
            onStartedSpeaking = false;
        }
    }

}
