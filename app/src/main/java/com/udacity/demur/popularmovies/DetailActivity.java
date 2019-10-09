package com.udacity.demur.popularmovies;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

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
import com.udacity.demur.popularmovies.viewmodel.DetailActivityViewModel;

import java.util.List;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    private static final String THE_MOVIE_KEY = "the_movie";
    private static final String VIDEO_SET_KEY = "video_set";
    private static final String REVIEW_SET_KEY = "review_set";
    private static final String ENTER_ORIENTATION_KEY = "enter_orientation";
    private static final String TAG = DetailActivity.class.getSimpleName();
    private TMDBLikedDatabase mDb;
    private DetailsPagerAdapter mDetailsPagerAdapter;
    private DetailActivityViewModel viewModel;
    private Integer enterOrientation = null;

    ActivityDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(DetailActivityViewModel.class);
        if (null == enterOrientation) {
            enterOrientation = getResources().getConfiguration().orientation;
        }

        if (getIntent().hasExtra(MainActivity.EXTRA_MOVIE_KEY)) {
            supportPostponeEnterTransition();

            mDb = TMDBLikedDatabase.getInstance(getApplicationContext());

            mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            }

            viewModel.setMovie((Movie) getIntent().getSerializableExtra(MainActivity.EXTRA_MOVIE_KEY));
            mBinding.setMovie(viewModel.getMovie());

            // Check if this Activity was started from Liked list
            if (viewModel.getMovie().isLiked()) {
                mBinding.ibFavorite.setSelected(true);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final LikedEntry likedEntry = mDb.likedDao().loadLikedById(viewModel.getMovie().getId());
                        if (null == viewModel.getReviewSet().getValue()
                                && null != likedEntry.getReviewSetJson()) {
                            viewModel.setReviewSet(new Gson().fromJson(likedEntry.getReviewSetJson(), ReviewSet.class));
                        }
                        if (null == viewModel.getVideoSet().getValue()
                                && null != likedEntry.getVideoSetJson()) {
                            viewModel.setVideoSet(new Gson().fromJson(likedEntry.getVideoSetJson(), VideoSet.class));
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
                Picasso.get().load(getString(R.string.tmdb_path_poster, viewModel.getMovie().getPoster_path()))
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
                Picasso.get().load(getString(R.string.tmdb_path_backdrop, viewModel.getMovie().getBackdrop_path()))
                        .into(mBinding.ivDetailBackdrop);
                // This Activity was not started from Liked list, but lets check if the movie is liked
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final List<LikedEntry> likedEntries = mDb.likedDao().checkLikedById(viewModel.getMovie().getId());
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
                    viewModel.getMovie().getId(),
                    viewModel.getMovie().getTitle(),
                    viewModel.getMovie().getVote_average(),
                    null != mBinding.ivDetailPoster.getDrawable()
                            ? Utilities.getBitmapAsByteArray(((BitmapDrawable) mBinding.ivDetailPoster.getDrawable()).getBitmap())
                            : null,
                    null != mBinding.ivDetailBackdrop.getDrawable()
                            ? Utilities.getBitmapAsByteArray(((BitmapDrawable) mBinding.ivDetailBackdrop.getDrawable()).getBitmap())
                            : null,
                    new Gson().toJson(viewModel.getMovie()),
                    null != viewModel.getVideoSet().getValue()
                            ? new Gson().toJson(viewModel.getVideoSet().getValue())
                            : null,
                    null != viewModel.getReviewSet().getValue()
                            ? new Gson().toJson(viewModel.getReviewSet().getValue())
                            : null
            );
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.likedDao().insertLiked(likedEntry);
                }
            });
            viewModel.getMovie().setLiked(true);
        } else {
            final int id = viewModel.getMovie().getId();
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.likedDao().deleteLikedById(id);
                }
            });
            viewModel.getMovie().setLiked(false);
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
        outState.putSerializable(THE_MOVIE_KEY, viewModel.getMovie());
        outState.putSerializable(REVIEW_SET_KEY, viewModel.getReviewSet().getValue());
        outState.putSerializable(VIDEO_SET_KEY, viewModel.getVideoSet().getValue());
        outState.putInt(ENTER_ORIENTATION_KEY, enterOrientation);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (null != savedInstanceState) {
            if (null == viewModel.getMovie()
                    && savedInstanceState.containsKey(THE_MOVIE_KEY)
                    && null != savedInstanceState.getSerializable(THE_MOVIE_KEY)) {
                viewModel.setMovie((Movie) savedInstanceState.getSerializable(THE_MOVIE_KEY));
            }
            if (null == viewModel.getReviewSet().getValue()
                    && savedInstanceState.containsKey(REVIEW_SET_KEY)
                    && null != savedInstanceState.getSerializable(REVIEW_SET_KEY)) {
                viewModel.setReviewSet((ReviewSet) savedInstanceState.getSerializable(REVIEW_SET_KEY));
            }
            if (null == viewModel.getVideoSet().getValue()
                    && savedInstanceState.containsKey(VIDEO_SET_KEY)
                    && null != savedInstanceState.getSerializable(VIDEO_SET_KEY)) {
                viewModel.setVideoSet((VideoSet) savedInstanceState.getSerializable(VIDEO_SET_KEY));
            }
            if (savedInstanceState.containsKey(ENTER_ORIENTATION_KEY)) {
                enterOrientation = savedInstanceState.getInt(ENTER_ORIENTATION_KEY);
            }
        }
    }

    private void shareYouTubeLink() {
        if (null != viewModel.getVideoSet().getValue() && viewModel.getVideoSet().getValue().getVideos().size() > 0) {
            for (Video video : viewModel.getVideoSet().getValue().getVideos()) {
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
            /* Workaround for avoiding malformed shared element transition on return to parent activity
             *  when orientation of device is different from the one at the time of this activity launch */
            if (enterOrientation == getResources().getConfiguration().orientation) {
                supportFinishAfterTransition();
            } else {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
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