package com.udacity.demur.popularmovies.service;

import com.udacity.demur.popularmovies.model.MovieSet;
import com.udacity.demur.popularmovies.model.ReviewSet;
import com.udacity.demur.popularmovies.model.VideoSet;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface TMDbClient {
    @GET("movie/popular")
    Call<MovieSet> listPopularMovies();

    /*
     * Available options:
     * String language
     * Integer page
     * String region
     */
    @GET("movie/popular")
    Call<MovieSet> listPopularMovies(@QueryMap Map<String, String> options);

    @GET("movie/top_rated")
    Call<MovieSet> listTopRatedMovies();

    /*
     * Available options:
     * String language
     * Integer page
     * String region
     */
    @GET("movie/top_rated")
    Call<MovieSet> listTopRatedMovies(@QueryMap Map<String, String> options);

    @GET("movie/{movie_id}/videos")
    Call<VideoSet> getVideos(@Path("movie_id") int movieId);

    /*
     * Available options:
     * String language
     */
    @GET("movie/{movie_id}/videos")
    Call<VideoSet> getVideos(@Path("movie_id") int movieId, @QueryMap Map<String, String> options);

    @GET("movie/{movie_id}/reviews")
    Call<ReviewSet> getReviews(@Path("movie_id") int movieId);

    /*
     * Available options:
     * String language
     * Integer page
     */
    @GET("movie/{movie_id}/reviews")
    Call<ReviewSet> getReviews(@Path("movie_id") int movieId, @QueryMap Map<String, String> options);
}