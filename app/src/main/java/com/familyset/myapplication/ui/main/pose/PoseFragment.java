package com.familyset.myapplication.ui.main.pose;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.familyset.myapplication.R;
import com.familyset.myapplication.databinding.FragmentPoseBinding;

import java.util.Objects;

public class PoseFragment extends Fragment {

    private FragmentPoseBinding binding;

    public static PoseFragment newInstance() {
        PoseFragment pf = new PoseFragment();
        return pf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPoseBinding.inflate(getLayoutInflater());
        binding.text.setText("Hello Pose Fragment");
        binding.button.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.ricardotejo.openpose.MocapActivity.class);
            requireActivity().startActivity(intent);
        });

        return binding.getRoot();
    }
}