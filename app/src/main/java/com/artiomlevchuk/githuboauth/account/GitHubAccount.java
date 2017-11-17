package com.artiomlevchuk.githuboauth.account;

import android.accounts.Account;

public class GitHubAccount extends Account {

    public static final String TYPE = "com.artiomlevchuk.githuboauth";

    public GitHubAccount(String name) {
        super(name, TYPE);
    }

}