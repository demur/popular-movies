package com.udacity.demur.popularmovies;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.demur.popularmovies.model.Movie;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().hasExtra("movie")) {
            Movie theMovie = (Movie) getIntent().getSerializableExtra("movie");
            ImageView ivDetailPoster = findViewById(R.id.iv_detail_poster);
            ImageView ivDetailBackdrop = findViewById(R.id.iv_detail_backdrop);
            TextView tvDetailTitle = findViewById(R.id.tv_detail_title);
            TextView tvOverview = findViewById(R.id.tv_overview);
            TextView tvReleaseDate = findViewById(R.id.tv_release_date);
            TextView tvVoteAverage = findViewById(R.id.tv_vote_average);

            Picasso.get().load("http://image.tmdb.org/t/p/w185/" + theMovie.getPoster_path())
                    .fit().into(ivDetailPoster);
            Picasso.get().load("http://image.tmdb.org/t/p/w780/" + theMovie.getBackdrop_path())
                    .into(ivDetailBackdrop);
            tvDetailTitle.setText(theMovie.getTitle());
            ivDetailPoster.setContentDescription(getString(R.string.content_description_poster, theMovie.getTitle()));
            ivDetailBackdrop.setContentDescription(getString(R.string.content_description_backdrop_shot, theMovie.getTitle()));
            tvOverview.setText("\t\t" + theMovie.getOverview());
            tvReleaseDate.setText(theMovie.getRelease_date());
            tvVoteAverage.setText(getString(R.string.vote_average, String.valueOf(theMovie.getVote_average())));
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.error_no_data_for_detail_activity, Toast.LENGTH_LONG);
            TextView v = toast.getView().findViewById(android.R.id.message);
            if (null != v) v.setGravity(Gravity.CENTER);
            toast.show();
            NavUtils.navigateUpFromSameTask(this);
        }
    }
}