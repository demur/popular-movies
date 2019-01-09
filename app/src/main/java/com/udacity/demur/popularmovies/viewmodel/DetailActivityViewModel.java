package com.udacity.demur.popularmovies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.udacity.demur.popularmovies.AppExecutors;
import com.udacity.demur.popularmovies.database.TMDBLikedDatabase;
import com.udacity.demur.popularmovies.model.Movie;
import com.udacity.demur.popularmovies.model.ReviewSet;
import com.udacity.demur.popularmovies.model.VideoSet;

public class DetailActivityViewModel extends AndroidViewModel {
    private Movie movie;
    private MutableLiveData<VideoSet> videoSet = new MutableLiveData<>();
    private MutableLiveData<ReviewSet> reviewSet = new MutableLiveData<>();
    private Application application;

    public DetailActivityViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setVideoSet(final VideoSet videoSet) {
        this.videoSet.setValue(videoSet);
        if (movie.isLiked()) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    TMDBLikedDatabase.getInstance(application.getApplicationContext())
                            .likedDao().updateReviewSetJson(movie.getId(), new Gson().toJson(videoSet));
                }
            });
        }
    }

    public MutableLiveData<VideoSet> getVideoSet() {
        return videoSet;
    }

    public void setReviewSet(final ReviewSet reviewSet) {
        this.reviewSet.setValue(reviewSet);
        if (movie.isLiked()) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    TMDBLikedDatabase.getInstance(application.getApplicationContext())
                            .likedDao().updateReviewSetJson(movie.getId(), new Gson().toJson(reviewSet));
                }
            });
        }
    }

    public MutableLiveData<ReviewSet> getReviewSet() {
        return reviewSet;
    }
}