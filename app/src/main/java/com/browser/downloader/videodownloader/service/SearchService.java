package com.browser.downloader.videodownloader.service;

import android.os.AsyncTask;
import android.text.Html;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchService extends AsyncTask<String, Integer, List<String>> {

    private SuggestionCallback mSuggestionCallback;

    public SearchService(SuggestionCallback suggestionCallback) {
        this.mSuggestionCallback = suggestionCallback;
    }

    @Override
    protected List<String> doInBackground(String... params) {
        URL url;
        HttpURLConnection urlConnection = null;
        List<String> suggestions = new ArrayList<>();

        try {
            url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == 500) {
                return null;
            }

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            JSONArray jsonArray = new JSONArray(convertStreamToString(in));
            JSONArray jsonArray2 = jsonArray.getJSONArray(1);
            for (int i = 0; i < jsonArray2.length(); i++) {
                JSONArray jsonArray3 = jsonArray2.getJSONArray(i);
                suggestions.add(Html.fromHtml(jsonArray3.getString(0))
                        .toString());
            }
            return suggestions;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<String> suggestions) {
        if (suggestions != null && mSuggestionCallback != null) {
            mSuggestionCallback.onCompleted(suggestions);
        }
    }

    private static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public interface SuggestionCallback {
        void onCompleted(List<String> suggestions);
    }

}