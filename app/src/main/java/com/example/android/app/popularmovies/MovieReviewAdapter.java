package com.example.android.app.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amrezzat on 11/26/2016.
 */

public class MovieReviewAdapter extends ArrayAdapter<MovieReview> {
    private static final String LOG_TAG = MovieReviewAdapter.class.getSimpleName();

    public MovieReviewAdapter(Activity context, List<MovieReview> movieReviews) {
        super(context, 0, movieReviews);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MovieReview movieReview = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_reviews_layout, parent, false);
        }

        TextView reviewAuthorView = (TextView) convertView.findViewById(R.id.movie_review_author_textview);
        reviewAuthorView.setText(movieReview.getReviewAuthor());

        TextView reviewContentView = (TextView) convertView.findViewById(R.id.movie_review_content_textview);
        reviewContentView.setText(movieReview.getReviewContent());

        return convertView;
    }

    public ArrayList<MovieReview> getAll(){
        int count = getCount();
        ArrayList<MovieReview> tempArray = new ArrayList<>(count);
        for (int i =0; i < count; i++) {
            tempArray.add(getItem(i));
        }
        return tempArray;
    }
}
