package com.udacity.demur.popularmovies;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.udacity.demur.popularmovies.database.LikedEntryJsonAndPoster;
import com.udacity.demur.popularmovies.model.Movie;
import com.udacity.demur.popularmovies.model.MovieSet;
import com.udacity.demur.popularmovies.service.RetrofitClient;
import com.udacity.demur.popularmovies.service.TMDbClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private final String LAYOUT_MANAGER_STATE_KEY = "layout_manager_state";
    private final String MOVIE_SET_KEY = "movie_set";
    private Parcelable mLayoutManagerState;
    private MovieSet mMovieSet;
    private RecyclerView.LayoutManager mLayoutManager;
    private MoviesAdapter mMovieAdapter;
    private SharedPreferences mSharedPrefs;
    private TMDbClient mClient;
    private ProgressBar mLoadingIndicator;
    private ImageView mStatusIcon;
    private TextView mStatusMessage;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static int mGridSpanCount = 3;
    private LikedViewModel mLikedViewModel;
    final Observer<List<LikedEntryJsonAndPoster>> likedObserver = new Observer<List<LikedEntryJsonAndPoster>>() {
        @Override
        public void onChanged(@Nullable final List<LikedEntryJsonAndPoster> likedJsonAndPosterEntries) {
            Log.d(TAG, "Updating list of Liked entries from LiveData in the ViewModel");
            if (likedJsonAndPosterEntries != null) {
                ArrayList<Movie> movies = new ArrayList<Movie>();
                Map<Integer, byte[]> posterMap = new HashMap<Integer, byte[]>();
                for (LikedEntryJsonAndPoster likedEntry : likedJsonAndPosterEntries) {
                    Movie movie = new Gson().fromJson(likedEntry.getMovieJson(), Movie.class);
                    //movie.setBytePoster(likedEntry.getPoster());
                    movie.setLiked(true);
                    movies.add(movie);
                    posterMap.put(movie.getId(), likedEntry.getPoster());
                }
                mMovieSet = new MovieSet();
                mMovieSet.setMovies(movies);
                mMovieAdapter.swapMovieList(movies);
                mLikedViewModel.setPosterMap(posterMap);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mStatusIcon = findViewById(R.id.iv_message_icon);
        mStatusMessage = findViewById(R.id.tv_message);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mSharedPrefs = getApplicationContext().getSharedPreferences("Settings", MODE_PRIVATE);

        mLikedViewModel = ViewModelProviders.of(this).get(LikedViewModel.class);
        mLayoutManager = new GridLayoutManager(this, mGridSpanCount);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new InfiniteScrollListener((GridLayoutManager) mLayoutManager, this) {
            @Override
            public void loadNextPage(final int currentPage) {
                // options for retrofit client
                Map<String, String> options = new HashMap<String, String>() {{
                    put("page", String.valueOf(currentPage));
                }};
                switch (mSharedPrefs.getInt("sort_mode", R.id.action_sort_popular)) {
                    case R.id.action_sort_top_rated:
                        requestTMDB(mClient.listTopRatedMovies(options), currentPage);
                        break;
                    case R.id.action_sort_popular:
                    default:
                        requestTMDB(mClient.listPopularMovies(options), currentPage);
                }
            }
        });
        mMovieAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(mMovieAdapter);

        mClient = RetrofitClient.getInstance(getApplicationContext());

        if (null != mMovieSet || (null != savedInstanceState
                && savedInstanceState.containsKey(MOVIE_SET_KEY)
                && null != savedInstanceState.getSerializable(MOVIE_SET_KEY))) {
            if (null == mMovieSet)
                mMovieSet = (MovieSet) savedInstanceState.getSerializable(MOVIE_SET_KEY);
            mMovieAdapter.swapMovieList(mMovieSet.getMovies());
        } else {
            populateRecycleView();
        }
    }

    public void populateRecycleView() {
        detachLikedViewModelObserver();
        switch (mSharedPrefs.getInt("sort_mode", R.id.action_sort_popular)) {
            case R.id.action_sort_top_rated:
                requestTMDB(mClient.listTopRatedMovies());
                break;
            case R.id.action_sort_liked:
                attachLikedViewModelObserver();
                break;
            case R.id.action_sort_popular:
            default:
                requestTMDB(mClient.listPopularMovies());
        }
    }

    // Default TMDB call
    public void requestTMDB(Call<MovieSet> call) {
        requestTMDB(call, -1);
    }

    // TMDB call with defined page number
    public void requestTMDB(Call<MovieSet> call, final int page) {
        if (page <= 0) {
            mStatusMessage.setVisibility(View.INVISIBLE);
            mStatusIcon.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
        call.enqueue(new Callback<MovieSet>() {
            @Override
            public void onResponse(Call<MovieSet> call, retrofit2.Response<MovieSet> response) {
                if (page <= 0) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                }
                if (response.code() == 200) {
                    if (page <= 0) {
                        mMovieSet = response.body();
                        mMovieAdapter.swapMovieList(mMovieSet.getMovies());
                        mRecyclerView.scrollToPosition(0);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        mMovieSet.addMovies(response.body());
                        mMovieAdapter.swapMovieList(mMovieSet.getMovies());
                    }
                } else {
                    if (page <= 0) {
                        mStatusIcon.setImageResource(R.drawable.ic_error_outline);
                        mStatusMessage.setText(R.string.error_unexpected_response);
                        mRecyclerView.setVisibility(View.INVISIBLE);
                        mStatusMessage.setVisibility(View.VISIBLE);
                        mStatusIcon.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(MainActivity.this, R.string.error_unexpected_response, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieSet> call, Throwable t) {
                if (Utilities.isOnline(getApplicationContext())) {
                    mStatusIcon.setImageResource(R.drawable.ic_error);
                    mStatusMessage.setText(R.string.error_problem_connecting);
                    if (page > 0) {
                        Toast.makeText(MainActivity.this, R.string.error_problem_connecting, Toast.LENGTH_LONG).show();
                    }
                } else {
                    mStatusIcon.setImageResource(R.drawable.ic_warning);
                    mStatusMessage.setText(R.string.error_no_connection);
                    if (page > 0) {
                        Toast.makeText(MainActivity.this, R.string.error_no_connection, Toast.LENGTH_LONG).show();
                    }
                }
                if (page <= 0) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    mStatusMessage.setVisibility(View.VISIBLE);
                    mStatusIcon.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(LAYOUT_MANAGER_STATE_KEY, mLayoutManager.onSaveInstanceState());
        // No need to preserve recycler view movie list for the liked set this way, cause that is managed by ViewModel as LiveData
        if (mSharedPrefs.getInt("sort_mode", R.id.action_sort_popular) != R.id.action_sort_liked) {
            outState.putSerializable(MOVIE_SET_KEY, mMovieSet);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (null != savedInstanceState) {
            if (savedInstanceState.containsKey(LAYOUT_MANAGER_STATE_KEY)
                    && null != savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE_KEY)) {
                mLayoutManagerState = savedInstanceState.getParcelable(LAYOUT_MANAGER_STATE_KEY);
            }
            if (savedInstanceState.containsKey(MOVIE_SET_KEY)
                    && null != savedInstanceState.getSerializable(MOVIE_SET_KEY)) {
                mMovieSet = (MovieSet) savedInstanceState.getSerializable(MOVIE_SET_KEY);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mLayoutManagerState) {
            mLayoutManager.onRestoreInstanceState(mLayoutManagerState);
            // Preventing recurring use of this state on the rest of onResume single calls (without onRestoreInstanceState)
            mLayoutManagerState = null;
        }
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.menu_sort_group == item.getGroupId()
                && item.getItemId() != mSharedPrefs.getInt("sort_mode", R.id.action_sort_popular)) {
            item.setChecked(true);
            mSharedPrefs
                    .edit()
                    .putInt("sort_mode", item.getItemId())
                    .commit();
            populateRecycleView();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movie) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra("movie", movie);

        startActivity(detailIntent);
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort, menu);
        int prevSortMode = mSharedPrefs.getInt("sort_mode", R.id.action_sort_popular);
        // Fix saved preference in case it's corrupted
        if (null == menu.findItem(prevSortMode)) {
            mSharedPrefs
                    .edit()
                    .putInt("sort_mode", R.id.action_sort_popular)
                    .commit();
            prevSortMode = R.id.action_sort_popular;
        }
        menu.findItem(prevSortMode).setChecked(true);
        return true;
    }

    private void attachLikedViewModelObserver() {
        mLikedViewModel.getLiked().observe(this, likedObserver);
        Log.d(TAG, "LikedViewModel observer attached");
    }

    private void detachLikedViewModelObserver() {
        mLikedViewModel.getLiked().removeObserver(likedObserver);
        Log.d(TAG, "LikedViewModel observer detached");
    }
}