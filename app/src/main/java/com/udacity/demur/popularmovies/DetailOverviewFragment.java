package com.udacity.demur.popularmovies;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.demur.popularmovies.databinding.DetailOverviewFragmentBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailOverviewFragment extends Fragment {
    private static final String TAG = DetailOverviewFragment.class.getSimpleName();

    public DetailOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DetailOverviewFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.detail_overview_fragment, container, false);
        binding.setMovie(((DetailActivity) getActivity()).getMovie());
        return binding.getRoot();
    }
}