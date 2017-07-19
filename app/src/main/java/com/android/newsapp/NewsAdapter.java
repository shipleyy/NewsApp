package com.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsList;
    public static NewsItem ni;
    private RelativeLayout rl;

    public static class NewsViewHolder extends RecyclerView.ViewHolder {

        public TextView vAuthor, vHeadline, vCategory, vDateTime;
        public RelativeLayout rl;

        public NewsViewHolder(View itemView) {
            super(itemView);
            vHeadline = (TextView) itemView.findViewById(R.id.article_headline);
            vCategory = (TextView) itemView.findViewById(R.id.article_category);
            vDateTime = (TextView) itemView.findViewById(R.id.article_time);
            rl = (RelativeLayout) itemView.findViewById(R.id.rl);
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(ni.url));
                    context.startActivity(i);
                }
            });
        }
    }

    public NewsAdapter(List<NewsItem> newsList) {
        this.newsList = newsList;
    }

    @Override
    public NewsAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.listitem, parent, false);
        return new NewsViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {

        //Find the current news item in the ArrayList
        ni = newsList.get(position);

        // Display the information from the current item in the list
        assert ni != null;
        holder.vHeadline.setText(ni.headline);
        holder.vCategory.setText(ni.category);
        String date = ni.dateTime;
        String newsDate = formatDate(date);
        holder.vDateTime.setText(newsDate);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }


    // Clear the current list
    public void clear() {
        int size = this.newsList.size();
        this.newsList.clear();
        notifyItemRangeRemoved(0, size);
    }

    /**
     * Adds new objects to the listVew
     */
    public void addAll(List<NewsItem> newsList) {
        this.newsList.addAll(newsList);
        notifyDataSetChanged();
    }

    // Formats the date from the JSON to an easier readable format
    public String formatDate(String date) {

        String dateFormatted = "";
        String dateNew = date.substring(0, 10); // gets date in yyyy-mm-dd format from timestamp

        // Format dateNew
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat newFormat = new SimpleDateFormat("MMM dd, yyyy");
        try {
            Date dt = inputFormat.parse(dateNew);
            dateFormatted = newFormat.format(dt);
        } catch (ParseException pe) {
            Log.e("formatDate", "Error " + pe);
        }

        return dateFormatted;
    }
}



