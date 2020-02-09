package com.aravi.imhealthy;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.mapzen.speakerbox.Speakerbox;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ReadActivity extends AppCompatActivity {

    public static boolean onStartedSpeaking = false;

    ImageView mMainInage;
    TextView mContent, mTitle, mPublished, mCommentCount;
    ImageView mShare, mSpeak, mBrowser;
    Speakerbox speakerbox;
    Button mComment;

    private UnifiedNativeAd nativeAd;


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

        LoadWithDelay();

    }


    private void ShareApp() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Hey ! I just found an awesome article on I'm healthy app click the link to download now : https://imhealthy.page.link/main";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share Article");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }


    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);

    }

    private void refreshAd() {

        AdLoader.Builder builder = new AdLoader.Builder(this, getResources().getString(R.string.native_ad_unit));

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
                FrameLayout frameLayout =
                        findViewById(R.id.fl_adplaceholder);
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
                        .inflate(R.layout.ad_unified, null);
                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
            }

        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(false)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(ReadActivity.this, "Failed to load ad: "
                        + errorCode, Toast.LENGTH_SHORT).show();
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }


    private void LoadWithDelay() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshAd();
            }
        }, 1000);
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
