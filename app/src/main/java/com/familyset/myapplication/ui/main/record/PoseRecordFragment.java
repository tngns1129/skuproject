package com.familyset.myapplication.ui.main.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.familyset.myapplication.databinding.FragmentPoseRecordBinding;
import com.familyset.myapplication.ui.main.blink.BlinkFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PoseRecordFragment extends Fragment {

    private FragmentPoseRecordBinding binding;

    public PoseRecordFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPoseRecordBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}
