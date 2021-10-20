package com.familyset.myapplication.ui.blink;

import static android.Manifest.permission.CAMERA;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.familyset.myapplication.R;
import com.familyset.myapplication.databinding.FragmentBlinkBinding;

import org.opencv.android.CameraBridgeViewBase;

import java.util.Collections;
import java.util.List;

public class BlinkFragment extends Fragment{

    private CameraBridgeViewBase mOpenCvCameraView;
    private BlinkFragmentViewModel blinkFragmentViewModel;

    private FragmentBlinkBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentBlinkBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        //View view = inflater.inflate(R.layout.fragment_blink, container, false);
        mOpenCvCameraView = (CameraBridgeViewBase)view.findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.GONE);
        //mOpenCvCameraView.setCvCameraViewListener();
        mOpenCvCameraView.setCameraIndex(0);

        blinkFragmentViewModel = new BlinkFragmentViewModel(view, getActivity());

        return binding.getRoot();
    }

    private void initView() {

    }

    @Override
    public void onPause() {
        super.onPause();
        blinkFragmentViewModel.onpause();
    }

    @Override
    public void onResume() {
        super.onResume();
        blinkFragmentViewModel.onresume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        blinkFragmentViewModel.ondestroy();
    }

    //퍼미션 관련 메소드
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;

    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
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