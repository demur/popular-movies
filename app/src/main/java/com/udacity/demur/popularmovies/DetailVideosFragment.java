package com.udacity.demur.popularmovies;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private RecyclerView mVideosRecyclerView;
    private TextView mVideosStatusMessage;
    private ImageView mVideosStatusIcon;
    private ProgressBar mVideosLoadingIndicator;
    private LinearLayoutManager mVideosLayoutManager;
    private VideosAdapter mVideoAdapter;
    private VideoSet mVideoSet;
    private TMDbClient mVideoClient;

    public DetailVideosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_videos_fragment, container, false);
        mVideosLoadingIndicator = view.findViewById(R.id.pb_videos_loading_indicator);
        mVideosStatusIcon = view.findViewById(R.id.iv_videos_message_icon);
        mVideosStatusMessage = view.findViewById(R.id.tv_videos_message);
        mVideosRecyclerView = view.findViewById(R.id.rv_videos);
        mVideosRecyclerView.setHasFixedSize(true);
        mVideosLayoutManager = new GridLayoutManager(getActivity(), mVideosGridSpanCount);
        mVideosRecyclerView.setLayoutManager(mVideosLayoutManager);
        mVideoAdapter = new VideosAdapter(getActivity());
        mVideosRecyclerView.setAdapter(mVideoAdapter);
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
        return view;
    }

    private void populateVideosRecycleView() {
        mVideoClient = RetrofitClient.getInstance(getActivity().getApplicationContext());
        mVideosStatusMessage.setVisibility(View.INVISIBLE);
        mVideosStatusIcon.setVisibility(View.INVISIBLE);
        mVideosLoadingIndicator.setVisibility(View.VISIBLE);
        mVideosRecyclerView.setVisibility(View.INVISIBLE);
        mVideoClient.getVideos(((DetailActivity) getActivity()).getMovie().getId()).enqueue(new Callback<VideoSet>() {
            @Override
            public void onResponse(Call<VideoSet> call, Response<VideoSet> response) {
                mVideosLoadingIndicator.setVisibility(View.INVISIBLE);
                if (response.code() == 200) {
                    mVideoSet = response.body();
                    ((DetailActivity) getActivity()).setVideoSet(mVideoSet);
                    mVideoAdapter.swapVideoList(mVideoSet.getVideos());
                    mVideosRecyclerView.scrollToPosition(0);
                    mVideosRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    mVideosStatusIcon.setImageResource(R.drawable.ic_error_outline);
                    mVideosStatusMessage.setText(R.string.error_unexpected_response);
                    mVideosRecyclerView.setVisibility(View.INVISIBLE);
                    mVideosStatusMessage.setVisibility(View.VISIBLE);
                    mVideosStatusIcon.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<VideoSet> call, Throwable t) {
                if (Utilities.isOnline(getActivity().getApplicationContext())) {
                    mVideosStatusIcon.setImageResource(R.drawable.ic_error);
                    mVideosStatusMessage.setText(R.string.error_problem_connecting);
                } else {
                    mVideosStatusIcon.setImageResource(R.drawable.ic_warning);
                    mVideosStatusMessage.setText(R.string.error_no_connection);
                }
                mVideosLoadingIndicator.setVisibility(View.INVISIBLE);
                mVideosRecyclerView.setVisibility(View.INVISIBLE);
                mVideosStatusMessage.setVisibility(View.VISIBLE);
                mVideosStatusIcon.setVisibility(View.VISIBLE);
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