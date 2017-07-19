package com.android.newsapp;

import android.app.LoaderManager;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NewsItem>>, SwipeRefreshLayout.OnRefreshListener {

    // The ProgressBar to be shown while data is downloading
    private ProgressBar progressBar;
    // Declaring the static Loader ID
    private static final int NEWS_LOADER_ID = 1;
    // The static API key
    private static final String GUARDIAN_API_KEY = "95c97378-ec53-47e4-b7b3-f35617f98388";
    // The API URL for all news items
    private static final String GUARDIAN_NEWS_ALL = "https://content.guardianapis.com/search?api-key=";
    // The API URL
    private String apiUrl;
    // Declaring the list
    public static List<NewsItem> listNews;
    // Declaring the adapter
    private NewsAdapter listAdapter;
    // Declaring the SwipeRefreshLayout
    private SwipeRefreshLayout mSwipeRefreshLayout;
    // The network manager
    public NetworkInfo activeNetwork;
    public TextView noItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Activate SwipeRefreshLayout feature so news list is updated when screen in swiped
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Declaring the empty list TextView and setting the initial visibility
        noItems = (TextView) findViewById(R.id.empty_text_view);
        noItems.setVisibility(View.GONE);

        // Declaring the ProgressBar and setting the initial visibility
        progressBar = (ProgressBar) findViewById(R.id.loading_spinner);

        // Declaring the RecyclerView
        RecyclerView recList = (RecyclerView) findViewById(R.id.news_recycler_view);
        // setting up the built in LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        // Setting up the adapter
        listNews = new ArrayList<>();
        listAdapter = new NewsAdapter(listNews);
        recList.setAdapter(listAdapter);

        // Checks for network connectivity
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        //Check if there is internet connection. If not, no need to do the background task
        if (isConnected) {

            // Use LoaderManager to make sure AsyncTask is not recreated if activity is stopped
            final LoaderManager loaderManager = getLoaderManager();

            // Create the initial API query for all news items
            apiUrl = GUARDIAN_NEWS_ALL + GUARDIAN_API_KEY;
            // Start loading the information in a background task
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            Toast.makeText(MainActivity.this, R.string.no_internet,
                    Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            noItems.setVisibility(View.VISIBLE);
            noItems.setText(getString(R.string.no_internet));
        }
    }

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int i, Bundle args) {
        return new NewsLoader(this, apiUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> data) {
        if (activeNetwork != null && activeNetwork.isConnected()) {
            mSwipeRefreshLayout.setRefreshing(false);
            listAdapter.clear();
            listAdapter.addAll(data);
            progressBar.setVisibility(View.GONE);
        } else {
            noItems.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        listAdapter.clear();
    }

    @Override
    public void onRefresh() {
        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
    }
}
