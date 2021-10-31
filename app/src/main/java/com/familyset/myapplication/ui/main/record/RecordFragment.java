package com.familyset.myapplication.ui.main.record;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.familyset.myapplication.R;
import com.familyset.myapplication.RecordAdapter;
import com.familyset.myapplication.databinding.FragmentRecordBinding;
import com.familyset.myapplication.ui.main.MainAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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

        setupView();


        return binding.getRoot();
    }

    private void setupView(){
        binding.viewpager.setAdapter(new RecordAdapter(this));
        String[] tabNames = getResources().getStringArray(R.array.setting_tab_name);
        new TabLayoutMediator(binding.tablayout, binding.viewpager, (tab, position) -> {
            TextView textView = new TextView(getActivity());
            textView.setText(tabNames[position]);
            textView.setGravity(Gravity.CENTER);
            tab.setCustomView(textView);
        }).attach();
    }
}