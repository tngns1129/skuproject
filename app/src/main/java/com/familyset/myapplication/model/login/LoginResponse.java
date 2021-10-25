package com.familyset.myapplication.model.login;

public class LoginResponse {
    private LoggedInUser success = null;
    private Integer error = null;

    public LoginResponse(LoggedInUser success, Integer error) {
        this.success = success;
        this.error = error;
    }

    public LoginResponse(LoggedInUser success) {
        this.success = success;
    }

    public LoginResponse(Integer error) {
        this.error = error;
    }

    public LoggedInUser getLoggedInUser() {
        return success;
    }

    public Integer getError() {
        return error;
    }
}
