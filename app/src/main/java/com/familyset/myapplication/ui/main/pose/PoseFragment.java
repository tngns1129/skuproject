package com.familyset.myapplication.ui.main.pose;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

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
            startActivity(intent);
        });
        return binding.getRoot();
    }
}