package com.artiomlevchuk.githuboauth;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.artiomlevchuk.githuboauth.api.GitHubService;
import com.artiomlevchuk.githuboauth.api.entity.AuthorizedUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //PreferenceManager.getDefaultSharedPreferences(this).edit().putString("PREF_TOKEN", null).apply();
        mToken = PreferenceManager.getDefaultSharedPreferences(this).getString("PREF_TOKEN", null);
        if (mToken == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            startSession();
        }
    }

    private void startSession() {
        GitHubService.Builder.build().getAuthorizedUser(mToken).enqueue(new Callback<AuthorizedUser>() {
            @Override
            public void onResponse(Call<AuthorizedUser> call, Response<AuthorizedUser> response) {
                if (response.isSuccessful()) {
                    AuthorizedUser user = response.body();
                    fillUserInfo(user);
                    Log.d("OAUTH", user.getName());
                } else {
                    Log.d("OAUTH", "not success");
                }
            }

            @Override
            public void onFailure(Call<AuthorizedUser> call, Throwable t) {
                Log.d("OAUTH", "Failure");
            }
        });
    }

    private void fillUserInfo(AuthorizedUser user) {
    }

}
