package com.udacity.demur.popularmovies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

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