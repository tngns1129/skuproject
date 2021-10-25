package com.familyset.myapplication.model.login;

public class LoginFormState {
    private Integer usernameError = null;
    private Integer passwordError = null;
    private Boolean isValid = false;

    public LoginFormState(Integer usernameError, Integer passwordError, Boolean isValid) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.isValid = isValid;
    }

    public Boolean getValid() {
        return isValid;
    }
}
