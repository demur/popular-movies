package com.udacity.demur.popularmovies.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {LikedEntry.class}, version = 1, exportSchema = false)
public abstract class TMDBLikedDatabase extends RoomDatabase {
    private static final String TAG = TMDBLikedDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "TMDB_liked";
    private static TMDBLikedDatabase sInstance;

    public static TMDBLikedDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        TMDBLikedDatabase.class, TMDBLikedDatabase.DATABASE_NAME)
                        .build();
            }
        }
        Log.d(TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract LikedDao likedDao();
}