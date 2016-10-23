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
    //private List<MovieDetail> movieDetails;

    public MovieDetailAdapter(Activity context, List<MovieDetail> movieDetails) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, movieDetails);
        //this.movieDetails = movieDetails;
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
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        MovieDetail movieDetail = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.popular_movies_layout, parent, false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.list_item_icon);
        iconView.setImageBitmap(movieDetail.getMoviePoster());

//        TextView versionNameView = (TextView) convertView.findViewById(R.id.list_item_version_name);
//        versionNameView.setText(movieDetail.versionName);
//
//        TextView versionNumberView = (TextView) convertView.findViewById(R.id.list_item_versionnumber_textview);
//        versionNumberView.setText(movieDetail.versionNumber);
        return convertView;
    }
}
