package com.familyset.myapplication.data.api;

import com.familyset.myapplication.model.blink.PersonalInfo;
import com.familyset.myapplication.model.login.LoggedInUser;
import com.familyset.myapplication.model.login.Login;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UsersService {
    @POST("/api/auth/login")
    Call<APIResponse<LoggedInUser>> login(@Body Login login);

    @GET("/api/personalInfo/{id}")
    Call<APIResponse<List<PersonalInfo>>> getPersonalInfos(@Path("id") String userId);

    @POST("/api/personalInfo/{id}")
    Call<APIResponse<PersonalInfo>> savePersonalInfo(@Path("id") String userId, @Body PersonalInfo personalInfo);
}
