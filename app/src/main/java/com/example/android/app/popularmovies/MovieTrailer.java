package com.example.android.app.popularmovies;

import android.graphics.Bitmap;

/**
 * Created by amrezzat on 11/25/2016.
 */

public class MovieTrailer {

    private String trailerTitle;
    private String trailerVideoSource;
    private Bitmap trailerImage;

    public MovieTrailer(String trailerTitle, String trailerVideoSource, Bitmap trailerImage) {
        this.trailerTitle = trailerTitle;
        this.trailerVideoSource = trailerVideoSource;
        this.trailerImage = trailerImage;
    }

    public String getTrailerTitle() {
        return trailerTitle;
    }

    public String getTrailerVideoSource() {
        return trailerVideoSource;
    }

    public Bitmap getTrailerImage() {
        return trailerImage;
    }
}
