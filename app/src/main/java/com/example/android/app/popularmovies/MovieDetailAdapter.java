package com.example.android.app.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by amrezzat on 10/23/2016.
 */

public class MovieDetailAdapter extends ArrayAdapter<MovieDetail> {

    private static final String LOG_TAG = MovieDetailAdapter.class.getSimpleName();

    public MovieDetailAdapter(Activity context, List<MovieDetail> movieDetails) {
        super(context, 0, movieDetails);
    }
    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MovieDetail movieDetail = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.popular_movies_layout, parent, false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.list_item_icon);
        iconView.setImageBitmap(movieDetail.getMoviePoster());

        return convertView;
    }
}
