package com.example.android.app.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MovieDetailAdapter popMoviesAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        popMoviesAdapter =
                new MovieDetailAdapter(
                        getActivity(), // The current context (this activity)
                        new ArrayList<MovieDetail>());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.popular_movies_grid);
        gridView.setAdapter(popMoviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MovieDetail movie = popMoviesAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, movie.getString());
                startActivity(intent);
            }
        });
        return  rootView;
    }

    private void updateMovieList() {
        FetchMoviesData movieTask = new FetchMoviesData();
        movieTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieList();
    }

    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    public class FetchMoviesData extends AsyncTask<Void, Void, MovieDetail[]> {

        private final String LOG_TAG = FetchMoviesData.class.getSimpleName();

        private MovieDetail[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException, IOException {

            JSONObject forecastJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = forecastJson.getJSONArray("results");

            MovieDetail[] moviesResult = new MovieDetail[moviesArray.length()];

            for(int i = 0; i < moviesArray.length(); i++) {
                JSONObject eachMovie = moviesArray.getJSONObject(i);
                String movieTitle = eachMovie.getString("title");
                Integer releaseDate = Integer.parseInt(eachMovie.getString("release_date").substring(0,4));
                String moviePosterStr = "http://image.tmdb.org/t/p/w185"+eachMovie.getString("poster_path");
                Bitmap moviePoster= Picasso.with(getContext()).load(moviePosterStr).get();
                Double voteAverage = Double.parseDouble(eachMovie.getString("vote_average"));
                String plotSynopsis = eachMovie.getString("overview");
                moviesResult[i] = new MovieDetail(movieTitle, releaseDate, moviePoster, voteAverage, plotSynopsis);
            }
            return moviesResult;
        }
        @Override
        protected MovieDetail[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            String myApiKey = "";
            String sortType = "popularity.desc";
            try {
                Uri.Builder movieUrl = new Uri.Builder();
                movieUrl.scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("discover")
                        .appendPath("movie")
                        .appendQueryParameter("sort_by", sortType)
                        .appendQueryParameter("api_key", myApiKey);
                URL url = new URL(movieUrl.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                //while(!isOnline());
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
            catch (IOException e){
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
            finally {
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
            }
            catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(MovieDetail[] movieResult) {
            if (movieResult != null) {
                popMoviesAdapter.clear();
                for (MovieDetail popMovieObj : movieResult) {
                    popMoviesAdapter.add(popMovieObj);
                }
            }
        }
    }
}
