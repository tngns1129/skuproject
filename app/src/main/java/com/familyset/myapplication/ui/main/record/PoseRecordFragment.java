package com.familyset.myapplication.ui.main.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.familyset.myapplication.databinding.FragmentPoseRecordBinding;
import com.familyset.myapplication.ui.main.blink.BlinkFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PoseRecordFragment extends Fragment {

    private FragmentPoseRecordBinding binding;

    private PoseRecordViewModel viewModel;

    private PoseRecordAdapter adapter;

    public PoseRecordFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPoseRecordBinding.inflate(getLayoutInflater());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        adapter = new PoseRecordAdapter();
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        viewModel = new ViewModelProvider(this).get(PoseRecordViewModel.class);
        observe();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadPoses();
    }

    private void observe() {
        viewModel.items.observe(getViewLifecycleOwner(), items -> {
            adapter.submitList(items);
        });
    }
}
