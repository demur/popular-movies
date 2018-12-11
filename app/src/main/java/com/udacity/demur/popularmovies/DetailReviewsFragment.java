package com.udacity.demur.popularmovies;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.demur.popularmovies.databinding.DetailReviewsFragmentBinding;
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
    private LinearLayoutManager mReviewsLayoutManager;
    private ReviewsAdapter mReviewAdapter;
    private ReviewSet mReviewSet;
    private TMDbClient mReviewClient;
    DetailReviewsFragmentBinding mBinding;

    public DetailReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.detail_reviews_fragment, container, false);

        mBinding.rvReviews.setHasFixedSize(false);
        mReviewsLayoutManager = new LinearLayoutManager(getActivity());
        mBinding.rvReviews.setLayoutManager(mReviewsLayoutManager);
        mReviewAdapter = new ReviewsAdapter(getActivity());
        mBinding.rvReviews.setAdapter(mReviewAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mBinding.rvReviews.getContext(),
                mReviewsLayoutManager.getOrientation());
        mBinding.rvReviews.addItemDecoration(dividerItemDecoration);
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
        return mBinding.getRoot();
    }

    private void populateReviewsRecycleView() {
        mReviewClient = RetrofitClient.getInstance(getActivity().getApplicationContext());
        mBinding.tvReviewsMessage.setVisibility(View.INVISIBLE);
        mBinding.ivReviewsMessageIcon.setVisibility(View.INVISIBLE);
        mBinding.pbReviewsLoadingIndicator.setVisibility(View.VISIBLE);
        mBinding.rvReviews.setVisibility(View.INVISIBLE);
        mReviewClient.getReviews(((DetailActivity) getActivity()).getMovie().getId()).enqueue(new Callback<ReviewSet>() {
            @Override
            public void onResponse(@NonNull Call<ReviewSet> call, @NonNull Response<ReviewSet> response) {
                mBinding.pbReviewsLoadingIndicator.setVisibility(View.INVISIBLE);
                if (response.code() == 200) {
                    mReviewSet = response.body();
                    ((DetailActivity) getActivity()).setReviewSet(mReviewSet);
                    mReviewAdapter.swapReviewList(mReviewSet.getReviews());
                    mBinding.rvReviews.scrollToPosition(0);
                    mBinding.rvReviews.setVisibility(View.VISIBLE);
                } else {
                    mBinding.ivReviewsMessageIcon.setImageResource(R.drawable.ic_error_outline);
                    mBinding.tvReviewsMessage.setText(R.string.error_unexpected_response);
                    mBinding.rvReviews.setVisibility(View.INVISIBLE);
                    mBinding.tvReviewsMessage.setVisibility(View.VISIBLE);
                    mBinding.ivReviewsMessageIcon.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReviewSet> call, @NonNull Throwable t) {
                if (Utilities.isOnline()) {
                    mBinding.ivReviewsMessageIcon.setImageResource(R.drawable.ic_error);
                    mBinding.tvReviewsMessage.setText(R.string.error_problem_connecting);
                } else {
                    mBinding.ivReviewsMessageIcon.setImageResource(R.drawable.ic_warning);
                    mBinding.tvReviewsMessage.setText(R.string.error_no_connection);
                }
                mBinding.pbReviewsLoadingIndicator.setVisibility(View.INVISIBLE);
                mBinding.rvReviews.setVisibility(View.INVISIBLE);
                mBinding.tvReviewsMessage.setVisibility(View.VISIBLE);
                mBinding.ivReviewsMessageIcon.setVisibility(View.VISIBLE);
            }
        });
    }
}