package com.familyset.myapplication.ui.main.record;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.familyset.myapplication.databinding.LayoutBlinkRecordBinding;
import com.familyset.myapplication.model.blink.PersonalInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        SimpleDateFormat mFormat = new SimpleDateFormat("yy/MM/dd hh:mm");

        public ViewHolder(LayoutBlinkRecordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PersonalInfo personalInfo) {
            binding.index.setText(String.valueOf(getAdapterPosition() + 1));
            Log.d("ggg", personalInfo.getStartTime().toString());
            binding.startTime.setText(mFormat.format(personalInfo.getStartTime()));
            binding.finishTime.setText(personalInfo.executeTime());
            binding.distanceAvg.setText(String.format("%.2f",personalInfo.getEyeDistanceAvg()));
            binding.blink.setText(String.valueOf(personalInfo.getBlinkNumber()));
        }
    }
}

class PersonalInfoDiffCallback extends DiffUtil.ItemCallback<PersonalInfo> {

    @Override
    public boolean areItemsTheSame(@NonNull PersonalInfo oldItem, @NonNull PersonalInfo newItem) {
        return oldItem.getStartTime().getTime() == newItem.getStartTime().getTime();
    }

    @Override
    public boolean areContentsTheSame(@NonNull PersonalInfo oldItem, @NonNull PersonalInfo newItem) {
        return oldItem.getFinishTime().getTime() == newItem.getFinishTime().getTime();
    }
}