package com.familyset.myapplication.ui.main.record;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.familyset.myapplication.data.repo.PoseRespository;
import com.familyset.myapplication.data.repo.UsersRepository;
import com.familyset.myapplication.model.pose.Pose;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class PoseRecordViewModel extends ViewModel {
    private UsersRepository usersRepository;

    private PoseRespository poseRespository;

    private MutableLiveData<List<Pose>> _items = new MutableLiveData<>();
    public LiveData<List<Pose>> items = _items;

    @Inject
    public PoseRecordViewModel(UsersRepository usersRepository, PoseRespository poseRespository) {
        this.usersRepository = usersRepository;
        this.poseRespository = poseRespository;
    }

    public void loadPoses() {
        poseRespository.getPoses(usersRepository.getUser().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        poses -> {
                            Log.d("PRVM", String.valueOf(poses.size()));
                            _items.setValue(poses);
                        },
                        error -> {
                            Log.d("PoseRecordVM", "Sub err");
                        }
                );
    }

}

