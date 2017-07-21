package com.android.newsapp;

class NewsItem {
    private final String headline;
    private final String category;
    private final String dateTime;
    private final String url;

    public NewsItem(String headline, String category, String dateTime, String url) {
        this.headline = headline;
        this.category = category;
        this.dateTime = dateTime;
        this.url = url;
    }

    public String getHeadline() {
        return headline;
    }

    public String getCategory() {
        return category;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getUrl() {
        return url;
    }
}