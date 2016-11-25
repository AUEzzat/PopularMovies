package com.example.android.app.popularmovies;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by amrezzat on 10/21/2016.
 */

public class MovieDetail implements Parcelable {

    private String movieID;
    private String movieTitle;
    private int releaseDate;
    private String releaseDateStr;
    private Bitmap moviePoster;
    private double voteAverage;
    private String voteAverageStr;
    private int voteCount;
    private String plotSynopsis;

    public MovieDetail(String movieID, String movieTitle, int releaseDate, Bitmap moviePoster,
                       double voteAverage, int voteCount, String plotSynopsis) {
        this.movieID = movieID;
        this.movieTitle = movieTitle;
        this.releaseDate = releaseDate;
        this.moviePoster = moviePoster;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.plotSynopsis = plotSynopsis;
    }

    public String getMovieID() {
        return movieID;
    }

    public String getMovieTitle() {

        return movieTitle;
    }

    public String getReleaseDateStr() {
        releaseDateStr = "????";
        if (releaseDate != 0)
            releaseDateStr = Integer.toString(releaseDate);
        return releaseDateStr;
    }

    public String getVoteAverageStr() {
        if (voteCount == 0)
            voteAverageStr = "Not Rated";
        else
            voteAverageStr = voteAverage + "/10";
        return voteAverageStr;
    }

    public String getPlotSynopsis() {
        if (plotSynopsis.length() == 0)
            plotSynopsis = "No overview found.";
        return plotSynopsis;
    }

    public Bitmap getMoviePoster() {
        return moviePoster;
    }

    protected MovieDetail(Parcel in) {
        movieID = in.readString();
        movieTitle = in.readString();
        releaseDate = in.readInt();
        moviePoster = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        voteAverage = in.readDouble();
        voteCount = in.readInt();
        plotSynopsis = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieID);
        dest.writeString(movieTitle);
        dest.writeInt(releaseDate);
        dest.writeValue(moviePoster);
        dest.writeDouble(voteAverage);
        dest.writeInt(voteCount);
        dest.writeString(plotSynopsis);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieDetail> CREATOR = new Parcelable.Creator<MovieDetail>() {
        @Override
        public MovieDetail createFromParcel(Parcel in) {
            return new MovieDetail(in);
        }

        @Override
        public MovieDetail[] newArray(int size) {
            return new MovieDetail[size];
        }
    };
}