package com.example.android.app.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by amrezzat on 11/25/2016.
 */

public class MovieTrailerAdapter extends ArrayAdapter<MovieTrailer> {

    private static final String LOG_TAG = MovieTrailerAdapter.class.getSimpleName();

    public MovieTrailerAdapter(Activity context, List<MovieTrailer> movieTrailers) {
        super(context, 0, movieTrailers);
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

        MovieTrailer movieTrailer = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_trailers_layout, parent, false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.movie_trailer_icon);
        iconView.setImageBitmap(movieTrailer.getTrailerImage());

        TextView versionNumberView = (TextView) convertView.findViewById(R.id.movie_trailer_text);
        versionNumberView.setText(movieTrailer.getTrailerTitle());

        return convertView;
    }
}