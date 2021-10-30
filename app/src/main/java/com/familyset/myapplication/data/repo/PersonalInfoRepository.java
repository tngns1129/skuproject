package com.familyset.myapplication.data.repo;

import android.util.Log;

import com.familyset.myapplication.data.api.APIResponse;
import com.familyset.myapplication.data.api.UsersService;
import com.familyset.myapplication.model.blink.PersonalInfo;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;

public class PersonalInfoRepository {

    private UsersService usersService;

    @Inject
    public PersonalInfoRepository(UsersService usersService) {
        this.usersService = usersService;
    }

    public Observable<List<PersonalInfo>> getPersonalInfos(String userId) {
        return Observable.create(observableOnSubscribe -> {
            try {
                Response<APIResponse<List<PersonalInfo>>> response = usersService.getPersonalInfos(userId).execute();
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

    public Single<PersonalInfo> savePersonalInfo(String userId, PersonalInfo personalInfo) {
        return Single.create(singleOnSubscribe -> {
            try {
                Response<APIResponse<PersonalInfo>> response = usersService.savePersonalInfo(userId, personalInfo).execute();
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("PRepository", response.body().getData().toString());
                    singleOnSubscribe.onSuccess(response.body().getData());
                } else {
                    Log.d("PRepository", response.message());
                    Throwable throwable = new Throwable(response.message());
                    singleOnSubscribe.onError(throwable);
                }
            } catch (Exception e) {
                Log.d("PRepository", e.getMessage());
                singleOnSubscribe.onError(e);
            }
        });
    }
}