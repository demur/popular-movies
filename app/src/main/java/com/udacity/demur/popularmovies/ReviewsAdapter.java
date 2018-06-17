package com.udacity.demur.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_rv_item, parent, false);
        view.setFocusable(true);
        return new ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReviewsAdapterViewHolder holder, int position) {
        final Review theReview = reviewList.get(position);

        holder.tvAuthor.setText(theReview.getAuthor());
        holder.tvContent.setText(theReview.getContent());
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
        final TextView tvAuthor;
        final TextView tvContent;

        ReviewsAdapterViewHolder(View itemView) {
            super(itemView);

            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvContent = itemView.findViewById(R.id.tv_content);
        }
    }
}