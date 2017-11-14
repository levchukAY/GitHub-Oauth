package com.artiomlevchuk.githuboauth.api;

import android.net.Uri;

import com.artiomlevchuk.githuboauth.api.entity.AccessToken;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GitHubAuthService {

    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("/login/oauth/access_token")
    Call<AccessToken> getAccessToken(
            @Field("grant_type") String grantType,
            @Field("code") String code,
            @Field("redirect_uri") String redirectUri,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret);

    class Builder {

        public static GitHubAuthService build() {
            return new Retrofit.Builder()
                    .baseUrl(new Uri.Builder()
                            .scheme("https")
                            .authority("github.com")
                            .build().toString())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(GitHubAuthService.class);
        }
    }

}
