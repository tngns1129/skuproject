package com.familyset.myapplication.ui.login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.familyset.myapplication.R;
import com.familyset.myapplication.model.login.LoginFormState;
import com.familyset.myapplication.model.login.LoginResponse;
import com.familyset.myapplication.data.repo.UsersRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class LoginViewModel extends ViewModel {

    private UsersRepository usersRepository;

    @Inject
    public LoginViewModel(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    private final MutableLiveData<LoginFormState> _loginFormState = new MutableLiveData<>();
    public LiveData<LoginFormState> loginFormState = _loginFormState;

    private final MutableLiveData<LoginResponse> _loginResponse = new MutableLiveData<>();
    public LiveData<LoginResponse> loginResponse = _loginResponse;

    public void loginDataChanged(String username, String password) {
        if (!isValidUsername(username)) {
            _loginFormState.setValue(new LoginFormState(R.string.invalid_username, null, false));
        } else if (!isValidPassword(password)) {
            _loginFormState.setValue(new LoginFormState(null, R.string.invalid_password, false));
        } else {
            _loginFormState.setValue(new LoginFormState(null, null, true));
        }
    }

    public void login(String username, String password) {
        if (isValidUsername(username) && isValidPassword(password)) {

            usersRepository.login(username, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        // on success
                        loggedInUser ->
                        _loginResponse.setValue(new LoginResponse(loggedInUser)),
                        // on error
                        error -> {
                            Log.d("LoginViewModel", error.getMessage());
                            _loginResponse.setValue(new LoginResponse(R.string.login_failed));
                        }
                    );

        }
    }

    private Boolean isValidUsername(String username) {
        return !username.equals("");
    }

    private Boolean isValidPassword(String password) {
        return !password.equals("");
    }
}
