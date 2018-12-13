package com.udacity.demur.popularmovies;

import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.udacity.demur.popularmovies.database.LikedEntry;
import com.udacity.demur.popularmovies.database.TMDBLikedDatabase;
import com.udacity.demur.popularmovies.databinding.ActivityDetailBinding;
import com.udacity.demur.popularmovies.model.Movie;
import com.udacity.demur.popularmovies.model.ReviewSet;
import com.udacity.demur.popularmovies.model.Video;
import com.udacity.demur.popularmovies.model.VideoSet;

import java.util.List;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    private static final String THE_MOVIE_KEY = "the_movie";
    private static final String VIDEO_SET_KEY = "video_set";
    private static final String REVIEW_SET_KEY = "review_set";
    private static final String TAG = DetailActivity.class.getSimpleName();
    private TMDBLikedDatabase mDb;
    private Movie theMovie;
    private DetailsPagerAdapter mDetailsPagerAdapter;
    private VideoSet mVideoSet;
    private ReviewSet mReviewSet;

    ActivityDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(MainActivity.EXTRA_MOVIE_KEY)) {
            supportPostponeEnterTransition();

            mDb = TMDBLikedDatabase.getInstance(getApplicationContext());

            mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            }

            theMovie = (Movie) getIntent().getSerializableExtra(MainActivity.EXTRA_MOVIE_KEY);
            mBinding.setMovie(this.theMovie);

            // Check if this Activity was started from Liked list
            if (theMovie.isLiked()) {
                mBinding.ibFavorite.setSelected(true);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final LikedEntry likedEntry = mDb.likedDao().loadLikedById(theMovie.getId());
                        if (null != likedEntry.getReviewSetJson()) {
                            mReviewSet = new Gson().fromJson(likedEntry.getReviewSetJson(), ReviewSet.class);
                        }
                        if (null != likedEntry.getVideoSetJson()) {
                            mVideoSet = new Gson().fromJson(likedEntry.getVideoSetJson(), VideoSet.class);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (null != likedEntry.getPoster()) {
                                    mBinding.ivDetailPoster.setImageBitmap(BitmapFactory.decodeByteArray(
                                            likedEntry.getPoster(),
                                            0,
                                            likedEntry.getPoster().length
                                    ));
                                }
                                if (null != likedEntry.getBackdrop()) {
                                    mBinding.ivDetailBackdrop.setImageBitmap(BitmapFactory.decodeByteArray(
                                            likedEntry.getBackdrop(),
                                            0,
                                            likedEntry.getBackdrop().length
                                    ));
                                }
                                scheduleSupportStartPostponedTransition(mBinding.ivDetailPoster);
                            }
                        });
                    }
                });
            } else {
                Picasso.get().load(getString(R.string.tmdb_path_poster) + theMovie.getPoster_path())
                        .fit().into(mBinding.ivDetailPoster, new Callback() {
                    @Override
                    public void onSuccess() {
                        scheduleSupportStartPostponedTransition(mBinding.ivDetailPoster);
                    }

                    @Override
                    public void onError(Exception e) {
                        scheduleSupportStartPostponedTransition(mBinding.ivDetailPoster);
                    }
                });
                Picasso.get().load(getString(R.string.tmdb_path_backdrop) + theMovie.getBackdrop_path())
                        .into(mBinding.ivDetailBackdrop);
                // This Activity was not started from Liked list, but lets check if the movie is liked
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final List<LikedEntry> likedEntries = mDb.likedDao().checkLikedById(theMovie.getId());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (likedEntries.size() > 0) {
                                    mBinding.ibFavorite.setSelected(true);
                                }
                            }
                        });
                    }
                });
            }
            setupViewPager(mBinding.vpContainer);

            mBinding.tabs.setupWithViewPager(mBinding.vpContainer);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.error_no_data_for_detail_activity, Toast.LENGTH_LONG);
            TextView v = toast.getView().findViewById(android.R.id.message);
            if (null != v) v.setGravity(Gravity.CENTER);
            toast.show();
            NavUtils.navigateUpFromSameTask(this);
        }
    }

    public void ibFavoriteClickHandler(View target) {
        if (!mBinding.ibFavorite.isSelected()) {
            final LikedEntry likedEntry = new LikedEntry(
                    theMovie.getId(),
                    theMovie.getTitle(),
                    theMovie.getVote_average(),
                    null != mBinding.ivDetailPoster.getDrawable()
                            ? Utilities.getBitmapAsByteArray(((BitmapDrawable) mBinding.ivDetailPoster.getDrawable()).getBitmap())
                            : null,
                    null != mBinding.ivDetailBackdrop.getDrawable()
                            ? Utilities.getBitmapAsByteArray(((BitmapDrawable) mBinding.ivDetailBackdrop.getDrawable()).getBitmap())
                            : null,
                    new Gson().toJson(theMovie),
                    null != mVideoSet
                            ? new Gson().toJson(mVideoSet)
                            : null,
                    null != mReviewSet
                            ? new Gson().toJson(mReviewSet)
                            : null
            );
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.likedDao().insertLiked(likedEntry);
                }
            });
            theMovie.setLiked(true);
        } else {
            final int id = theMovie.getId();
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.likedDao().deleteLikedById(id);
                }
            });
            theMovie.setLiked(false);
        }
        mBinding.ibFavorite.setSelected(!mBinding.ibFavorite.isSelected());
    }

    private void setupViewPager(ViewPager viewPager) {
        mDetailsPagerAdapter = new DetailsPagerAdapter(getSupportFragmentManager());
        mDetailsPagerAdapter.addFragment(new DetailOverviewFragment(), getString(R.string.tab_title_overview));
        mDetailsPagerAdapter.addFragment(new DetailVideosFragment(), getString(R.string.tab_title_videos));
        mDetailsPagerAdapter.addFragment(new DetailReviewsFragment(), getString(R.string.tab_title_reviews));
        viewPager.setAdapter(mDetailsPagerAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(THE_MOVIE_KEY, theMovie);
        outState.putSerializable(REVIEW_SET_KEY, mReviewSet);
        outState.putSerializable(VIDEO_SET_KEY, mVideoSet);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (null != savedInstanceState) {
            if (savedInstanceState.containsKey(THE_MOVIE_KEY)
                    && null != savedInstanceState.getSerializable(THE_MOVIE_KEY)) {
                theMovie = (Movie) savedInstanceState.getSerializable(THE_MOVIE_KEY);
            }
            if (savedInstanceState.containsKey(REVIEW_SET_KEY)
                    && null != savedInstanceState.getSerializable(REVIEW_SET_KEY)) {
                mReviewSet = (ReviewSet) savedInstanceState.getSerializable(REVIEW_SET_KEY);
            }
            if (savedInstanceState.containsKey(VIDEO_SET_KEY)
                    && null != savedInstanceState.getSerializable(VIDEO_SET_KEY)) {
                mVideoSet = (VideoSet) savedInstanceState.getSerializable(VIDEO_SET_KEY);
            }
        }
    }

    private void shareYouTubeLink() {
        if (null != mVideoSet && mVideoSet.getVideos().size() > 0) {
            for (Video video : mVideoSet.getVideos()) {
                if (video.getSite().equalsIgnoreCase("YouTube")) {
                    ShareCompat.IntentBuilder.from(this)
                            .setType("text/plain")
                            .setText(video.getName() + " " + video.getType() + "\n"
                                    + getString(R.string.youtube_link, video.getKey()))
                            .setChooserTitle(R.string.share_youtube_link_choose_title)
                            .startChooser();
                    return;
                }
            }
        }
        Toast.makeText(DetailActivity.this, R.string.nothing_to_share, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            shareYouTubeLink();
        } else if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Movie getMovie() {
        return theMovie;
    }

    public VideoSet getVideoSet() {
        return mVideoSet;
    }

    public void setVideoSet(VideoSet videoSet) {
        mVideoSet = videoSet;
        if (theMovie.isLiked()) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.likedDao().updateVideoSetJson(theMovie.getId(), new Gson().toJson(mVideoSet));
                }
            });
        }
    }

    public ReviewSet getReviewSet() {
        return mReviewSet;
    }

    public void setReviewSet(ReviewSet reviewSet) {
        mReviewSet = reviewSet;
        if (theMovie.isLiked()) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.likedDao().updateReviewSetJson(theMovie.getId(), new Gson().toJson(mReviewSet));
                }
            });
        }
    }

    private void scheduleSupportStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                DetailActivity.this.supportStartPostponedEnterTransition();
                return true;
            }
        });
    }
}