package com.familyset.myapplication.ui.main.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.familyset.myapplication.databinding.FragmentBlinkRecordBinding;
import com.familyset.myapplication.ui.main.blink.BlinkFragment;

public class BlinkRecordFragment extends Fragment {

    private FragmentBlinkRecordBinding binding;

    public static BlinkRecordFragment newInstance() {
        BlinkRecordFragment bp = new BlinkRecordFragment();
        return bp;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBlinkRecordBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}
