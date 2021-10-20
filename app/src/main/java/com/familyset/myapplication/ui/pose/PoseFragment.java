package com.familyset.myapplication.ui.pose;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.familyset.myapplication.R;

public class PoseFragment extends Fragment {

    private PoseFragmentViewModel poseFragmentViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pose, container, false);
        poseFragmentViewModel = new PoseFragmentViewModel(view, getActivity());
        return view;

    }

}