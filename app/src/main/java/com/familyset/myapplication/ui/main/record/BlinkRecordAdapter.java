package com.familyset.myapplication.ui.main.record;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.familyset.myapplication.databinding.LayoutBlinkRecordBinding;
import com.familyset.myapplication.model.blink.PersonalInfo;

public class BlinkRecordAdapter extends ListAdapter<PersonalInfo, BlinkRecordAdapter.ViewHolder> {

    private BlinkRecordViewModel viewModel;

    public BlinkRecordAdapter() {
        this(new PersonalInfoDiffCallback());
    }

    protected BlinkRecordAdapter(@NonNull DiffUtil.ItemCallback<PersonalInfo> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutBlinkRecordBinding binding = LayoutBlinkRecordBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PersonalInfo personalInfo = getItem(position);

        holder.bind(personalInfo);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LayoutBlinkRecordBinding binding;

        public ViewHolder(LayoutBlinkRecordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PersonalInfo personalInfo) {
            binding.index.setText(getAdapterPosition());
            binding.startTime.setText("2011111111");
            binding.finishTime.setText("2012312111");
            binding.distanceAvg.setText("30");
            binding.blink.setText(personalInfo.getBlinkNumber());
        }
    }
}

class PersonalInfoDiffCallback extends DiffUtil.ItemCallback<PersonalInfo> {

    @Override
    public boolean areItemsTheSame(@NonNull PersonalInfo oldItem, @NonNull PersonalInfo newItem) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(@NonNull PersonalInfo oldItem, @NonNull PersonalInfo newItem) {
        return false;
    }
}