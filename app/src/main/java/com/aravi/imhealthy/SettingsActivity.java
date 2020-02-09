package com.aravi.imhealthy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.webianks.easy_feedback.EasyFeedback;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        mToolbar = findViewById(R.id.toolbar_setting);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        setSupportActionBar(mToolbar);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);


            Preference feedback = findPreference("feedback");
            Preference contact = findPreference("contact");
            SwitchPreference notifications = findPreference("notifications");
            Preference update = findPreference("update");
            Preference rate = findPreference("rate");


            Objects.requireNonNull(feedback).setOnPreferenceClickListener(preference -> {
                new EasyFeedback.Builder(getContext())
                        .withEmail("kamaravichow@gmail.com")
                        .withSystemInfo()
                        .build()
                        .start();
                return false;
            });

            Objects.requireNonNull(contact).setOnPreferenceClickListener(preference -> {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","contact.24ac@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact : ");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, I am ");
                startActivity(Intent.createChooser(emailIntent, "Drop an Email ..."));

                return false;
            });



            Objects.requireNonNull(update).setOnPreferenceClickListener(preference -> {
                String url = "https://play.google.com/store/apps/details?id=com.aravi.imhealthy";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return false;
            });

            Objects.requireNonNull(rate).setOnPreferenceClickListener(preference -> {
                String url = "https://play.google.com/store/apps/details?id=com.aravi.imhealthy";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return false;
            });

        }
    }

}