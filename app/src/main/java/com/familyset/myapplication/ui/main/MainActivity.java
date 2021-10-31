package com.familyset.myapplication.ui.main;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.familyset.myapplication.R;
import com.familyset.myapplication.databinding.ActivityMainBinding;
import com.familyset.myapplication.ui.main.record.BlinkRecordFragment;
import com.familyset.myapplication.ui.main.record.PoseRecordFragment;
import com.familyset.myapplication.ui.main.record.RecordFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupView();
    }

    private void setupView(){
        binding.viewpager.setUserInputEnabled(false);
        binding.viewpager.setAdapter(
                new MainAdapter(this)
        );

        String[] tabNames = getResources().getStringArray(R.array.main_tab_name);
        new TabLayoutMediator(binding.tablayout, binding.viewpager, (tab, position) -> {
            TextView textView = new TextView(this);
            textView.setText(tabNames[position]);
            textView.setGravity(Gravity.CENTER);
            tab.setCustomView(textView);
        }).attach();
    }


}