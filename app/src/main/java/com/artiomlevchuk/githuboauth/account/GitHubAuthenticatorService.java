package com.artiomlevchuk.githuboauth.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GitHubAuthenticatorService extends Service {

    private GitHubAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("GitOAuth", "GitHubAuthenticatorService onCreate()");
        mAuthenticator = new GitHubAuthenticator(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("GitOAuth", "GitHubAuthenticatorService onBind()");
        return mAuthenticator.getIBinder();
    }

}
