package com.udacity.demur.popularmovies.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LikedDao {
    @Query("SELECT * FROM liked ORDER BY rating DESC")
    LiveData<List<LikedEntry>> loadAllLiked();

    @Query("SELECT movie_json FROM liked ORDER BY rating DESC")
    LiveData<List<String>> loadAllLikedMovieJson();

    @Query("SELECT movie_json, poster FROM liked ORDER BY rating DESC")
    LiveData<List<LikedEntryJsonAndPoster>> loadAllLikedMovieJsonAndPoster();

    @Query("SELECT poster FROM liked ORDER BY rating DESC")
    List<byte[]> loadLikedBytePosters();

    @Insert
    void insertLiked(LikedEntry likedEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateLiked(LikedEntry likedEntry);

    @Delete
    void deleteLiked(LikedEntry likedEntry);

    @Query("DELETE FROM liked WHERE id = :id")
    void deleteLikedById(int id);

    @Query("SELECT * FROM liked WHERE id = :id")
    LiveData<LikedEntry> loadLikedByIdLive(int id);

    @Query("SELECT * FROM liked WHERE id = :id")
    LikedEntry loadLikedById(int id);

    @Query("SELECT * FROM liked WHERE id = :id")
    List<LikedEntry> checkLikedById(int id);

    @Query("UPDATE liked SET review_set_json = :review_set_json WHERE id = :id")
    void updateReviewSetJson(int id, String review_set_json);

    @Query("UPDATE liked SET video_set_json = :video_set_json WHERE id = :id")
    void updateVideoSetJson(int id, String video_set_json);

    @Query("SELECT id FROM liked")
    LiveData<List<Integer>> loadAllLikedIdsLive();

    @Query("SELECT id FROM liked")
    List<Integer> loadAllLikedIds();
}