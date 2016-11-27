package com.example.android.app.popularmovies;

/**
 * Created by amrezzat on 11/26/2016.
 */

public class MovieReview {

    private String reviewAuthor;
    private String reviewContent;

    public MovieReview(String reviewAuthor, String reviewContent) {
        this.reviewAuthor = reviewAuthor;
        this.reviewContent = reviewContent;
    }

    public String getReviewAuthor() {
        return reviewAuthor;
    }

    public String getReviewContent() {
        return reviewContent;
    }
}
