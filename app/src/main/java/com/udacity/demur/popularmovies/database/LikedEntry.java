package com.udacity.demur.popularmovies.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "liked")
public class LikedEntry {
    @PrimaryKey
    private int id;//@ColumnInfo(name = "_id")
    private String title;
    private double rating;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] poster;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] backdrop;
    @ColumnInfo(name = "movie_json")
    private String movieJson;
    @ColumnInfo(name = "video_set_json")
    private String videoSetJson;
    @ColumnInfo(name = "review_set_json")
    private String reviewSetJson;

    @Ignore
    public LikedEntry(int id, String title) {
        this.id = id;
        this.title = title;
    }

    @Ignore
    public LikedEntry(int id, String title, double rating) {
        this.id = id;
        this.title = title;
        this.rating = rating;
    }

    @Ignore
    public LikedEntry(int id, String title, double rating, byte[] poster) {
        this.id = id;
        this.title = title;
        this.rating = rating;
        this.poster = poster;
    }

    @Ignore
    public LikedEntry(int id, String title, double rating, byte[] poster, byte[] backdrop, String movieJson) {
        this.id = id;
        this.title = title;
        this.rating = rating;
        this.poster = poster;
        this.backdrop = backdrop;
        this.movieJson = movieJson;
    }

    public LikedEntry(int id, String title, double rating, byte[] poster, byte[] backdrop, String movieJson, String videoSetJson, String reviewSetJson) {
        this.id = id;
        this.title = title;
        this.rating = rating;
        this.poster = poster;
        this.backdrop = backdrop;
        this.movieJson = movieJson;
        this.videoSetJson = videoSetJson;
        this.reviewSetJson = reviewSetJson;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public byte[] getPoster() {
        return poster;
    }

    public void setPoster(byte[] poster) {
        this.poster = poster;
    }

    public byte[] getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(byte[] backdrop) {
        this.backdrop = backdrop;
    }

    public String getMovieJson() {
        return movieJson;
    }

    public void setMovieJson(String movieJson) {
        this.movieJson = movieJson;
    }

    public String getVideoSetJson() {
        return videoSetJson;
    }

    public void setVideoSetJson(String videoSetJson) {
        this.videoSetJson = videoSetJson;
    }

    public String getReviewSetJson() {
        return reviewSetJson;
    }

    public void setReviewSetJson(String reviewSetJson) {
        this.reviewSetJson = reviewSetJson;
    }
}