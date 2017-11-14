package com.artiomlevchuk.githuboauth.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthorizedUser {

    @SerializedName("login")
    @Expose
    private String login;

    public String getLogin() {
        return login;
    }

}