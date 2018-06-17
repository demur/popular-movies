package com.udacity.demur.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;

import static android.content.Context.MODE_PRIVATE;

public abstract class InfiniteScrollListener extends OnScrollListener {
    private static final int startingPage = 1;
    private static final int itemsPerPage = 20;
    private GridLayoutManager layoutManager;
    private int itemsBeforeNextLoad = 3;
    private int formerItemCount = 0;
    private int currentPage = 1;
    private boolean isLoading = true;
    private int formerSortMode = 0;
    private SharedPreferences mSharedPrefs;

    public InfiniteScrollListener(GridLayoutManager layoutManager, Context context) {
        this.layoutManager = layoutManager;
        itemsBeforeNextLoad *= layoutManager.getSpanCount();
        mSharedPrefs = context.getSharedPreferences("Settings", MODE_PRIVATE);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int immediateSortMode = mSharedPrefs.getInt("sort_mode", R.id.action_sort_popular);
        // Checking if type of content in RecyclerView has changed
        if (formerSortMode != immediateSortMode) {
            if (isLoading) {
                isLoading = false;
            }
            formerSortMode = immediateSortMode;
        }
        // Drop InfiniteScrollListener if RecyclerView displays list of liked movies
        if (immediateSortMode == R.id.action_sort_liked) {
            return;
        }
        int immediateItemCount = layoutManager.getItemCount();
        // Fix currentPage on rotation and resume
        if (currentPage == 1 && immediateItemCount > itemsPerPage) {
            currentPage = (immediateItemCount + itemsPerPage - 1) / itemsPerPage;
        }

        // In case data is corrupted fix it, else if loaded movies detected update data
        if (immediateItemCount < formerItemCount) {
            formerItemCount = immediateItemCount;
            currentPage = startingPage;
        } else if (immediateItemCount > formerItemCount && isLoading) {
            formerItemCount = immediateItemCount;
            isLoading = false;
        }

        // If no loading detected and being scrolled into preload zone then load next page
        if (!isLoading && (layoutManager.findLastVisibleItemPosition() + itemsBeforeNextLoad) > immediateItemCount) {
            currentPage++;
            isLoading = true;
            loadNextPage(currentPage);
        }
    }

    public abstract void loadNextPage(int currentPage);
}