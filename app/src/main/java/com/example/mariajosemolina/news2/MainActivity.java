package com.example.mariajosemolina.news2;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    private ListView newsItems;
    private NewsAdapter newsAdapter;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;

    public static Boolean switchAuthor;
    public static Boolean switchTime ;
    public static Boolean switchPreview ;
    public static HashSet<String> sectionNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsAdapter = new NewsAdapter(this);
        newsItems = findViewById(R.id.newsItems);
        newsItems.setEmptyView(findViewById(R.id.empty_list_item));
        newsItems.setAdapter(newsAdapter);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open , R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        switchAuthor = sharedPref.getBoolean(SettingsActivity.KEY_PREF_AUTHOR_SWITCH, false);
        switchTime = sharedPref.getBoolean(SettingsActivity.KEY_PREF_TIME_SWITCH, false);
        switchPreview = sharedPref.getBoolean(SettingsActivity.KEY_PREF_PREVIEW_SWITCH, false);
        sectionNames = (HashSet) sharedPref.getStringSet(SettingsActivity.KEY_PREF_SECTION_NAMES, new HashSet<String>());

        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected){
            getLoaderManager().initLoader(1, null, loaderCallbacks);

        } else {
            Intent intent = new Intent(MainActivity.this, ErrorMessage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("error","Error: No connection");
            intent.putExtra("detail", "Try again, when your device has connection");
            this.startActivity(intent);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        switch(item.getItemId()){

            case R.id.nav_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private LoaderManager.LoaderCallbacks<JSONArray> loaderCallbacks = new LoaderManager.LoaderCallbacks<JSONArray>() {
        @Override
        public Loader<JSONArray> onCreateLoader(int id, Bundle args) {
            return new NewsLoader(getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<JSONArray> loader, JSONArray data) {
            if (data != null) {
                newsAdapter.swapData(data);
            } else if (data.length() != 0) {
                Toast.makeText(getBaseContext(), "No news to display, check your settings.", Toast.LENGTH_SHORT).show();

            } else {
                Intent intent = new Intent(MainActivity.this, ErrorMessage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("error","Empty or null data (check your settings)");
                intent.putExtra("detail", "There was a problem with the retrieved data, re-open the app and try again.");
                startActivity(intent);
            }
        }
        @Override
        public void onLoaderReset(Loader<JSONArray> loader) {
            newsAdapter.swapData(new JSONArray(new ArrayList<String>()));
        }
    };
}
