package com.example.android.app.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    public static class DetailActivityFragment extends Fragment {

        private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

        private String movieStr[];

        public DetailActivityFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                movieStr = intent.getStringExtra(Intent.EXTRA_TEXT).split("░");
                ((TextView) rootView.findViewById(R.id.movie_title)).setText(movieStr[0]);
                ((TextView) rootView.findViewById(R.id.release_date)).setText(movieStr[1]);
                ((ImageView) rootView.findViewById(R.id.movie_poster)).setImageBitmap(StringToBitMap(movieStr[2]));
                ((TextView) rootView.findViewById(R.id.vote_average)).setText(movieStr[3]);
                ((TextView) rootView.findViewById(R.id.plot_synopsis)).setText(movieStr[4]);
            }
            return rootView;
        }

        /**
         * @param encodedString
         * @return bitmap (from given string)
         */
        private static Bitmap StringToBitMap(String encodedString){
            try{
                byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
                Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                return bitmap;
            }catch(Exception e){
                e.getMessage();
                return null;
            }
        }
    }
}


