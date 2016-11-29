package com.example.android.app.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by amrezzat on 11/26/2016.
 */

public class MovieReview implements Parcelable {

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

    protected MovieReview(Parcel in) {
        reviewAuthor = in.readString();
        reviewContent = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reviewAuthor);
        dest.writeString(reviewContent);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieReview> CREATOR = new Parcelable.Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };
}