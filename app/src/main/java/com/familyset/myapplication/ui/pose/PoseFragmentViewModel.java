package com.familyset.myapplication.ui.pose;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.familyset.myapplication.R;
import com.ricardotejo.openpose.MocapActivity;

public class PoseFragmentViewModel {
    private View view;
    private Button button;
    private TextView textView;
    private FragmentActivity fragmentActivity;

    PoseFragmentViewModel(View view, FragmentActivity fragmentActivity){
        this.view = view;
        this.fragmentActivity = fragmentActivity;
        initView();
    }

    private void initView() {
        textView = view.findViewById(R.id.text);
        button = view.findViewById(R.id.button);
        textView.setText("Hello Pose Fragment");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), com.ricardotejo.openpose.MocapActivity.class);
                fragmentActivity.startActivity(intent);
            }
        });
    }

    private void setTextView() {
        textView.setText("Hi Nice to meet you");
    }

}
