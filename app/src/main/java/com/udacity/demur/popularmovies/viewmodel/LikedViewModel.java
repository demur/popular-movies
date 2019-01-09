package com.udacity.demur.popularmovies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.udacity.demur.popularmovies.database.LikedEntryJsonAndPoster;
import com.udacity.demur.popularmovies.database.TMDBLikedDatabase;

import java.util.List;
import java.util.Map;

public class LikedViewModel extends AndroidViewModel {

    private static final String TAG = LikedViewModel.class.getSimpleName();

    private LiveData<List<LikedEntryJsonAndPoster>> likedJsonAndPosterEntries;
    private Map<Integer, byte[]> posterMap;

    public LikedViewModel(@NonNull Application application) {
        super(application);
        TMDBLikedDatabase mDb = TMDBLikedDatabase.getInstance(this.getApplication());
        Log.d(TAG, "LikedViewModel: getting JSONs of liked entries from the DB");
        likedJsonAndPosterEntries = mDb.likedDao().loadAllLikedMovieJsonAndPoster();
    }

    public LiveData<List<LikedEntryJsonAndPoster>> getLiked() {
        return likedJsonAndPosterEntries;
    }

    public Map<Integer, byte[]> getPosterMap() {
        return posterMap;
    }

    public void setPosterMap(Map<Integer, byte[]> posterMap) {
        this.posterMap = posterMap;
    }
}