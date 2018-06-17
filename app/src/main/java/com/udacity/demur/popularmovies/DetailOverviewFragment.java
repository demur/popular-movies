package com.udacity.demur.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailOverviewFragment extends Fragment {
    private static final String TAG = DetailOverviewFragment.class.getSimpleName();

    public DetailOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_overview_fragment, container, false);
        TextView tvOverview = view.findViewById(R.id.tv_overview);
        TextView tvReleaseDate = view.findViewById(R.id.tv_release_date);
        TextView tvVoteAverage = view.findViewById(R.id.tv_vote_average);
        if (null != tvOverview) {
            tvOverview.setText(getString(R.string.first_line_indent, ((DetailActivity) getActivity()).getMovie().getOverview()));
        }
        if (null != tvReleaseDate) {
            tvReleaseDate.setText(
                    ((DetailActivity) getActivity()).getMovie().getRelease_date());
        }
        if (null != tvVoteAverage) {
            tvVoteAverage.setText(
                    getString(R.string.vote_average,
                            String.valueOf(
                                    ((DetailActivity) getActivity()).getMovie().getVote_average())));
        }
        return view;
    }
}