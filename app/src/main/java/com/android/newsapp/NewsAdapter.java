package com.android.newsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final List<NewsItem> mNewsList;
    private final Context mContext;
    private ItemClickListener clickListener;


    public NewsAdapter(Context context, List<NewsItem> newsList) {
        mContext = context;
        mNewsList = newsList;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        // Inflate the Layout listitem.xml
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem, viewGroup, false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {

        // Getting the current item from the list
        final NewsItem ni = mNewsList.get(position);

        // Display the information from the current item in the list
        holder.vHeadline.setText(ni.getHeadline());
        holder.vCategory.setText(ni.getCategory());
        holder.vDateTime.setText(ni.getDateTime());
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    // Clear the current list
    public void clear() {
        int size = mNewsList.size();
        mNewsList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView vHeadline;
        private final TextView vCategory;
        private final TextView vDateTime;

        // The NewsViewHolder constructor
        public NewsViewHolder(View itemView) {
            super(itemView);
            vHeadline = (TextView) itemView.findViewById(R.id.article_headline);
            vCategory = (TextView) itemView.findViewById(R.id.article_category);
            vDateTime = (TextView) itemView.findViewById(R.id.article_time);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
        }
    }

    /**
     * Adds new objects to the listVew
     */
    public void addAll(List<NewsItem> newsList) {
        mNewsList.addAll(newsList);
        notifyDataSetChanged();
    }

    // Formats the date from the JSON to an easier readable format
    private String formatDate(String date) {

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



