package com.udacity.demur.popularmovies.database;

import androidx.room.ColumnInfo;

public class LikedEntryJsonAndPoster {
    private byte[] poster;
    @ColumnInfo(name = "movie_json")
    private String movieJson;

    public LikedEntryJsonAndPoster(String movieJson, byte[] poster) {
        this.movieJson = movieJson;
        this.poster = poster;
    }

    public byte[] getPoster() {
        return poster;
    }

    public void setPoster(byte[] poster) {
        this.poster = poster;
    }

    public String getMovieJson() {
        return movieJson;
    }

    public void setMovieJson(String movieJson) {
        this.movieJson = movieJson;
    }
}