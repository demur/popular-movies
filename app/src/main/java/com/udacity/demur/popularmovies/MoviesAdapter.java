package com.udacity.demur.popularmovies;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.udacity.demur.popularmovies.database.TMDBLikedDatabase;
import com.udacity.demur.popularmovies.databinding.MovieRvItemBinding;
import com.udacity.demur.popularmovies.model.Movie;

import java.util.List;
import java.util.Map;

class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {
    private final Context mContext;
    private final MoviesAdapterOnClickHandler mClickHandler;
    private List<Movie> movieList;
    private TMDBLikedDatabase mDb;
    private Map<Integer, byte[]> mPosterMap;

    MoviesAdapter(@NonNull Context context, MoviesAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
        mDb = TMDBLikedDatabase.getInstance(context);
        setHasStableIds(false);
    }

    @NonNull
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MovieRvItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.movie_rv_item, parent, false);
        itemBinding.getRoot().setFocusable(true);
        return new MoviesAdapterViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MoviesAdapterViewHolder holder, int position) {
        final Movie theMovie = movieList.get(position);
        if (!theMovie.isLiked()) {
            Picasso.get()
                    .load(mContext.getString(R.string.tmdb_path_poster) + theMovie.getPoster_path())
                    .fit()
                    .into(holder.binding.ivPoster);
        } else {
            if (mPosterMap == null && ViewModelProviders.of((MainActivity) mContext).get(LikedViewModel.class).getPosterMap() != null) {
                mPosterMap = ViewModelProviders.of((MainActivity) mContext).get(LikedViewModel.class).getPosterMap();
            }
            if (mPosterMap != null && mPosterMap.containsKey(theMovie.getId()) && mPosterMap.get(theMovie.getId()) != null) {
                holder.binding.ivPoster.setImageBitmap(BitmapFactory.decodeByteArray(
                        mPosterMap.get(theMovie.getId()),
                        0,
                        mPosterMap.get(theMovie.getId()).length
                ));
            }
        }
        holder.binding.tvTitle.setText(theMovie.getTitle());
        holder.binding.ivPoster.setContentDescription(mContext.getString(R.string.content_description_poster, theMovie.getTitle()));
    }

    @Override
    public int getItemCount() {
        if (null == movieList) return 0;
        return movieList.size();
    }

    @Override
    public long getItemId(int position) {
        return movieList.get(position).getId();
    }

    void swapMovieList(List<Movie> movieList) {
        mPosterMap = null;
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final MovieRvItemBinding binding;

        MoviesAdapterViewHolder(MovieRvItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            this.binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (null != mClickHandler) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mClickHandler.onClick(movieList.get(position));
                }
            }
        }
    }
}