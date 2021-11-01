package com.familyset.myapplication.ui.main.pose;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;

import com.familyset.myapplication.R;
import com.familyset.myapplication.data.repo.PersonalInfoRepository;
import com.familyset.myapplication.data.repo.PoseRespository;
import com.familyset.myapplication.data.repo.UsersRepository;
import com.familyset.myapplication.model.pose.Pose;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class PoseFragmentViewModel extends ViewModel {
    private UsersRepository usersRepository;

    private PoseRespository poseRespository;

    @Inject
    public PoseFragmentViewModel(UsersRepository usersRepository, PoseRespository poseRespository) {
        this.usersRepository = usersRepository;
        this.poseRespository = poseRespository;
    }

    public void savePose(Pose pose) {
        poseRespository.savePose(usersRepository.getUser().getUserId(), pose)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        p -> {
                            Log.d("PFVM", "save");
                        },
                        error -> {
                            Log.d("PFVM", error.toString());
                        }
                );
    }
}
