package com.familyset.myapplication.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.familyset.myapplication.ui.main.blink.BlinkFragment;
import com.familyset.myapplication.ui.main.pose.PoseFragment;
import com.familyset.myapplication.ui.main.record.RecordFragment;
import com.familyset.myapplication.ui.main.setting.SetFragment;

public class MainAdapter extends FragmentStateAdapter {
    public MainAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0){
            return BlinkFragment.newInstance();
        }
        else if(position==1){
            return PoseFragment.newInstance();
        }
        else if(position==2){
            return RecordFragment.newInstance();
        }
        else
            return SetFragment.newInstance();
    }

    @Override
    public int getItemCount() {
        return 4;
    }

}
