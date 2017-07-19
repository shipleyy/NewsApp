package com.android.newsapp;


import android.content.Context;

import java.util.List;

public class NewsLoader extends android.content.AsyncTaskLoader<List<NewsItem>> {

    private String apiUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        apiUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<NewsItem> loadInBackground() {
        if (apiUrl == null) {
            return null;
        }
        return DataHandler.getNewsData(apiUrl);
    }
}
