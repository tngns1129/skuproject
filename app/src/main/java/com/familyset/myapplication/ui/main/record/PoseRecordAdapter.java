package com.familyset.myapplication.ui.main.record;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.familyset.myapplication.databinding.LayoutPoseRecordBinding;
import com.familyset.myapplication.model.pose.Pose;

import java.text.SimpleDateFormat;

public class PoseRecordAdapter extends ListAdapter<Pose, PoseRecordAdapter.ViewHolder> {

    public PoseRecordAdapter() {
        this(new PoseDiffCallback());
    }

    protected PoseRecordAdapter(@NonNull DiffUtil.ItemCallback<Pose> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutPoseRecordBinding binding = LayoutPoseRecordBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LayoutPoseRecordBinding binding;
        SimpleDateFormat mFormat = new SimpleDateFormat("yy/MM/dd hh:mm");

        public ViewHolder(LayoutPoseRecordBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Pose pose) {
            binding.index.setText(String.valueOf(getAdapterPosition() + 1));
            binding.startTime.setText(mFormat.format(pose.getStartTime()));
            binding.finishTime.setText(pose.getDifference());
            binding.neckTime.setText(pose.getAllWrongNeckTimes());
            binding.waistTime.setText(pose.getAllWrongWaistTimes());
        }
    }
}

class PoseDiffCallback extends DiffUtil.ItemCallback<Pose> {

    @Override
    public boolean areItemsTheSame(@NonNull Pose oldItem, @NonNull Pose newItem) {
        return oldItem.getStartTime().getTime() == newItem.getStartTime().getTime();
    }

    @Override
    public boolean areContentsTheSame(@NonNull Pose oldItem, @NonNull Pose newItem) {
        return oldItem.getFinishTime().getTime() == newItem.getFinishTime().getTime();
    }
}