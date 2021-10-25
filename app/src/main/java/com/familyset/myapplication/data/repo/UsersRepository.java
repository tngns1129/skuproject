package com.familyset.myapplication.data.repo;

import com.familyset.myapplication.data.api.UsersService;
import com.familyset.myapplication.model.login.LoggedInUser;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Single;

public class UsersRepository {
    @Inject
    UsersService usersService;

    private LoggedInUser user;

    @Inject
    public UsersRepository(UsersService usersService) {
        this.usersService = usersService;
    }

    public Single<LoggedInUser> login(String username, String password) {
        return usersService.login(username, password)
                .doOnSuccess(this::setUser);
    }

    private void setUser(LoggedInUser user) {
        this.user = user;
    }

    public LoggedInUser getUser() {
        return user;
    }
}
