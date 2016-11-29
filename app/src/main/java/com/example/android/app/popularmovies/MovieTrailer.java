package com.example.android.app.popularmovies;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by amrezzat on 11/25/2016.
 */

public class MovieTrailer implements Parcelable {

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

    protected MovieTrailer(Parcel in) {
        trailerTitle = in.readString();
        trailerVideoSource = in.readString();
        trailerImage = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trailerTitle);
        dest.writeString(trailerVideoSource);
        dest.writeValue(trailerImage);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieTrailer> CREATOR = new Parcelable.Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };
}