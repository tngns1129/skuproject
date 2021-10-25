package com.familyset.myapplication.ui.main.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.familyset.myapplication.R;
import com.familyset.myapplication.databinding.FragmentSetBinding;

public class SetFragment extends Fragment {

    private FragmentSetBinding binding;

    public static SetFragment newInstance() {
        SetFragment sf = new SetFragment();
        return sf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSetBinding.inflate(getLayoutInflater());
        binding.text.setText("Hello SettingFragment");
        binding.button.setOnClickListener(view -> setTextView());

        return binding.getRoot();
    }

    private void setTextView() {
        binding.text.setText("Hi Nice to meet you");
    }
}