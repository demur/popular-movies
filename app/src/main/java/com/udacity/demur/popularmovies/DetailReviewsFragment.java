package com.udacity.demur.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.demur.popularmovies.model.ReviewSet;
import com.udacity.demur.popularmovies.service.RetrofitClient;
import com.udacity.demur.popularmovies.service.TMDbClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailReviewsFragment extends Fragment {
    private static final String TAG = DetailReviewsFragment.class.getSimpleName();
    private static final String REVIEW_SET_KEY = "review_set";
    private RecyclerView mReviewsRecyclerView;
    private TextView mReviewsStatusMessage;
    private ImageView mReviewsStatusIcon;
    private ProgressBar mReviewsLoadingIndicator;
    private LinearLayoutManager mReviewsLayoutManager;
    private ReviewsAdapter mReviewAdapter;
    private ReviewSet mReviewSet;
    private TMDbClient mReviewClient;

    public DetailReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_reviews_fragment, container, false);

        mReviewsLoadingIndicator = view.findViewById(R.id.pb_reviews_loading_indicator);
        mReviewsStatusIcon = view.findViewById(R.id.iv_reviews_message_icon);
        mReviewsStatusMessage = view.findViewById(R.id.tv_reviews_message);
        mReviewsRecyclerView = view.findViewById(R.id.rv_reviews);
        mReviewsRecyclerView.setHasFixedSize(false);
        mReviewsLayoutManager = new LinearLayoutManager(getActivity());
        mReviewsRecyclerView.setLayoutManager(mReviewsLayoutManager);
        mReviewAdapter = new ReviewsAdapter(getActivity());
        mReviewsRecyclerView.setAdapter(mReviewAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mReviewsRecyclerView.getContext(),
                mReviewsLayoutManager.getOrientation());
        mReviewsRecyclerView.addItemDecoration(dividerItemDecoration);
        if (null == mReviewSet) {
            mReviewSet = ((DetailActivity) getActivity()).getReviewSet();
        }
        if (null != mReviewSet || (null != savedInstanceState
                && savedInstanceState.containsKey(REVIEW_SET_KEY)
                && null != savedInstanceState.getSerializable(REVIEW_SET_KEY))) {
            if (null == mReviewSet)
                mReviewSet = (ReviewSet) savedInstanceState.getSerializable(REVIEW_SET_KEY);
            mReviewAdapter.swapReviewList(mReviewSet.getReviews());
        } else {
            populateReviewsRecycleView();
        }
        return view;
    }

    private void populateReviewsRecycleView() {
        mReviewClient = RetrofitClient.getInstance(getActivity().getApplicationContext());
        mReviewsStatusMessage.setVisibility(View.INVISIBLE);
        mReviewsStatusIcon.setVisibility(View.INVISIBLE);
        mReviewsLoadingIndicator.setVisibility(View.VISIBLE);
        mReviewsRecyclerView.setVisibility(View.INVISIBLE);
        mReviewClient.getReviews(((DetailActivity) getActivity()).getMovie().getId()).enqueue(new Callback<ReviewSet>() {
            @Override
            public void onResponse(Call<ReviewSet> call, Response<ReviewSet> response) {
                mReviewsLoadingIndicator.setVisibility(View.INVISIBLE);
                if (response.code() == 200) {
                    mReviewSet = response.body();
                    ((DetailActivity) getActivity()).setReviewSet(mReviewSet);
                    mReviewAdapter.swapReviewList(mReviewSet.getReviews());
                    mReviewsRecyclerView.scrollToPosition(0);
                    mReviewsRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    mReviewsStatusIcon.setImageResource(R.drawable.ic_error_outline);
                    mReviewsStatusMessage.setText(R.string.error_unexpected_response);
                    mReviewsRecyclerView.setVisibility(View.INVISIBLE);
                    mReviewsStatusMessage.setVisibility(View.VISIBLE);
                    mReviewsStatusIcon.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ReviewSet> call, Throwable t) {
                if (Utilities.isOnline(getActivity().getApplicationContext())) {
                    mReviewsStatusIcon.setImageResource(R.drawable.ic_error);
                    mReviewsStatusMessage.setText(R.string.error_problem_connecting);
                } else {
                    mReviewsStatusIcon.setImageResource(R.drawable.ic_warning);
                    mReviewsStatusMessage.setText(R.string.error_no_connection);
                }
                mReviewsLoadingIndicator.setVisibility(View.INVISIBLE);
                mReviewsRecyclerView.setVisibility(View.INVISIBLE);
                mReviewsStatusMessage.setVisibility(View.VISIBLE);
                mReviewsStatusIcon.setVisibility(View.VISIBLE);
            }
        });
    }
}