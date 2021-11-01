package com.familyset.myapplication.data.repo;

import android.util.Log;

import com.familyset.myapplication.data.api.APIResponse;
import com.familyset.myapplication.data.api.UsersService;
import com.familyset.myapplication.model.pose.Pose;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;

public class PoseRespository {
    private UsersService usersService;

    @Inject
    public PoseRespository(UsersService usersService) {
        this.usersService = usersService;
    }

    public Observable<List<Pose>> getPoses(String userId) {
        return Observable.create(observableOnSubscribe -> {
            try {
                Response<APIResponse<List<Pose>>> response = usersService.getPoses(userId).execute();
                if (response.isSuccessful() && response.body() != null) {
                    observableOnSubscribe.onNext(response.body().getData());
                } else {
                    Throwable throwable = new Throwable(response.message());
                    observableOnSubscribe.onError(throwable);
                }
            } catch (Exception e) {
                observableOnSubscribe.onError(e);
            }
        });
    }

    public Single<Pose> savePose(String userId, Pose pose) {
        return Single.create(singleOnSubscribe -> {
            try {
                Response<APIResponse<Pose>> response = usersService.savePose(userId, pose).execute();
                if (response.isSuccessful() && response.body() != null) {
                    singleOnSubscribe.onSuccess(response.body().getData());
                } else {
                    Throwable throwable = new Throwable(response.message());
                    singleOnSubscribe.onError(throwable);
                }
            } catch (Exception e) {
                singleOnSubscribe.onError(e);
            }
        });
    }
}
