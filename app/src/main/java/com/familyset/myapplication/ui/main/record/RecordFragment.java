package com.familyset.myapplication.ui.main.record;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.familyset.myapplication.R;
import com.familyset.myapplication.databinding.FragmentRecordBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RecordFragment extends Fragment {

    private FragmentRecordBinding binding;

    private RecordFragmentViewModel viewModel;

    public static RecordFragment newInstance() {
        RecordFragment rf = new RecordFragment();
        return rf;
    }

    private RecordFragment() {
        viewModel = new ViewModelProvider(this).get(RecordFragmentViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecordBinding.inflate(getLayoutInflater());

        //setupView();

        return binding.getRoot();
    }

    private void setupView(){
        /*
        binding.recordviewpager.setAdapter(
                new RecordAdapter(this)
        );

        ViewPager2 viewpager = binding.recordviewpager;
        viewpager.setAdapter(new RecordAdapter(this));
        TabLayout tabLayout = binding.recordtablayout;
        String[] tabNames = getResources().getStringArray(R.array.setting_tab_name);
        new TabLayoutMediator(tabLayout, binding.recordviewpager, (tab, position) -> {
            TextView textView = new TextView(getActivity());
            textView.setText(tabNames[position]);
            textView.setGravity(Gravity.CENTER);
            tab.setCustomView(textView);
        }).attach();

        tabLayout.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewpager.setCurrentItem(1);
            }
        });
         */

    }
}