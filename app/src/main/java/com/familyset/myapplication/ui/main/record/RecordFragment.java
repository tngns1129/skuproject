package com.familyset.myapplication.ui.main.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.familyset.myapplication.R;
import com.familyset.myapplication.databinding.FragmentRecordBinding;

public class RecordFragment extends Fragment {

    private FragmentRecordBinding binding;

    public static RecordFragment newInstance() {
        RecordFragment rf = new RecordFragment();
        return rf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecordBinding.inflate(getLayoutInflater());
        binding.text.setText("Hello Record Fragment");
        binding.button.setOnClickListener(view -> setTextView());

        return binding.getRoot();
    }

    private void setTextView() {
        binding.text.setText("Hi Nice to meet you");
    }

}