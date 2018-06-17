package com.udacity.demur.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.video_rv_item, parent, false);
        view.setFocusable(true);
        return new VideosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideosAdapterViewHolder holder, int position) {
        final Video theVideo = videoList.get(position);

        holder.tvVideoName.setText(theVideo.getName());
        if (theVideo.getSite().equalsIgnoreCase("YouTube")) {
            Picasso.get()
                    .load(mContext.getString(R.string.youtube_thumbnail, theVideo.getKey()))
                    .fit()
                    .into(holder.ivVideoThumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.tvVideoName.setVisibility(View.VISIBLE);
                            holder.ivVideoPlayIcon.setVisibility(View.GONE);
                        }
                    });
        } else {
            holder.tvVideoName.setVisibility(View.VISIBLE);
            holder.ivVideoPlayIcon.setVisibility(View.GONE);
        }
        holder.ivVideoThumbnail.setContentDescription(theVideo.getName() + " " + theVideo.getType());
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
        final TextView tvVideoName;
        final ImageView ivVideoThumbnail;
        final ImageView ivVideoPlayIcon;

        VideosAdapterViewHolder(View itemView) {
            super(itemView);

            tvVideoName = itemView.findViewById(R.id.tv_video_name);
            ivVideoThumbnail = itemView.findViewById(R.id.iv_video_thumbnail);
            ivVideoPlayIcon = itemView.findViewById(R.id.iv_video_play_icon);
            itemView.setOnClickListener(this);
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