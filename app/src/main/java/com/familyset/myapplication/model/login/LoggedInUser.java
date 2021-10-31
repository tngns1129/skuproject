package com.familyset.myapplication.model.login;

public class LoggedInUser {
    private final String id;
    private String displayName;

    public LoggedInUser(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getUserId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
