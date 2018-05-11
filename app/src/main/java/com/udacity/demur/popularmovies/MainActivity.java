package com.udacity.demur.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.demur.popularmovies.model.Movie;
import com.udacity.demur.popularmovies.model.MovieSet;
import com.udacity.demur.popularmovies.service.TMDbClient;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        mLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mMovieAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(mMovieAdapter);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                        .build();

                Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        mClient = retrofit.create(TMDbClient.class);

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
        switch (mSharedPrefs.getInt("sort_mode", R.id.action_sort_popular)) {
            case R.id.action_sort_top_rated:
                requestTMDB(mClient.listTopRatedMovies());
                break;
            case R.id.action_sort_popular:
            default:
                requestTMDB(mClient.listPopularMovies());
        }
    }

    public void requestTMDB(Call<MovieSet> call) {
        mStatusMessage.setVisibility(View.INVISIBLE);
        mStatusIcon.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<MovieSet>() {
            @Override
            public void onResponse(Call<MovieSet> call, retrofit2.Response<MovieSet> response) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                if (response.code() == 200) {
                    mMovieSet = response.body();
                    mMovieAdapter.swapMovieList(mMovieSet.getMovies());
                    mRecyclerView.scrollToPosition(0);
                    mRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    mStatusIcon.setImageResource(R.drawable.ic_error_outline_black_24dp);
                    mStatusMessage.setText(R.string.error_unexpected_response);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    mStatusMessage.setVisibility(View.VISIBLE);
                    mStatusIcon.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<MovieSet> call, Throwable t) {
                if (isOnline()) {
                    mStatusIcon.setImageResource(R.drawable.ic_error_black_24dp);
                    mStatusMessage.setText(R.string.error_problem_connecting);
                } else {
                    mStatusIcon.setImageResource(R.drawable.ic_warning_black_24dp);
                    mStatusMessage.setText(R.string.error_no_connection);
                }
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                mStatusMessage.setVisibility(View.VISIBLE);
                mStatusIcon.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(LAYOUT_MANAGER_STATE_KEY, mLayoutManager.onSaveInstanceState());
        outState.putSerializable(MOVIE_SET_KEY, mMovieSet);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (null != savedInstanceState) {
            if (savedInstanceState.containsKey(LAYOUT_MANAGER_STATE_KEY)
                    && null != savedInstanceState.getSerializable(LAYOUT_MANAGER_STATE_KEY)) {
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
        if (null != mLayoutManagerState)
            mLayoutManager.onRestoreInstanceState(mLayoutManagerState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort, menu);
        menu.findItem(mSharedPrefs.getInt("sort_mode", R.id.action_sort_popular)).setChecked(true);
        return true;
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

    /*
     * This function is the product of gar at https://stackoverflow.com/a/4009133
     * suggested to use by Udacity to implement network connection check
     * */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}