package com.artiomlevchuk.githuboauth.ui.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.artiomlevchuk.githuboauth.R;
import com.artiomlevchuk.githuboauth.account.GitHubAccount;
import com.artiomlevchuk.githuboauth.api.GitHubService;
import com.artiomlevchuk.githuboauth.api.entity.Repository;
import com.artiomlevchuk.githuboauth.ui.adapter.ReposAdapter;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int CHOOSE_ACCOUNT_CODE = 1002;

    String mToken;

    private ReposAdapter reposAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ButterKnife.setDebug(true);

        initRecyclerView();

        Log.d("GitOAuth", "MainActivity onCreate");

        selectAccount();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("GitOAuth", "MainActivity onActivityResult()");
        if (requestCode == CHOOSE_ACCOUNT_CODE && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Account selectedAccount = getAccountByName(accountName);
            if (selectedAccount != null) {
                getToken(selectedAccount);
            }

            Log.d("GitOAuth", "name = " + accountName);
        }
    }

    private void initRecyclerView() {
        RecyclerView reposRecyclerView = (RecyclerView) findViewById(R.id.repos_recycler_view);
        reposRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reposAdapter = new ReposAdapter();
        reposRecyclerView.setAdapter(reposAdapter);
    }

    private void selectAccount() {
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Log.d("GitOAuth", "MainActivity newChooseAccountIntent");
            intent = AccountManager.newChooseAccountIntent(null, null,
                    new String[]{ GitHubAccount.TYPE }, null, null, null, null);
        } else {
            Log.d("GitOAuth", "MainActivity newChooseAccountIntent old");
            intent = AccountManager.newChooseAccountIntent(null, null,
                    new String[]{ GitHubAccount.TYPE }, false, null, null, null, null);
        }
        startActivityForResult(intent, CHOOSE_ACCOUNT_CODE);
    }

    private void getToken(Account account) {
        Log.d("GitOAuth", "MainActivity getToken");
        final AccountManager am = AccountManager.get(MainActivity.this);
        am.getAuthToken(account, account.type, new Bundle(), MainActivity.this,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            mToken = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
                            getOwnedRepositories(mToken);
                            Log.d("GitOAuth", "token = " + mToken);
                        } catch (OperationCanceledException | IOException
                                | AuthenticatorException e) {
                            e.printStackTrace();
                            Log.d("GitOAuth", "exception");
                        }
                    }
                }, null);

    }

    private Account getAccountByName(String accountName) {
        final AccountManager am = AccountManager.get(MainActivity.this);
        for (Account account : am.getAccountsByType(GitHubAccount.TYPE)) {
            if (account.name.equals(accountName)) {
                return account;
            }
        }
        return null;
    }

    private void getOwnedRepositories(String token) {
        Log.d("GitOAuth", "MainActivity getAuthorizedUser");
        GitHubService.Builder.build().getOwnedRepositories(token).enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                if (response.isSuccessful()) {
                    List<Repository> repos = response.body();
                    Log.d("GitOAuth", repos.size() + " repos");
                    reposAdapter.setReposList(repos);
                } else {
                    Log.d("GitOAuth", "not success");
                }
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                Log.d("GitOAuth", "Failure");
            }
        });
    }

}
