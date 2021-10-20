package com.familyset.myapplication.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.familyset.myapplication.ui.blink.BlinkFragment;
import com.familyset.myapplication.ui.pose.PoseFragment;
import com.familyset.myapplication.ui.record.RecordFragment;
import com.familyset.myapplication.ui.setting.SetFragment;

public class MainAdapter extends FragmentStateAdapter {
    public MainAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0){
            return new BlinkFragment();
        }
        else if(position==1){
            return new PoseFragment();
        }
        else if(position==2){
            return new RecordFragment();
        }
        else
            return new SetFragment();
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
