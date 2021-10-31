package com.familyset.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.familyset.myapplication.ui.main.record.BlinkRecordFragment;
import com.familyset.myapplication.ui.main.record.PoseRecordFragment;


public class RecordAdapter extends FragmentStateAdapter {
    public RecordAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0){
            return new BlinkRecordFragment();
        }
        else{
            return new PoseRecordFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}