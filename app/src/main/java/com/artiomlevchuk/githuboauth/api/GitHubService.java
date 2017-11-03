package com.artiomlevchuk.githuboauth.api;

import android.net.Uri;

import com.artiomlevchuk.githuboauth.api.entity.AccessToken;
import com.artiomlevchuk.githuboauth.api.entity.AuthorizedUser;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GitHubService {

    @GET("user")
    Call<AuthorizedUser> getAuthorizedUser(
            @Query("access_token") String accessToken
    );

    class Builder {

        public static GitHubService build() {
            return new Retrofit.Builder()
                    .baseUrl(new Uri.Builder()
                            .scheme(ApiConstants.HTTP)
                            .authority(ApiConstants.API_URL)
                            .build().toString())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(GitHubService.class);
        }
    }

}
