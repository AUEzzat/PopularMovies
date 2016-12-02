package com.example.android.app.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivityFragment extends Fragment {

    private MovieDetailAdapter popMoviesAdapter;
    private View rootView;
    private SharedPreferences sharedPrefs;
    private FetchMoviesData fetchMoviesData = null;

    public MainActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMoviesGrid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        popMoviesAdapter =
                new MovieDetailAdapter(
                        getActivity(), // The current context (this activity)
                        new ArrayList<MovieDetail>());
        if (savedInstanceState != null) {
            ArrayList<MovieDetail> items = savedInstanceState.getParcelableArrayList("moviesAdapter");
            if (items != null)
                popMoviesAdapter.addAll(items); // Load saved data if any.
        }

        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String toastMessage = "Enter your themoviedb.org API";
        if (getString(R.string.pref_api_value).equals(toastMessage)) {
            toastMessage = "Enter your themoviedb.org API key";
            Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_LONG).show();
        }

        // Get a reference to the GridView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.popular_movies_grid);
        gridView.setAdapter(popMoviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MovieDetail movie = popMoviesAdapter.getItem(position);
                //check twopane or not
                if (getActivity().findViewById(R.id.details_fragment) != null) {
                    DetailActivity.DetailActivityFragment movieDetailFragment = new DetailActivity.DetailActivityFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("movie_detail", movie);
                    movieDetailFragment.setArguments(args);

                    getFragmentManager().beginTransaction()
                            .replace(R.id.details_fragment, movieDetailFragment)
                            .commit();
                } else {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra("movie_detail", movie);
                    startActivity(intent);
                }
            }
        });
        gridView.setDrawSelectorOnTop(true);
        return rootView;
    }

    private void updateMoviesGrid() {
        FetchMoviesData movieTask = new FetchMoviesData();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String api_key = sharedPrefs.getString(getString(R.string.pref_api_key), getString(R.string.pref_api_value));
        String movieStartPref = sharedPrefs.getString(getString(R.string.pref_movie_state_key),
                getString(R.string.pref_value_popularity));
        movieTask.execute(api_key, movieStartPref);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("moviesAdapter", popMoviesAdapter.getAll());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fetchMoviesData != null) {
            fetchMoviesData.cancel(true);
            fetchMoviesData = null;
        }

    }

    public class FetchMoviesData extends AsyncTask<String, Void, MovieDetail[]> {

        private final String LOG_TAG = FetchMoviesData.class.getSimpleName();

        private boolean sortByFavourites;
//        private ProgressDialog progressDialog;

        private MovieDetail[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException, IOException {

            JSONObject movieJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray;
            moviesArray = movieJson.getJSONArray("results");

            MovieDetail[] moviesResult = new MovieDetail[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {

                JSONObject eachMovie = moviesArray.getJSONObject(i);
                String movieID = eachMovie.getString("id");
                String movieTitle = eachMovie.getString("title");
                String releaseDateStr = eachMovie.getString("release_date");
                Integer releaseDate = (releaseDateStr.length() != 0) ?
                        Integer.parseInt(releaseDateStr.substring(0, 4)) : 0;
                String posterID = eachMovie.getString("poster_path");
                String moviePosterStr = "http://image.tmdb.org/t/p/w185" + posterID;
                if (posterID.equals("null"))
                    moviePosterStr = "https://cdn.amctheatres.com/Media/Default/Images/noposter.jpg";
                Bitmap moviePoster = Picasso.with(getActivity()).load(moviePosterStr).centerCrop().resize(185, 277).get();
                Double voteAverage = Double.parseDouble(eachMovie.getString("vote_average"));
                Integer voteCount = Integer.parseInt(eachMovie.getString("vote_count"));
                String plotSynopsis = eachMovie.getString("overview");
                moviesResult[i] = new MovieDetail(movieID, movieTitle, releaseDate, moviePoster,
                        voteAverage, voteCount, plotSynopsis);

            }
            return moviesResult;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = ProgressDialog.show(getContext(), "Loading...",
//                    "Data is Loading...");
        }

        @Override
        protected MovieDetail[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            String myApiKey = params[0];
            String movieState = "popularity.desc";
            String movieStatePref = params[1];
            sortByFavourites = false;
            if (movieStatePref.equals(getString(R.string.pref_value_top_rated)))
                movieState = "top_rated.desc";
            else if (movieStatePref.equals(getString(R.string.pref_value_popularity)))
                movieState = "popularity.desc";
            else
                sortByFavourites = true;
            try {
                if (sortByFavourites) {
                    Set<String> favouriteMoviesSet =
                            sharedPrefs.getStringSet(getString(R.string.favourite_movies), new HashSet<String>());
                    JSONArray moviesJsonArray = new JSONArray();
                    for (String favouriteMovieID : favouriteMoviesSet) {
                        Uri.Builder movieUrl = new Uri.Builder();
                        movieUrl.scheme("https")
                                .authority("api.themoviedb.org")
                                .appendPath("3")
                                .appendPath("movie")
                                .appendPath(favouriteMovieID)
                                .appendQueryParameter("api_key", myApiKey);
                        URL url = new URL(movieUrl.toString());
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
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
                        String movieJsonStr = buffer.toString();
                        JSONObject favMovieJsonObj = new JSONObject(movieJsonStr);
                        moviesJsonArray.put(favMovieJsonObj);
                    }
                    JSONObject moviesJsonArrayObj = new JSONObject().put("results", moviesJsonArray);
                    moviesJsonStr = moviesJsonArrayObj.toString();
                } else {
                    Uri.Builder movieUrl = new Uri.Builder();
                    movieUrl.scheme("https")
                            .authority("api.themoviedb.org")
                            .appendPath("3")
                            .appendPath("discover")
                            .appendPath("movie")
                            .appendQueryParameter("sort_by", movieState)
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
                    moviesJsonStr = buffer.toString();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } catch (JSONException e) {
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
                return getMoviesDataFromJson(moviesJsonStr);
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
        protected void onPostExecute(MovieDetail[] movieResult) {
//            progressDialog.dismiss();
            if (movieResult != null && popMoviesAdapter != null && isAdded()) {
                popMoviesAdapter.clear();
                for (MovieDetail popMovieObj : movieResult) {
                    popMoviesAdapter.add(popMovieObj);
                }
                if (movieResult.length == 0) {
                    TextView popMoviesText = (TextView) rootView.findViewById(R.id.popular_movies_text);
                    if (sortByFavourites)
                        popMoviesText.setText("Add movies to Favourites to see here");
                    else
                        popMoviesText.setText("No movies found with this search criteria");
                }
            }
            fetchMoviesData = null;
        }
    }
}
