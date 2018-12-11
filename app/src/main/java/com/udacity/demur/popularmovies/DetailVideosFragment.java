package com.udacity.demur.popularmovies;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.demur.popularmovies.databinding.DetailVideosFragmentBinding;
import com.udacity.demur.popularmovies.model.Video;
import com.udacity.demur.popularmovies.model.VideoSet;
import com.udacity.demur.popularmovies.service.RetrofitClient;
import com.udacity.demur.popularmovies.service.TMDbClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailVideosFragment extends Fragment implements VideosAdapter.OnVideoClicked {
    private static final String TAG = DetailVideosFragment.class.getSimpleName();
    private static final String VIDEO_SET_KEY = "video_set";
    private static int mVideosGridSpanCount = 2;
    private LinearLayoutManager mVideosLayoutManager;
    private VideosAdapter mVideoAdapter;
    private VideoSet mVideoSet;
    private TMDbClient mVideoClient;
    DetailVideosFragmentBinding mBinding;

    public DetailVideosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.detail_videos_fragment, container, false);
        mBinding.rvVideos.setHasFixedSize(true);
        mVideosLayoutManager = new GridLayoutManager(getActivity(), mVideosGridSpanCount);
        mBinding.rvVideos.setLayoutManager(mVideosLayoutManager);
        mVideoAdapter = new VideosAdapter(getActivity());
        mBinding.rvVideos.setAdapter(mVideoAdapter);
        mVideoAdapter.setOnClick(this);
        if (null == mVideoSet) {
            mVideoSet = ((DetailActivity) getActivity()).getVideoSet();
        }
        if (null != mVideoSet || (null != savedInstanceState
                && savedInstanceState.containsKey(VIDEO_SET_KEY)
                && null != savedInstanceState.getSerializable(VIDEO_SET_KEY))) {
            if (null == mVideoSet)
                mVideoSet = (VideoSet) savedInstanceState.getSerializable(VIDEO_SET_KEY);
            mVideoAdapter.swapVideoList(mVideoSet.getVideos());
        } else {
            populateVideosRecycleView();
        }
        return mBinding.getRoot();
    }

    private void populateVideosRecycleView() {
        mVideoClient = RetrofitClient.getInstance(getActivity().getApplicationContext());
        mBinding.tvVideosMessage.setVisibility(View.INVISIBLE);
        mBinding.ivVideosMessageIcon.setVisibility(View.INVISIBLE);
        mBinding.pbVideosLoadingIndicator.setVisibility(View.VISIBLE);
        mBinding.rvVideos.setVisibility(View.INVISIBLE);
        mVideoClient.getVideos(((DetailActivity) getActivity()).getMovie().getId()).enqueue(new Callback<VideoSet>() {
            @Override
            public void onResponse(@NonNull Call<VideoSet> call, @NonNull Response<VideoSet> response) {
                mBinding.pbVideosLoadingIndicator.setVisibility(View.INVISIBLE);
                if (response.code() == 200) {
                    mVideoSet = response.body();
                    ((DetailActivity) getActivity()).setVideoSet(mVideoSet);
                    mVideoAdapter.swapVideoList(mVideoSet.getVideos());
                    mBinding.rvVideos.scrollToPosition(0);
                    mBinding.rvVideos.setVisibility(View.VISIBLE);
                } else {
                    mBinding.ivVideosMessageIcon.setImageResource(R.drawable.ic_error_outline);
                    mBinding.tvVideosMessage.setText(R.string.error_unexpected_response);
                    mBinding.rvVideos.setVisibility(View.INVISIBLE);
                    mBinding.tvVideosMessage.setVisibility(View.VISIBLE);
                    mBinding.ivVideosMessageIcon.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<VideoSet> call, @NonNull Throwable t) {
                if (Utilities.isOnline()) {
                    mBinding.ivVideosMessageIcon.setImageResource(R.drawable.ic_error);
                    mBinding.tvVideosMessage.setText(R.string.error_problem_connecting);
                } else {
                    mBinding.ivVideosMessageIcon.setImageResource(R.drawable.ic_warning);
                    mBinding.tvVideosMessage.setText(R.string.error_no_connection);
                }
                mBinding.pbVideosLoadingIndicator.setVisibility(View.INVISIBLE);
                mBinding.rvVideos.setVisibility(View.INVISIBLE);
                mBinding.tvVideosMessage.setVisibility(View.VISIBLE);
                mBinding.ivVideosMessageIcon.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onVideoClick(Video video) {
        Uri uri = Uri.parse(getActivity().getString(R.string.youtube_thumbnail, video.getKey()));
        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, uri);
        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(youtubeIntent, 0);
        boolean isIntentSafe = activities.size() > 0;
        if (isIntentSafe) {
            startActivity(youtubeIntent);
        }

    }
}