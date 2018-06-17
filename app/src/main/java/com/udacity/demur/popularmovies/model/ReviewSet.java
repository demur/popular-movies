package com.udacity.demur.popularmovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ReviewSet implements Serializable {
    @Expose
    @SerializedName("total_results")
    private int total_results;
    @Expose
    @SerializedName("total_pages")
    private int total_pages;
    @Expose
    @SerializedName("results")
    private List<Review> results;
    @Expose
    @SerializedName("page")
    private int page;
    @Expose
    @SerializedName("id")
    private int id;

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public List<Review> getReviews() {
        return results;
    }

    public void setReviews(List<Review> reviews) {
        this.results = reviews;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}