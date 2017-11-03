package com.artiomlevchuk.githuboauth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.artiomlevchuk.githuboauth.api.ApiConstants;
import com.artiomlevchuk.githuboauth.api.GitHubAuthService;
import com.artiomlevchuk.githuboauth.api.GitHubService;
import com.artiomlevchuk.githuboauth.api.entity.AccessToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = new Uri.Builder()
                        .scheme(ApiConstants.HTTPS)
                        .authority(ApiConstants.GIT_HUB_URL)
                        .appendPath("login")
                        .appendPath("oauth")
                        .appendPath("authorize")
                        .appendQueryParameter("response_type", "code")
                        .appendQueryParameter("client_id", ApiConstants.CLIENT_ID)
                        .appendQueryParameter("redirect_uri", ApiConstants.REDIRECT_URI)
                        .appendQueryParameter("scope", "gist,user,repo")
                        .build();
                Log.d("OAUTH", "request " + uri.toString());
                Intent oauthIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(oauthIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(ApiConstants.REDIRECT_URI)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                Log.d("OAUTH", "code = " + code);
                getAccessToken(code);
            } else if (uri.getQueryParameter("error") != null) {
                Log.d("OAUTH", uri.getQueryParameter("error"));
            }
        }
    }

    private void getAccessToken(String code) {
        GitHubAuthService.Builder.build().getAccessToken(
                "authorization_code",
                code,
                ApiConstants.REDIRECT_URI,
                ApiConstants.CLIENT_ID,
                ApiConstants.CLIENT_SECRET
        ).enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    String accessToken = response.body().getAccessToken();
                    if (accessToken != null) {
                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this)
                                .edit().putString("PREF_TOKEN", accessToken).apply();
                        Log.d("OAUTH", "token = " + getPreferences(Context.MODE_PRIVATE).getString("PREF_TOKEN", null));
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Log.d("OAUTH", response.body().getError());
                        Log.d("OAUTH", response.body().getErrorDescription());
                        Log.d("OAUTH", response.body().getErrorUri());
                    }
                } else {
                    Log.d("OAUTH", response.message());
                    Log.d("OAUTH", response.code() + "");
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.d("OAUTH", "Failure");
            }
        });
    }
}
