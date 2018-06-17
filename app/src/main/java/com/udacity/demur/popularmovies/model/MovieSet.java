package com.udacity.demur.popularmovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MovieSet implements Serializable {
    @Expose
    @SerializedName("results")
    private ArrayList<Movie> results;
    @Expose
    @SerializedName("total_pages")
    private int total_pages;
    @Expose
    @SerializedName("total_results")
    private int total_results;
    @Expose
    @SerializedName("page")
    private int page;

    public ArrayList<Movie> getMovies() {
        return results;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.results = movies;
    }

    public void addMovies(ArrayList<Movie> movies) {
        this.results.addAll(movies);
    }

    public void addMovies(MovieSet movieSet) {
        this.results.addAll(movieSet.getMovies());
        this.total_pages = movieSet.getTotal_pages();
        this.total_results = movieSet.getTotal_results();
        this.page = movieSet.getPage();
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public int getTotal_results() {
        return total_results;
    }

    public void setTotal_results(int total_results) {
        this.total_results = total_results;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}