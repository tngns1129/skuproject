package com.familyset.myapplication.ui.setting;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.familyset.myapplication.R;

public class SetFragmentViewModel {
    private View view;
    private Button button;
    private TextView textView;

    SetFragmentViewModel(View view){
        this.view = view;
        initView();
    }

    private void initView() {
        textView = view.findViewById(R.id.text);
        button = view.findViewById(R.id.button);
        textView.setText("Hello SettingFragment");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextView();
            }
        });
    }

    private void setTextView() {
        textView.setText("Hi Nice to meet you");
    }

}
