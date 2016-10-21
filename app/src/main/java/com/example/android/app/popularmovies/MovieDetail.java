package com.example.android.app.popularmovies;

/**
 * Created by amrezzat on 10/21/2016.
 */

public class MovieDetail {
    String movieTitle;
    String releaseDate;
    String moviePoster;
    double voteAverage;
    String plotSynopsis;
    public MovieDetail(String movieTitle,String releaseDate,String moviePoster,float voteAverage,String plotSynopsis){
        this.movieTitle = movieTitle;
        this.releaseDate = releaseDate;
        this.moviePoster = moviePoster;
        this.voteAverage = voteAverage;
        this.plotSynopsis = plotSynopsis;
    }
}
