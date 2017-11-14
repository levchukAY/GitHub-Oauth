package com.artiomlevchuk.githuboauth.api;

import android.net.Uri;

import com.artiomlevchuk.githuboauth.api.entity.AuthorizedUser;
import com.artiomlevchuk.githuboauth.api.entity.Repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GitHubService {

    @GET("user")
    Call<AuthorizedUser> getAuthorizedUser(@Query("access_token") String accessToken);

    @GET("user/repos")
    Call<List<Repository>> getOwnedRepositories(@Query("access_token") String accessToken);

    class Builder {

        public static GitHubService build() {
            return new Retrofit.Builder()
                    .baseUrl(new Uri.Builder()
                            .scheme("http")
                            .authority("api.github.com")
                            .build().toString())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(GitHubService.class);
        }
    }

}
