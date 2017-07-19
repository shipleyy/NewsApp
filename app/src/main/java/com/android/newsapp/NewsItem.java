package com.android.newsapp;

public class NewsItem {
    protected String author, headline, category, dateTime, url;

    public NewsItem(String author, String headline, String category, String dateTime, String url) {
        this.author = author;
        this.headline = headline;
        this.category = category;
        this.dateTime = dateTime;
        this.url = url;
    }
}