package com.example.mariajosemolina.news2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.util.ArrayList;


public class NewsAdapter extends BaseAdapter {
    private JSONArray data = null;
    private LayoutInflater layoutInflater;
    private TextView title;
    private TextView date;
    private TextView section;
    private TextView author;
    private TextView preview;

    Context context;

    public NewsAdapter(Context context) {
        this.context = context;
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (data != null)
            return data.length();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        try {
            return data.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.news_item,parent, false);
        } else {
            view = convertView;
        }


        title = view.findViewById(R.id.title);
        date = view.findViewById(R.id.date);
        section = view.findViewById(R.id.section);
        author = view.findViewById(R.id.author);
        preview = view.findViewById(R.id.preview);


        // Empty variables to get the information from the JSON
        String webTitle = "";
        String webPublicationDate = "";
        String sectionName = "";
        String webUrl = "";
        String webAuthor = "";
        String previewBodyText = "";


        JSONObject finalObject = null;
        try {
            finalObject = data.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            webTitle = finalObject.optString("webTitle");
            webPublicationDate = finalObject.optString("webPublicationDate");
            sectionName = finalObject.optString("sectionName");
            webUrl = finalObject.optString("webUrl");

            //Extract the JSONArray with the key "tag"
            JSONArray tagsArray = finalObject.getJSONArray("tags");

            // Check if tag array has some content
            if (tagsArray.length() == 1) {
                JSONObject contributorTag = (JSONObject) tagsArray.get(0);
                // The author name field is inside the tag webTitle from the array Tags
                webAuthor = contributorTag.optString("webTitle");
            }

            // Parsing the preview text only if the option is selected
            if (MainActivity.switchPreview) {

                JSONObject fields = (JSONObject) finalObject.get("fields");
                if (fields != null) {
                    String bodyText = fields.optString("bodyText");

                    // Getting the first 100 words of the bodyText
                    String[] bodyTextSplitted = bodyText.split("\\s+");

                    // Concatenating number of words that you required
                    for (int i = 0; i < 100; i++) {
                        previewBodyText = previewBodyText + " " + bodyTextSplitted[i];
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Handling the date
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date D = new Date();
        try {
            D = dateFormatter.parse(webPublicationDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat df;
        if (MainActivity.switchTime) {
            df = new SimpleDateFormat("dd MMM yyyy HH:mm");
        } else {
            df = new SimpleDateFormat("dd MMM yyyy");
        }

        String reportDate = df.format(D);
        if (webTitle != null) {
            title.setText(webTitle);
        }
        if (reportDate != null) {
            date.setText(reportDate);
        }
        if (sectionName != null) {
            section.setText(sectionName);
        }
        if (webAuthor != null) {
            // Only showing the author if the option is selected.
            if (MainActivity.switchAuthor)
                author.setText(webAuthor);
        }
        if (previewBodyText != "" && MainActivity.switchPreview) {
            preview.setText(previewBodyText+"...");
        }

        if (webUrl != null) {
            // Adding an OnClickListener so once the user click on the news (the whole item)
            // it will open the news URL
            final String finalWebUrl = webUrl;
            view.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                  Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(finalWebUrl));
                  context.startActivity(i);
               }
            });
        }

        return view;

    }

    public void swapData(JSONArray data) {
        this.data  = new JSONArray(new ArrayList<String>());
        this.data = data;
        notifyDataSetChanged();
    }
}
