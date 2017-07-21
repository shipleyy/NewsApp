package com.android.newsapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_VIEW;
import static android.media.CamcorderProfile.get;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NewsItem>>, SwipeRefreshLayout.OnRefreshListener,
ItemClickListener {

    // The ProgressBar to be shown while data is downloading
    private ProgressBar progressBar;
    // Declaring the static Loader ID
    private static final int NEWS_LOADER_ID = 1;
    // The static API key
    private static final String GUARDIAN_API_KEY = "95c97378-ec53-47e4-b7b3-f35617f98388";
    private static final String GUARDIAN_API_AUTHORITY = "content.guardianapis.com";
    private static final String GUARDIAN_API_SEARCH = "search";
    private static final String GUARDIAN_API_BEFOREKEY = "api-key";
    // The API URL
    private String apiUrl;
    // Declaring the adapter
    private NewsAdapter mAdapter;
    // Declaring the SwipeRefreshLayout
    private SwipeRefreshLayout mSwipeRefreshLayout;
    // The network manager
    private NetworkInfo activeNetwork;
    // Declaring the noItems TextView
    private TextView noItems;
    // Declaring the noInternet TextView
    private TextView noInternet;
    // Declaring the RecyclerView
    private RecyclerView recList;
    // TODO - Declaring the list for onClickListener
    private List<NewsItem> newsItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Activate SwipeRefreshLayout feature so news list is updated when screen in swiped
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Declaring the noInternet TextView and setting the initial visibility
        noInternet = (TextView) findViewById(R.id.no_internet_textview);
        noInternet.setVisibility(View.GONE);

        // Declaring the empty list TextView
        noItems = (TextView) findViewById(R.id.no_items_textview);

        // Declaring the ProgressBar and setting the initial visibility
        progressBar = (ProgressBar) findViewById(R.id.loading_spinner);

        // Declaring the RecyclerView
        recList = (RecyclerView) findViewById(R.id.news_recycler_view);

        // Setting up the adapter
        mAdapter = new NewsAdapter(this, new ArrayList<NewsItem>());

        // setting up the built in LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        // Connecting the adapter to the list
        recList.setAdapter(mAdapter);

        // Adding a ClickListener to the adapter
        mAdapter.setClickListener(this);

        // Checks for network connectivity
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        //Check if there is internet connection. If not, no need to do the background task
        if (isConnected) {

            // Use LoaderManager to make sure AsyncTask is not recreated if activity is stopped
            final LoaderManager loaderManager = getLoaderManager();

            // Create the initial API query for all news items
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority(GUARDIAN_API_AUTHORITY)
                    .appendPath(GUARDIAN_API_SEARCH)
                    .appendQueryParameter(GUARDIAN_API_BEFOREKEY, GUARDIAN_API_KEY);
            apiUrl = builder.build().toString();
            // Start loading the information in a background task
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            Toast.makeText(MainActivity.this, R.string.no_internet,
                    Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            noInternet.setVisibility(View.VISIBLE);
            noInternet.setText(getString(R.string.no_internet));
        }
    }

    @Override
    public void onClick(View view, int position) {
        final NewsItem news = newsItems.get(position);
        Intent i = new Intent(ACTION_VIEW, Uri.parse(news.getUrl()));
        startActivity(i);
    }

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int i, Bundle args) {
        return new NewsLoader(this, apiUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> data) {
        if (activeNetwork != null && activeNetwork.isConnected()) {
            mSwipeRefreshLayout.setRefreshing(false);
            mAdapter.clear();
            mAdapter.addAll(data);
            progressBar.setVisibility(View.GONE);
        } else {
            noInternet.setVisibility(View.VISIBLE);
        }
        // In case no items are displayed even though there is an internet connection
        if (data.isEmpty()) {
            recList.setVisibility(View.GONE);
            noItems.setVisibility(View.VISIBLE);
        } else {
            recList.setVisibility(View.VISIBLE);
            noItems.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        mAdapter.clear();
    }

    @Override
    public void onRefresh() {
        getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
    }
}
