package com.udacity.demur.popularmovies;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.demur.popularmovies.databinding.DetailOverviewFragmentBinding;
import com.udacity.demur.popularmovies.viewmodel.DetailActivityViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailOverviewFragment extends Fragment {
    private static final String TAG = DetailOverviewFragment.class.getSimpleName();
    private DetailActivityViewModel viewModel;

    public DetailOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != getActivity()) {
            viewModel = ViewModelProviders.of(getActivity()).get(DetailActivityViewModel.class);
        }
        DetailOverviewFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.detail_overview_fragment, container, false);
        binding.setMovie(viewModel.getMovie());
        return binding.getRoot();
    }
}