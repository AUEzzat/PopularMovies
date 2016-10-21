package com.example.android.app.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    private ArrayAdapter<String> popMoviesAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        popMoviesAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.popular_movies_layout, // The name of the layout ID.
                        R.id.popular_movies_textview, // The ID of the textview to populate.
                        new ArrayList<String>());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.popular_movies_list);
        listView.setAdapter(popMoviesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String movie = popMoviesAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, movie);
                startActivity(intent);
            }
        });
        return  rootView;
    }

    private void updateMovieList() {
        FetchMoviesData movieTask = new FetchMoviesData();
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String location = prefs.getString(getString(R.string.pref_location_key),
//                getString(R.string.pref_location_default));
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

    public class FetchMoviesData extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG = FetchMoviesData.class.getSimpleName();

        private String[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {

            JSONObject forecastJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = forecastJson.getJSONArray("results");

            String[] resultStrs = new String[moviesArray.length()];
            String movieTitle="";
            String releaseDate="";
            String moviePoster="";
            String voteAverage="";
            String plotSynopsis="";
            for(int i = 0; i < moviesArray.length(); i++) {
                JSONObject eachMovie = moviesArray.getJSONObject(i);
                movieTitle = eachMovie.getString("title");
                releaseDate = eachMovie.getString("release_date");
                moviePoster = "http://image.tmdb.org/t/p/w185"+eachMovie.getString("poster_path");
                voteAverage = eachMovie.getString("vote_average");
                plotSynopsis = eachMovie.getString("overview");
                System.out.println(moviePoster);
                resultStrs[i] = movieTitle+","+releaseDate+","+moviePoster+","+voteAverage+","+plotSynopsis;
            }
            return resultStrs;
        }
        @Override
        protected String[] doInBackground(Void... params) {
//            if (params.length == 0) {
//                return null;
//            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            String movieUrl = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=80a833a4763920b2c735c6e5e3911338";
            try {
                URL url = new URL(movieUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                while(!isOnline());
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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
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
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            }
            // Create the request to OpenWeatherMap, and open the connection
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

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }
        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                popMoviesAdapter.clear();
                for (String popMovieStr : result) {
                    popMoviesAdapter.add(popMovieStr);
                }
            }
        }
    }
}
