package com.udacity.demur.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

@Database(entities = {LikedEntry.class}, version = 1, exportSchema = false)
public abstract class TMDBLikedDatabase extends RoomDatabase {
    private static final String LOG_TAG = TMDBLikedDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "TMDB_liked";
    private static TMDBLikedDatabase sInstance;

    public static TMDBLikedDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        TMDBLikedDatabase.class, TMDBLikedDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract LikedDao likedDao();
}