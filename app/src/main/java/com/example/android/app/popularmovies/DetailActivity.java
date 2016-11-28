package com.example.android.app.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
import java.util.HashSet;
import java.util.Set;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    public static class DetailActivityFragment extends Fragment {

        private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
        private MovieDetail movie;
        private ArrayAdapter<MovieReview> movieReviewsAdapter;
        private ArrayAdapter<MovieTrailer> movieTrailersAdapter;
        private ListView movieTrailersListView;
        private ListView movieReviewsListView;
        private Set<String> favouriteMoviesSet;
        private Button favouriteButton;

        public DetailActivityFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            movieTrailersAdapter =
                    new MovieTrailerAdapter(
                            getActivity(), // The current context (this activity)
                            new ArrayList<MovieTrailer>());
            movieReviewsAdapter = new MovieReviewAdapter(
                    getActivity(), // The current context (this activity)
                    new ArrayList<MovieReview>());
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            movieTrailersListView = (ListView) rootView.findViewById(R.id.movie_trailers_list);
            movieTrailersListView.setAdapter(movieTrailersAdapter);


            movieTrailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    MovieTrailer movieTrailer = movieTrailersAdapter.getItem(position);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(movieTrailer.getTrailerVideoSource()));
                    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Log.d(LOG_TAG, "Couldn't call " + movieTrailer.getTrailerVideoSource() + ", no receiving apps installed!");
                    }
                }
            });


            movieReviewsListView = (ListView) rootView.findViewById(R.id.movie_reviews_list);
            movieReviewsListView.setAdapter(movieReviewsAdapter);

            if (intent != null && intent.hasExtra("movie_detail")) {
                movie = intent.getExtras().getParcelable("movie_detail");
                ((TextView) rootView.findViewById(R.id.movie_title)).setText(movie.getMovieTitle());
                ((TextView) rootView.findViewById(R.id.release_date)).setText(movie.getReleaseDateStr());
                ((ImageView) rootView.findViewById(R.id.movie_poster)).setImageBitmap(movie.getMoviePoster());
                ((TextView) rootView.findViewById(R.id.vote_average)).setText(movie.getVoteAverageStr());
                ((TextView) rootView.findViewById(R.id.plot_synopsis)).setText(movie.getPlotSynopsis());


                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                favouriteMoviesSet =
                        sharedPref.getStringSet(getString(R.string.favourite_movies), new HashSet<String>());

                favouriteButton = (Button) rootView.findViewById(R.id.favourite_button);
                if (favouriteMoviesSet.contains(movie.getMovieID()))
                    favouriteButton.setText(getString(R.string.favourite_button_remove));
                else
                    favouriteButton.setText(getString(R.string.favourite_button_add));
                favouriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = sharedPref.edit();
                        String sortGridBy = sharedPref.getString(
                                getString(R.string.pref_movie_state_key),
                                getString(R.string.pref_value_popularity));
                        if (favouriteMoviesSet.contains(movie.getMovieID())) {
                            favouriteMoviesSet.remove(movie.getMovieID());
                            favouriteButton.setText(getString(R.string.favourite_button_add));
                        }
                        else {
                            favouriteMoviesSet.add(movie.getMovieID());
                            favouriteButton.setText(getString(R.string.favourite_button_remove));
                        }
                        editor.putString(getString(R.string.pref_movie_state_key), sortGridBy);
                        editor.putStringSet(getString(R.string.favourite_movies), favouriteMoviesSet);
                        editor.clear();
                        editor.apply();
                    }
                });

                final ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.activity_detail_scroll_view);
                // Wait until my scrollView is ready
                scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Ready, move up
                        scrollView.fullScroll(View.FOCUS_UP);
                    }
                });
            }
            return rootView;
        }


        /****
         * Method for Setting the Height of the ListView dynamically.
         * *** Hack to fix the issue of not showing all the items of the ListView
         * *** when placed inside a ScrollView
         ****/
        public static void setListViewHeightBasedOnChildren(ListView listView) {
            ListAdapter listAdapter = listView.getAdapter();
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
        }

        private void updateMovieDetails() {
            FetchMovieTrailers movieTrailersTask = new FetchMovieTrailers();
            FetchMovieReviews movieReviewsTask = new FetchMovieReviews();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String api_key = prefs.getString(getString(R.string.pref_api_key), getString(R.string.pref_api_value));
            String movieID = movie.getMovieID();
            movieTrailersTask.execute(api_key, movieID);
            movieReviewsTask.execute(api_key, movieID);
        }

        @Override
        public void onStart() {
            super.onStart();
            updateMovieDetails();
        }

        public class FetchMovieTrailers extends AsyncTask<String, Void, ArrayList<MovieTrailer>> {

            private final String LOG_TAG = FetchMovieTrailers.class.getSimpleName();

            private ArrayList<MovieTrailer> getMovieTrailersFromJson(String movieTrailersJsonStr) throws JSONException, IOException {

                JSONObject movieJson = new JSONObject(movieTrailersJsonStr);
                JSONArray movieTrailersArray = movieJson.getJSONArray("youtube");
                ArrayList<MovieTrailer> movieTrailers = new ArrayList<>(movieTrailersArray.length());

                for (int i = 0; i < movieTrailersArray.length(); i++) {
                    JSONObject eachTrailer = movieTrailersArray.getJSONObject(i);
                    String trailerTitle = eachTrailer.getString("name");
                    String trailerSource = eachTrailer.getString("source");
                    String trailerImageSource = "https://img.youtube.com/vi/" + trailerSource + "/0.jpg";
                    Bitmap trailerImageBitmap = Picasso.with(getContext()).load(trailerImageSource).get();
                    trailerImageBitmap = Bitmap.createScaledBitmap(trailerImageBitmap, (int) (trailerImageBitmap.getWidth() * 0.5),
                            (int) (trailerImageBitmap.getHeight() * 0.5), true);
                    String trailerVideoSource = "https://www.youtube.com/watch?v=" + trailerSource;
                    MovieTrailer movieTrailer = new MovieTrailer(trailerTitle, trailerVideoSource, trailerImageBitmap);
                    movieTrailers.add(movieTrailer);
                }
                return movieTrailers;
            }

            @Override
            protected ArrayList<MovieTrailer> doInBackground(String... params) {

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String movieTrailersJsonStr = null;
                String myApiKey = params[0];
                String movieID = params[1];
                try {
                    Uri.Builder movieUrl = new Uri.Builder();
                    movieUrl.scheme("https")
                            .authority("api.themoviedb.org")
                            .appendPath("3")
                            .appendPath("movie")
                            .appendPath(movieID)
                            .appendPath("trailers")
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
                    movieTrailersJsonStr = buffer.toString();
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
                    return getMovieTrailersFromJson(movieTrailersJsonStr);
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
            protected void onPostExecute(ArrayList<MovieTrailer> movieTrailers) {
                if (movieTrailers != null) {
                    movieTrailersAdapter.clear();
                    for (MovieTrailer movieTrailer : movieTrailers) {
                        movieTrailersAdapter.add(movieTrailer);
                    }
                    if (movieTrailers.size() == 0) {
                        movieTrailers.add(new MovieTrailer("No trailers found", "https://www.youtube.com",
                                Bitmap.createBitmap(480, 360, Bitmap.Config.ARGB_8888)));
                    }
                    setListViewHeightBasedOnChildren(movieTrailersListView);
                }
            }
        }

        public class FetchMovieReviews extends AsyncTask<String, Void, ArrayList<MovieReview>> {

            private final String LOG_TAG = FetchMovieReviews.class.getSimpleName();

            private ArrayList<MovieReview> getMovieReviewsFromJson(String movieReviewsJsonStr) throws JSONException, IOException {

                JSONObject movieJson = new JSONObject(movieReviewsJsonStr);
                JSONArray movieReviewsArray = movieJson.getJSONArray("results");
                ArrayList<MovieReview> movieReviews = new ArrayList<>(movieReviewsArray.length());

                for (int i = 0; i < movieReviewsArray.length(); i++) {
                    JSONObject eachMovie = movieReviewsArray.getJSONObject(i);
                    String reviewAuthor = eachMovie.getString("author");
                    String reviewContent = eachMovie.getString("content");
                    MovieReview movieReview = new MovieReview(reviewAuthor, reviewContent);
                    movieReviews.add(movieReview);
                }
                return movieReviews;
            }

            @Override
            protected ArrayList<MovieReview> doInBackground(String... params) {

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
            protected void onPostExecute(ArrayList<MovieReview> movieReviews) {
                if (movieReviews != null) {
                    movieReviewsAdapter.clear();
                    for (MovieReview movieReview : movieReviews) {
                        movieReviewsAdapter.add(movieReview);
                    }
                    if (movieReviews.size() == 0) {
                        movieReviewsAdapter.add(new MovieReview("", "No reviews yet."));
                    }
                    setListViewHeightBasedOnChildren(movieReviewsListView);
                }
            }
        }
    }
}


