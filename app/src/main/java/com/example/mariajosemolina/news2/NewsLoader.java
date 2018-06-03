package com.example.mariajosemolina.news2;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NewsLoader extends AsyncTaskLoader<JSONArray>  {

    // Tag for the log messages
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    Context context;

    public NewsLoader(Context context) {
        super(context);
        this.context = context;
    }
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public JSONArray loadInBackground() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        JSONArray results = null;

        try {
            URL url = new URL(getContext().getResources().getString(R.string.THEGUARDIAN_API));
            connection = (HttpURLConnection) url.openConnection();

            connection.connect();
            String line;

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                final String a = buffer.toString();
                JSONObject parentObject = new JSONObject(a);
                JSONObject response = parentObject.getJSONObject("response");
                results = response.getJSONArray("results");
            }

            else {
                Intent intent = new Intent(this.context, ErrorMessage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("error","Invalid HTTP response");
                intent.putExtra("detail", "Please make sure the URL is correct.");
                this.context.startActivity(intent);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL", e);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error with making connection", e);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON results", e);

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem closing the buffer reader.", e);
            }
        }

        /******************************
         *     Filter the data
         ******************************/

        // Only if some categories are selected in the settings we filter the news
        if (MainActivity.sectionNames.size() > 0) {
            // New JSONArray to save the news that we want to show
            JSONArray list = new JSONArray();

            // Iterate every JSONOBject from the previous results.
            for (int i = 0; i < results.length(); i++) {
                JSONObject element = null;
                try {
                    element = results.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Check if the sectionName is inside our preferences sectionNames
                // to filter the data before it goes to the NewsAdapter

                // We first get the sectionName of the news (JSONObject)
                String sectionName = element.optString("sectionName");
                // Then we construct the category as we have define in our strings.xml
                // Replacing the spaces by "-" and adding a "-checked" at the end,
                // because in that way we can simple see if this sectionName is inside the set
                // of categories the user has selected.
                String tmpSectionName = sectionName.toLowerCase().replaceAll("\\s+", "-") + "-checked";
                if (MainActivity.sectionNames.contains(tmpSectionName)) {
                    try {
                        list.put(results.get(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return list;
        }
        // Otherwise we return the news without filtering them
        else {
            return results;
        }
    }
}
