package com.familyset.myapplication;

import android.app.Activity;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class MainViewModel {

    private Activity mainActivity;
    private ViewPager2 viewPager2;
    private final List<String> tabElement = Arrays.asList("사용","미사용","통계","설정");
    private TabLayout tabLayout;

    MainViewModel(Activity activity, ViewPager2 viewPager2){
        this.mainActivity = activity;
        this.viewPager2 = viewPager2;
        initView();
    }

    private void initView(){
        tabLayout = mainActivity.findViewById(R.id.tablayout);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView textView = new TextView(mainActivity);
                textView.setText(tabElement.get(position));
                textView.setGravity(Gravity.CENTER);
                tab.setCustomView(textView);
            }
        }).attach();
    }
}
