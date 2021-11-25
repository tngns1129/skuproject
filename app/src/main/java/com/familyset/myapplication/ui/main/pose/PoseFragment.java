package com.familyset.myapplication.ui.main.pose;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.familyset.myapplication.R;
import com.familyset.myapplication.databinding.FragmentPoseBinding;
import com.familyset.myapplication.model.pose.Pose;

import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PoseFragment extends Fragment {

    private FragmentPoseBinding binding;

    private PoseFragmentViewModel viewModel;

    public PoseFragment() {}

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        // Handle the Intent
                        String a = intent.getStringExtra("pose");
                        String b = intent.getStringExtra("poseObject");
                        List<String> c = intent.getParcelableExtra("imageDir");
                        Pose pose = new Pose(b);
                        viewModel.savePose(pose);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("통계").setMessage(a).create();

                        LayoutInflater factory = LayoutInflater.from(requireContext());
                        View view = factory.inflate(R.layout.image, null);
                        ImageView imageView = view.findViewById(R.id.dialog_imageview);

                        if (c != null) {
                            for(String str : c){
                                Bitmap bm = BitmapFactory.decodeFile(str);
                                imageView.setImageBitmap(bm);
                                builder.setView(view);
                            }
                        }

                        builder.show();
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPoseBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(PoseFragmentViewModel.class);

        binding.button.setText("Start");

        binding.firstset.setText("안좋은 자세 판단 민감도");
        binding.secondset.setText("카메라 모드");
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.posefragmentfirstspinner, R.layout.support_simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.posefragmentsecondspinner, R.layout.support_simple_spinner_dropdown_item);
        binding.firstspiner.setAdapter(adapter1);
        binding.secondspiner.setAdapter(adapter2);


        binding.button.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), com.ricardotejo.openpose.MocapActivity.class);
            intent.putExtra("firstSet", binding.firstspiner.getSelectedItem().toString());
            intent.putExtra("firstSetId", Integer.toString(binding.firstspiner.getSelectedItemPosition()));
            intent.putExtra("secondSetId", Integer.toString(binding.secondspiner.getSelectedItemPosition()));
            mStartForResult.launch(intent);
        });
        return binding.getRoot();
    }
}