package com.udacity.demur.popularmovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class VideoSet implements Serializable {
    @Expose
    @SerializedName("results")
    private List<Video> results;
    @Expose
    @SerializedName("id")
    private int id;

    public List<Video> getVideos() {
        return results;
    }

    public void setVideos(List<Video> videos) {
        this.results = videos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}