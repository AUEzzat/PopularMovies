package com.example.android.app.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    public static class DetailActivityFragment extends Fragment {

        private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

        private MovieDetail movie;
        private ArrayAdapter<String> movieReviewsAdapter;

        public DetailActivityFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            movieReviewsAdapter = new ArrayAdapter<String>(
                            getActivity(), // The current context (this activity)
                            R.layout.movie_reviews_layout, // The name of the layout ID.
                            R.id.movie_review_layout_textview, // The ID of the textview to populate.
                            new ArrayList<String>());
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.movie_reviews_list);
            listView.setAdapter(movieReviewsAdapter);
            if (intent != null && intent.hasExtra("movie_detail")) {
                movie = intent.getExtras().getParcelable("movie_detail");
                ((TextView) rootView.findViewById(R.id.movie_title)).setText(movie.getMovieTitle());
                ((TextView) rootView.findViewById(R.id.release_date)).setText(movie.getReleaseDateStr());
                ((ImageView) rootView.findViewById(R.id.movie_poster)).setImageBitmap(movie.getMoviePoster());
                ((TextView) rootView.findViewById(R.id.vote_average)).setText(movie.getVoteAverageStr());
                ((TextView) rootView.findViewById(R.id.plot_synopsis)).setText(movie.getPlotSynopsis());
            }
            return rootView;
        }

        private void updateMovieDetails() {
            FetchMovieReviews movieReviewsTask = new FetchMovieReviews();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String api_key = prefs.getString(getString(R.string.pref_api_key), getString(R.string.pref_api_value));
            String movieID = movie.getMovieID();
            movieReviewsTask.execute(api_key, movieID);
        }

        @Override
        public void onStart() {
            super.onStart();
            updateMovieDetails();
        }

        public class FetchMovieReviews extends AsyncTask<String, Void, ArrayList<String>> {

            private final String LOG_TAG = FetchMovieReviews.class.getSimpleName();

            private ArrayList<String> getMovieReviewsFromJson(String movieReviewsJsonStr) throws JSONException, IOException {

                JSONObject forecastJson = new JSONObject(movieReviewsJsonStr);
                JSONArray moviesReviewsArray = forecastJson.getJSONArray("results");
                ArrayList<String> movieReviews = new ArrayList<>(moviesReviewsArray.length());

                for (int i = 0; i < moviesReviewsArray.length(); i++) {
                    JSONObject eachMovie = moviesReviewsArray.getJSONObject(i);
                    String reviewAuthor = eachMovie.getString("author");
                    String reviewContent = eachMovie.getString("content");
                    String review = reviewContent;
                    movieReviews.add(reviewAuthor + ":\n\n" + review);
                }
                return movieReviews;
            }

            @Override
            protected ArrayList<String> doInBackground(String... params) {

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String movieReviewsJsonStr = null;
                String myApiKey = params[0];
                String movieID = params[1];
                try {
                    Uri.Builder movieUrl = new Uri.Builder();
                    movieUrl.scheme("https")
                            .authority("api.themoviedb.org")
                            .appendPath("3")
                            .appendPath("movie")
                            .appendPath(movieID)
                            .appendPath("reviews")
                            .appendQueryParameter("api_key", myApiKey);
                    URL url = new URL(movieUrl.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    movieReviewsJsonStr = buffer.toString();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
                try {
                    return getMovieReviewsFromJson(movieReviewsJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<String> movieReviews) {
                if (movieReviews != null) {
                    movieReviewsAdapter.clear();
                    for (String movieReview : movieReviews) {
                        movieReviewsAdapter.add(movieReview);
                    }
                }
                if(movieReviews.size() == 0){
                    movieReviewsAdapter.add("No reviews yet.");
                }
            }
        }
    }
}


