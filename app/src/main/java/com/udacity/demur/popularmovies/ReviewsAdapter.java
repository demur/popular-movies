package com.udacity.demur.popularmovies;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.udacity.demur.popularmovies.databinding.ReviewRvItemBinding;
import com.udacity.demur.popularmovies.model.Review;

import java.util.List;

class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {
    private final Context mContext;
    private List<Review> reviewList;

    ReviewsAdapter(@NonNull Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReviewRvItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.review_rv_item, parent, false);
        itemBinding.getRoot().setFocusable(true);
        return new ReviewsAdapterViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewsAdapterViewHolder holder, int position) {
        final Review theReview = reviewList.get(position);
        holder.bind(theReview);
    }

    @Override
    public int getItemCount() {
        if (null == reviewList) return 0;
        return reviewList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    void swapReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
        notifyDataSetChanged();
    }

    class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {
        private final ReviewRvItemBinding binding;

        ReviewsAdapterViewHolder(ReviewRvItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Review review) {
            binding.setReview(review);
            binding.executePendingBindings();
        }
    }
}