package com.familyset.myapplication.data.api;

import com.familyset.myapplication.model.login.LoggedInUser;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UsersService {
    @GET("/users/login")
    Single<LoggedInUser> login(@Query("username") String username, @Query("password") String password);
}
