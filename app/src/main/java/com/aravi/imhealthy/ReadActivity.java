package com.aravi.imhealthy;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.mapzen.speakerbox.Speakerbox;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import static android.view.WindowManager.LayoutParams;

public class ReadActivity extends AppCompatActivity {

    public static boolean onStartedSpeaking = false;

    ImageView mMainInage;
    TextView mContent, mTitle, mPublished, mCommentCount;
    ImageView mShare, mSpeak, mBrowser;
    Speakerbox speakerbox;
    Button mComment;

    private final String TAG = ReadActivity.class.getSimpleName();
    private NativeAd nativeAd;

    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().setFlags(LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_read);

        mContent = findViewById(R.id.content_read);
        mTitle = findViewById(R.id.title_read);
        mMainInage = findViewById(R.id.image_read);
        mShare = findViewById(R.id.share_read);
        mSpeak = findViewById(R.id.tts_read);
        mBrowser = findViewById(R.id.web_read);
        mPublished = findViewById(R.id.published_read);
        mCommentCount = findViewById(R.id.comment_count);
        mComment = findViewById(R.id.addcommentbtn);


        //loadNativeAd();

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
                if (onStartedSpeaking) {
                    speakerbox.stop();
                } else {
                    speakerbox.play(document.text());
                    onStartedSpeaking = true;
                }

            }
        });

        mPublished.setText("Published on : " + getIntent().getStringExtra("date"));

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

        mCommentCount.setText(getIntent().getStringExtra("comments") + " Comments");
        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getIntent().getStringExtra("url") + "#comments")));
            }
        });

    }

    private void ShareApp() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Hey ! I just found an awesome article on I'm healthy app click the link to download now : https://imhealthy.page.link/main";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share Article");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }


    private void loadNativeAd() {
        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        nativeAd = new NativeAd(this, "198827961258971_198828124592288");

        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
                LoadWithDelay();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");

                // Race condition, load() called again before last ad was displayed
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
                LoadWithDelay();
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        });

        // Request an ad
        nativeAd.loadAd();
    }


    private void inflateAd(NativeAd nativeAd) {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeAdLayout = findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(ReadActivity.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(ReadActivity.this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }

    private void LoadWithDelay(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                loadNativeAd();
            }
        }, 5000);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (onStartedSpeaking) {
            speakerbox.stop();
            onStartedSpeaking = false;
        }
    }

}
