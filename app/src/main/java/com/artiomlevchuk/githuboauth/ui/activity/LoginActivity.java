package com.artiomlevchuk.githuboauth.ui.activity;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.artiomlevchuk.githuboauth.R;
import com.artiomlevchuk.githuboauth.account.GitHubAccount;
import com.artiomlevchuk.githuboauth.api.GitHubConstants;
import com.artiomlevchuk.githuboauth.api.GitHubAuthService;
import com.artiomlevchuk.githuboauth.api.GitHubService;
import com.artiomlevchuk.githuboauth.api.entity.AccessToken;
import com.artiomlevchuk.githuboauth.api.entity.AuthorizedUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AccountAuthenticatorActivity {

    public static final String EXTRA_TOKEN_TYPE = "com.artiomlevchuk.githuboauth.EXTRA_TOKEN_TYPE";

    public static final int AUTH_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d("GitOAuth", "LoginActivity onCreate() " + this);
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authorizationRequest();
                //startActivityForResult(new Intent(LoginActivity.this, GitHubAuthActivity.class), AUTH_CODE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GitOAuth", "LoginActivity onResume() " + this);

        Uri uri = getIntent().getData();
        Log.d("GitOAuth", "uri = " + uri);
        if (uri != null && uri.toString().startsWith(GitHubConstants.REDIRECT_URI)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                Log.d("GitOAuth", "code = " + code);
                getAccessToken(code);
            } else if (uri.getQueryParameter("error") != null) {
                Log.d("GitOAuth", uri.getQueryParameter("error"));
            }
        } else {
            Log.d("GitOAuth", "null");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("GitOAuth", "LoginActivity onNewIntent() " + this);
        Uri uri = intent.getData();
        Log.d("GitOAuth", "uri = " + uri);
        if (uri != null && uri.toString().startsWith(GitHubConstants.REDIRECT_URI)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                Log.d("GitOAuth", "code = " + code);
                getAccessToken(code);
            } else if (uri.getQueryParameter("error") != null) {
                Toast.makeText(this, uri.getQueryParameter("error"),
                        Toast.LENGTH_SHORT).show();
                Log.d("GitOAuth", uri.getQueryParameter("error"));
            }
        } else {
            Log.d("GitOAuth", "null");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("GitOAuth", "LoginActivity onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTH_CODE && resultCode == RESULT_OK) {
            String code = getIntent().getStringExtra("EXTRA_CODE");
            Log.d("GitOAuth", "LoginActivity code = " + code);
            getAccessToken(code);
        }
    }

    public void authorizationRequest() {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority("github.com")
                .appendPath("login")
                .appendPath("oauth")
                .appendPath("authorize")
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("client_id", GitHubConstants.CLIENT_ID)
                .appendQueryParameter("redirect_uri", GitHubConstants.REDIRECT_URI)
                .appendQueryParameter("scope", "gist,user,repo")
                .build();
        Log.d("GitOAuth", "request " + uri.toString());
        Intent oauthIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(oauthIntent);
    }

    private void onTokenReceived(Account account, String token) {
        Log.d("GitOAuth", "LoginActivity onTokenReceived");
        final AccountManager am = AccountManager.get(this);
        final Bundle result = new Bundle();
        if (am.addAccountExplicitly(account, null, new Bundle())) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, token);
            am.setAuthToken(account, account.type, token);
            Log.d("GitOAuth", "LoginActivity set_account");
        } else {
            Log.d("GitOAuth", "LoginActivity account_already_exists");
            result.putString(AccountManager.KEY_ERROR_MESSAGE,
                    getString(R.string.account_already_exists));
        }
        setAccountAuthenticatorResult(result);
        setResult(RESULT_OK);
        Log.d("GitOAuth", "LoginActivity pre finish");
        finish();
    }

    private void getAccessToken(String code) {
        GitHubAuthService.Builder.build().getAccessToken(
                "authorization_code",
                code,
                GitHubConstants.REDIRECT_URI,
                GitHubConstants.CLIENT_ID,
                GitHubConstants.CLIENT_SECRET
        ).enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    String accessToken = response.body().getAccessToken();
                    if (accessToken != null) {
                        Log.d("GitOAuth", "token = " + accessToken);
                        getAuthorizedUser(accessToken);
                    } else {
                        Toast.makeText(LoginActivity.this, response.body().getErrorDescription(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("GitOAuth", response.body().getError());
                        Log.d("GitOAuth", response.body().getErrorDescription());
                        Log.d("GitOAuth", response.body().getErrorUri());
                    }
                } else {
                    Log.d("GitOAuth", response.message());
                    Log.d("GitOAuth", response.code() + "");
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.d("GitOAuth", "Failure");
            }
        });
    }

    private void getAuthorizedUser(final String token) {
        Log.d("GitOAuth", "MainActivity getAuthorizedUser");
        GitHubService.Builder.build().getAuthorizedUser(token).enqueue(new Callback<AuthorizedUser>() {
            @Override
            public void onResponse(Call<AuthorizedUser> call, Response<AuthorizedUser> response) {
                if (response.isSuccessful()) {
                    AuthorizedUser authorizedUser = response.body();
                    Log.d("GitOAuth", authorizedUser.getLogin());
                    onTokenReceived(new GitHubAccount(authorizedUser.getLogin()), token);
                } else {
                    Log.d("GitOAuth", "not success");
                }
            }

            @Override
            public void onFailure(Call<AuthorizedUser> call, Throwable t) {
                Log.d("GitOAuth", "Failure");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("GitOAuth", "onDestroy() " + this);
    }
}
