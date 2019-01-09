package com.udacity.demur.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.demur.popularmovies.adapter.ReviewsAdapter;
import com.udacity.demur.popularmovies.databinding.DetailReviewsFragmentBinding;
import com.udacity.demur.popularmovies.model.ReviewSet;
import com.udacity.demur.popularmovies.service.RecyclerViewStateHelper;
import com.udacity.demur.popularmovies.service.RetrofitClient;
import com.udacity.demur.popularmovies.service.TMDbClient;
import com.udacity.demur.popularmovies.viewmodel.DetailActivityViewModel;

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
    private TMDbClient mReviewClient;
    DetailReviewsFragmentBinding mBinding;
    private RecyclerViewStateHelper rvHelper = new RecyclerViewStateHelper();
    private DetailActivityViewModel viewModel;

    public DetailReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null != getActivity()) {
            viewModel = ViewModelProviders.of(getActivity()).get(DetailActivityViewModel.class);
        }
        mBinding = DataBindingUtil.inflate(inflater, R.layout.detail_reviews_fragment, container, false);
        mBinding.setRvHelper(rvHelper);

        mBinding.rvReviews.setHasFixedSize(false);
        mReviewsLayoutManager = new LinearLayoutManager(getActivity());
        mBinding.rvReviews.setLayoutManager(mReviewsLayoutManager);
        mReviewAdapter = new ReviewsAdapter(getActivity());
        mBinding.rvReviews.setAdapter(mReviewAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mBinding.rvReviews.getContext(),
                mReviewsLayoutManager.getOrientation());
        mBinding.rvReviews.addItemDecoration(dividerItemDecoration);
        viewModel.getReviewSet().observe(this, new Observer<ReviewSet>() {
            @Override
            public void onChanged(@Nullable ReviewSet reviewSet) {
                mReviewAdapter.swapReviewList(null != reviewSet ? reviewSet.getReviews() : null);
                mBinding.rvReviews.scrollToPosition(0);
            }
        });
        if (null == viewModel.getReviewSet().getValue()) {
            if (null != savedInstanceState
                    && savedInstanceState.containsKey(REVIEW_SET_KEY)
                    && null != savedInstanceState.getSerializable(REVIEW_SET_KEY)) {
                viewModel.setReviewSet((ReviewSet) savedInstanceState.getSerializable(REVIEW_SET_KEY));
            } else {
                populateReviewsRecycleView();
            }
        }
        return mBinding.getRoot();
    }

    private void populateReviewsRecycleView() {
        if (null != getActivity()) {
            mReviewClient = RetrofitClient.getInstance(getActivity().getApplicationContext());
            rvHelper.setLoadingState(true);
            rvHelper.setErrorState(false);
            mReviewClient.getReviews(viewModel.getMovie().getId()).enqueue(new Callback<ReviewSet>() {
                @Override
                public void onResponse(@NonNull Call<ReviewSet> call, @NonNull Response<ReviewSet> response) {
                    rvHelper.setLoadingState(false);
                    if (response.code() == 200) {
                        viewModel.setReviewSet(response.body());
                    } else {
                        mBinding.ivReviewsMessageIcon.setImageResource(R.drawable.ic_error_outline);
                        mBinding.tvReviewsMessage.setText(R.string.error_unexpected_response);
                        rvHelper.setErrorState(true);
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
                    rvHelper.setLoadingState(false);
                    rvHelper.setErrorState(true);
                }
            });
        }
    }
}