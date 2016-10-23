package com.example.android.app.popularmovies;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by amrezzat on 10/21/2016.
 */

public class MovieDetail {

    private String movieTitle;
    private int releaseDate;
    private Bitmap moviePoster;
    private double voteAverage;
    private String plotSynopsis;

    public MovieDetail(){
        this.movieTitle = "";
        this.releaseDate = 0;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        this.moviePoster = Bitmap.createBitmap(200, 200, conf);
        this.voteAverage = 0.0;
        this.plotSynopsis = "";
    }

    public MovieDetail(String movieTitle, int releaseDate, Bitmap moviePoster, double voteAverage, String plotSynopsis){
        this.movieTitle = movieTitle;
        this.releaseDate = releaseDate;
        this.moviePoster = moviePoster;
        this.voteAverage = voteAverage;
        this.plotSynopsis = plotSynopsis;
    }

    public Bitmap getMoviePoster(){
        return moviePoster;
    }

    public String getString(){
        return (movieTitle+","+releaseDate+","+BitMapToString(moviePoster)+","+voteAverage+","+plotSynopsis);
    }

    /**
     * @param bitmap
     * @return converting bitmap and return a string
     */
    private static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}
