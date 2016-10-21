package com.example.android.app.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            startActivity(new Intent(this, SettingsActivity.class));
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public static class DetailActivityFragment extends Fragment {

        private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

        private String movieStr[];
//        private static final String MOVIE_SHARE_HASHTAG = " #PopularMoviesApp";

        public DetailActivityFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                movieStr = intent.getStringExtra(Intent.EXTRA_TEXT).split(",");
                ((TextView) rootView.findViewById(R.id.movie_title)).setText(movieStr[0]);
                ((TextView) rootView.findViewById(R.id.release_date)).setText(movieStr[1]);
                ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_poster);
                Picasso.with(getContext())
                        .load(movieStr[2])
                        .into(imageView);
                ((TextView) rootView.findViewById(R.id.vote_average)).setText(movieStr[3]);
                ((TextView) rootView.findViewById(R.id.plot_synopsis)).setText(movieStr[4]);
            }
            return rootView;
        }
//        private Intent createShareForecastIntent() {
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//            shareIntent.setType("text/plain");
//            shareIntent.putExtra(Intent.EXTRA_TEXT,
//                    movieStr + MOVIE_SHARE_HASHTAG);
//            return shareIntent;
//        }
    }
}


