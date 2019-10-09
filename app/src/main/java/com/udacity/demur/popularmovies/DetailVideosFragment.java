package com.udacity.demur.popularmovies;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.udacity.demur.popularmovies.adapter.VideosAdapter;
import com.udacity.demur.popularmovies.databinding.DetailVideosFragmentBinding;
import com.udacity.demur.popularmovies.model.Video;
import com.udacity.demur.popularmovies.model.VideoSet;
import com.udacity.demur.popularmovies.service.RecyclerViewStateHelper;
import com.udacity.demur.popularmovies.service.RetrofitClient;
import com.udacity.demur.popularmovies.service.TMDbClient;
import com.udacity.demur.popularmovies.viewmodel.DetailActivityViewModel;

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
    private TMDbClient mVideoClient;
    DetailVideosFragmentBinding mBinding;
    private RecyclerViewStateHelper rvHelper = new RecyclerViewStateHelper();
    private DetailActivityViewModel viewModel;

    public DetailVideosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null != getActivity()) {
            viewModel = ViewModelProviders.of(getActivity()).get(DetailActivityViewModel.class);
        }
        mBinding = DataBindingUtil.inflate(inflater, R.layout.detail_videos_fragment, container, false);
        mBinding.setRvHelper(rvHelper);
        mBinding.rvVideos.setHasFixedSize(true);
        mVideosLayoutManager = new GridLayoutManager(getActivity(), mVideosGridSpanCount);
        mBinding.rvVideos.setLayoutManager(mVideosLayoutManager);
        mVideoAdapter = new VideosAdapter(getActivity());
        mBinding.rvVideos.setAdapter(mVideoAdapter);
        mVideoAdapter.setOnClick(this);
        viewModel.getVideoSet().observe(this, new Observer<VideoSet>() {
            @Override
            public void onChanged(@Nullable VideoSet videoSet) {
                mVideoAdapter.swapVideoList(null != videoSet ? videoSet.getVideos() : null);
                mBinding.rvVideos.scrollToPosition(0);
            }
        });
        if (null == viewModel.getVideoSet().getValue()) {
            if (null != savedInstanceState
                    && savedInstanceState.containsKey(VIDEO_SET_KEY)
                    && null != savedInstanceState.getSerializable(VIDEO_SET_KEY)) {
                viewModel.setVideoSet((VideoSet) savedInstanceState.getSerializable(VIDEO_SET_KEY));
            } else {
                populateVideosRecycleView();
            }
        }
        return mBinding.getRoot();
    }

    private void populateVideosRecycleView() {
        if (null != getActivity()) {
            mVideoClient = RetrofitClient.getInstance(getActivity().getApplicationContext());
            rvHelper.setLoadingState(true);
            rvHelper.setErrorState(false);
            mVideoClient.getVideos(viewModel.getMovie().getId()).enqueue(new Callback<VideoSet>() {
                @Override
                public void onResponse(@NonNull Call<VideoSet> call, @NonNull Response<VideoSet> response) {
                    rvHelper.setLoadingState(false);
                    if (response.code() == 200) {
                        viewModel.setVideoSet(response.body());
                    } else {
                        mBinding.ivVideosMessageIcon.setImageResource(R.drawable.ic_error_outline);
                        mBinding.tvVideosMessage.setText(R.string.error_unexpected_response);
                        rvHelper.setErrorState(true);
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
                    rvHelper.setLoadingState(false);
                    rvHelper.setErrorState(true);
                }
            });
        }
    }

    @Override
    public void onVideoClick(Video video) {
        if (null != getActivity()) {
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
}