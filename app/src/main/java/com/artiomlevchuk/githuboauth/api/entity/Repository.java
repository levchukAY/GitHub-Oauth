package com.artiomlevchuk.githuboauth.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Repository {

    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

}