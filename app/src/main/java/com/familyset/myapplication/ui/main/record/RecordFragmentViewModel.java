package com.familyset.myapplication.ui.main.record;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModel;

import com.familyset.myapplication.R;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RecordFragmentViewModel extends ViewModel {

    @Inject
    public RecordFragmentViewModel() {}
}
