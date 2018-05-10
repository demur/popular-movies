package com.udacity.demur.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.demur.popularmovies.model.Movie;

import java.util.List;

class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {
    private final Context mContext;
    private final MoviesAdapterOnClickHandler mClickHandler;
    private List<Movie> movieList;

    MoviesAdapter(@NonNull Context context, MoviesAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_rv_item, parent, false);
        view.setFocusable(true);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapterViewHolder holder, int position) {
        Movie theMovie = movieList.get(position);
        Picasso.get()
                .load("http://image.tmdb.org/t/p/w185/" + theMovie.getPoster_path())
                .fit()
                .into(holder.ivPoster);
        holder.tvTitle.setText(theMovie.getTitle());
        holder.ivPoster.setContentDescription("\"" + theMovie.getTitle() + "\" movie poster");
    }

    @Override
    public int getItemCount() {
        if (null == movieList) return 0;
        return movieList.size();
    }

    void swapMovieList(List<Movie> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView ivPoster;
        final TextView tvTitle;

        MoviesAdapterViewHolder(View itemView) {
            super(itemView);

            ivPoster = itemView.findViewById(R.id.iv_poster);
            tvTitle = itemView.findViewById(R.id.tv_title);

            itemView.setOnClickListener(this);
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