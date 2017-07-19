package com.android.newsapp;


import android.text.TextUtils;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DataHandler {

    // GUARDIAN API keys for the necessary information
    private static final String GUARDIAN_KEY_RESPONSE = "response";
    private static final String GUARDIAN_KEY_RESULTS = "results";
    private static final String GUARDIAN_KEY_SECTION = "sectionName";
    private static final String GUARDIAN_KEY_PUBLISHED_DATE = "webPublicationDate";
    private static final String GUARDIAN_KEY_TITLE = "webTitle";
    private static final String GUARDIAN_KEY_WEBURL = "webUrl";

    private static final String LOG_TAG = DataHandler.class.getName();

    private DataHandler() {
    }

    public static List<NewsItem> getNewsData(String requestUrl) {

        //Creating the URL via the createUrl method
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem connecting", e);
        }

        // Extract relevant fields from the JSON response and create an {@link NewsItem} object
        return parseNews(jsonResponse);
    }

    // Create the URL via this method
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    // Open the HTTP connection to the server
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            Log.v(LOG_TAG, "URL response code: " + urlConnection.getResponseCode());

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<NewsItem> parseNews(String jsonResponse) {

        // If JSON string is empty or null, return
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<NewsItem> newsList = new ArrayList<>();

        // Try to parse the jsonResponse. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.

        try {

            // Create a JSONObject from the JSON response string
            JSONObject root = new JSONObject(jsonResponse);

            // Open the JSONObject containing all the response data
            JSONObject allData = root.getJSONObject(GUARDIAN_KEY_RESPONSE);

            // Open the array of news items
            JSONArray allNewsArray = allData.getJSONArray(GUARDIAN_KEY_RESULTS);

            // For all news items create an NewsItem object
            for (int i = 0; i < allNewsArray.length(); i++) {
                JSONObject currentNewsItem = allNewsArray.getJSONObject(i);

                // Get the title / headline
                String newsHeadline = currentNewsItem.getString(GUARDIAN_KEY_TITLE);

                // Get the section / category that the news belongs
                String newsCategory = currentNewsItem.getString(GUARDIAN_KEY_SECTION);

                // Get the date the news was published
                String newsDate = currentNewsItem.getString(GUARDIAN_KEY_PUBLISHED_DATE);

                // Get the URL for the direct link to the content
                String newsUrl = currentNewsItem.getString(GUARDIAN_KEY_WEBURL);

                // Save all the data in a new NewsItem object
                NewsItem newsItem = new NewsItem(newsHeadline, newsHeadline, newsCategory, newsDate, newsUrl);

                // Add the newsItem to the ArrayList
                newsList.add(newsItem);
            }

            Log.v("DataHandler", "Length of array" + allNewsArray.length());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newsList;
    }
}
