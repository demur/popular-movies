package com.udacity.demur.popularmovies;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.udacity.demur.popularmovies.databinding.VideoRvItemBinding;
import com.udacity.demur.popularmovies.model.Video;

import java.util.List;

class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosAdapterViewHolder> {
    private final Context mContext;
    private List<Video> videoList;
    private OnVideoClicked onClick;

    VideosAdapter(@NonNull Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public VideosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VideoRvItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.video_rv_item, parent, false);
        itemBinding.getRoot().setFocusable(true);
        return new VideosAdapterViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideosAdapterViewHolder holder, int position) {
        final Video theVideo = videoList.get(position);
        holder.bind(theVideo);

        if (theVideo.getSite().equalsIgnoreCase("YouTube")) {
            Picasso.get()
                    .load(mContext.getString(R.string.youtube_thumbnail, theVideo.getKey()))
                    .fit()
                    .into(holder.binding.ivVideoThumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.binding.tvVideoName.setVisibility(View.VISIBLE);
                            holder.binding.ivVideoPlayIcon.setVisibility(View.GONE);
                        }
                    });
        } else {
            holder.binding.tvVideoName.setVisibility(View.VISIBLE);
            holder.binding.ivVideoPlayIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (null == videoList) return 0;
        return videoList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    void swapVideoList(List<Video> videoList) {
        this.videoList = videoList;
        notifyDataSetChanged();
    }

    public void setOnClick(OnVideoClicked onClick) {
        this.onClick = onClick;
    }

    public interface OnVideoClicked {
        void onVideoClick(Video video);
    }

    class VideosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final VideoRvItemBinding binding;

        VideosAdapterViewHolder(VideoRvItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(this);
        }

        void bind(Video video) {
            binding.setVideo(video);
            binding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            if (null != onClick) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onClick.onVideoClick(videoList.get(position));
                }
            }
        }
    }
}