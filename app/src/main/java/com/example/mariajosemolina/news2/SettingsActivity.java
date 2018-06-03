package com.example.mariajosemolina.news2;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_PREF_AUTHOR_SWITCH = "author_switch";
    public static final String KEY_PREF_TIME_SWITCH = "time_switch";
    public static final String KEY_PREF_PREVIEW_SWITCH = "preview_switch";
    public static final String KEY_PREF_SECTION_NAMES = "section_names";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Settings");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .commit();
    }
}
