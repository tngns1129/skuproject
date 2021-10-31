package com.familyset.myapplication.data.repo;

import android.accounts.NetworkErrorException;
import android.util.Log;

import com.familyset.myapplication.data.api.APIResponse;
import com.familyset.myapplication.data.api.UsersService;
import com.familyset.myapplication.model.login.LoggedInUser;
import com.familyset.myapplication.model.login.Login;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Response;

public class UsersRepository {
    @Inject
    UsersService usersService;

    private LoggedInUser user;

    @Inject
    public UsersRepository(UsersService usersService) {
        this.usersService = usersService;
    }

    public Single<LoggedInUser> login(Login login) {
        return Single.create(singleOnSubscribe -> {
            try {
                Response<APIResponse<LoggedInUser>> response = usersService.login(login).execute();
                if (response.isSuccessful() && response.body() != null) {
                    this.setUser(response.body().getData());
                    singleOnSubscribe.onSuccess(this.user);
                } else {
                    Throwable throwable = new Throwable(response.message());
                    singleOnSubscribe.onError(throwable);
                }
            } catch (Exception e) {
                singleOnSubscribe.onError(e);
            }
        });
    }

    private void setUser(LoggedInUser user) {
        this.user = user;
    }

    public LoggedInUser getUser() {
        return user;
    }
}
