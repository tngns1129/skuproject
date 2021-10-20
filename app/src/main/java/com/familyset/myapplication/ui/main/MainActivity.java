package com.familyset.myapplication.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.familyset.myapplication.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private FragmentStateAdapter adapter;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupView();
        mainViewModel = new MainViewModel(MainActivity.this, viewPager2);
    }

    private void setupView(){
        viewPager2 = findViewById(R.id.viewpager);
        adapter = new MainAdapter(this);
        viewPager2.setAdapter(adapter);
    }



}