package com.familyset.myapplication.ui.main.blink;

import static android.Manifest.permission.CAMERA;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.familyset.myapplication.R;
import com.familyset.myapplication.databinding.FragmentBlinkBinding;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BlinkFragment extends Fragment{

    private BlinkFragmentViewModel viewModel = null;

    private FragmentBlinkBinding binding;

    String firstset;

    public BlinkFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBlinkBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(BlinkFragmentViewModel.class);
        initView();
        observeLiveData();
        return binding.getRoot();
    }

    private void initView() {
        binding.onoff.setBackgroundColor(Color.GRAY);
        binding.onoff.setText("OFF");
        binding.onoff.setOnClickListener(v -> {
            if (viewModel.isRunning()) {
                viewModel.stop();
            } else {
                viewModel.start(
                        getContext(),
                        binding.firstspiner.getSelectedItem().toString(),
                        binding.secondspiner.getSelectedItem().toString()
                );
            }

        });
        binding.templereset.setOnClickListener(v -> viewModel.resetLearnFrames());

        binding.firstset.setText("알림 받을 거리");
        binding.secondset.setText("두번째 설정");
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.blinkfragmentfirstspinner, R.layout.support_simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.blinkfragmentsecondspinner, R.layout.support_simple_spinner_dropdown_item);
        binding.firstspiner.setAdapter(adapter1);
        binding.secondspiner.setAdapter(adapter2);

        binding.secondset.setVisibility(View.GONE);
        binding.secondspiner.setVisibility(View.GONE);

        firstset = binding.firstspiner.getSelectedItem().toString();
        firstset = firstset.substring(0,2);

        binding.activitySurfaceView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {

            }

            @Override
            public void onCameraViewStopped() {

            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                return viewModel.drawEyes(inputFrame);
            }
        });
    }

    private static Toast sToast;
    public static void showToast(Context context, String message) {
        if (sToast == null) {
            sToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(message);
        }
        sToast.show();
    }


    private void observeLiveData() {
        viewModel.distance.observe(getViewLifecycleOwner(), distance -> {
            binding.distance.setText(distance);
            if (Double.parseDouble(distance) < Double.parseDouble(firstset)) {
                showToast(getActivity(), "핸드폰을 멀리하세요!!!");
            }

        });



        viewModel.distanceAvg.observe(getViewLifecycleOwner(), distanceAvg -> binding.distanceAvg.setText(distanceAvg));

        viewModel.blink.observe(getViewLifecycleOwner(), blink -> binding.blink.setText(blink));

        viewModel.templeset.observe(getViewLifecycleOwner(), templeset -> binding.templeset.setText(templeset));

        viewModel.running.observe(getViewLifecycleOwner(), running -> {
            if (running) {
                showBlinkUI();
            } else {
                hideBlinkUI();
            }
        });
    }

    private void showBlinkUI() {
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        binding.activitySurfaceView.enableFpsMeter();
        binding.activitySurfaceView.setCameraIndex(1);
        binding.activitySurfaceView.enableView();

        binding.firstspiner.setVisibility(View.GONE);
        binding.secondspiner.setVisibility(View.GONE);
        binding.firstset.setVisibility(View.GONE);
        binding.secondset.setVisibility(View.GONE);

        binding.onoff.setText("ON");
        binding.onoff.setBackgroundColor(Color.RED);

        binding.templereset.setVisibility(View.VISIBLE);
        binding.info.setVisibility(View.VISIBLE);
        binding.activitySurfaceView.setVisibility(View.VISIBLE);

        binding.blink.setVisibility(View.GONE);
        binding.distanceAvg.setVisibility(View.GONE);
    }

    private void hideBlinkUI() {
        binding.firstspiner.setVisibility(View.VISIBLE);
        //binding.secondspiner.setVisibility(View.VISIBLE);
        binding.firstset.setVisibility(View.VISIBLE);
        //binding.secondset.setVisibility(View.VISIBLE);

        binding.onoff.setText("OFF");
        binding.onoff.setBackgroundColor(Color.GRAY);

        binding.info.setVisibility(View.GONE);
        binding.activitySurfaceView.setVisibility(View.GONE);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("통계").setMessage(viewModel.getAllPersonalInfo()).create().show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (viewModel.isRunning()) {
            binding.activitySurfaceView.disableView();
        }

        Log.d("onpause", "onpause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onresume", "onresume");

        if (viewModel.isRunning()) {
            binding.activitySurfaceView.enableView();
        }

        //if (!viewModel.isRunning()) {
        //    viewModel.initOpenCV(getContext());
        //}
        /*
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, fragmentActivity, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        */
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel.isRunning()) {
            binding.activitySurfaceView.disableView();
            viewModel.stop();
        }
    }

    //퍼미션 관련 메소드
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;

    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(binding.activitySurfaceView);
    }

    protected void onCameraPermissionGranted() {
        List<? extends CameraBridgeViewBase> cameraViews = getCameraViewList();
        if (cameraViews == null) {
            return;
        }
        for (CameraBridgeViewBase cameraBridgeViewBase: cameraViews) {
            if (cameraBridgeViewBase != null) {
                cameraBridgeViewBase.setCameraPermissionGranted();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean havePermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                havePermission = false;
            }
        }
        if (havePermission) {
            onCameraPermissionGranted();
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onCameraPermissionGranted();
        }else{
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity());
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                getActivity().finish();
            }
        });
        builder.create().show();
    }


}