package com.alcatraz.admin.project_alcatraz.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alcatraz.admin.project_alcatraz.BuildConfig;
import com.alcatraz.admin.project_alcatraz.Utility.Constants;

import java.io.IOException;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApiClient {
    public static final String Base_URL = "https://api.checkout.com/v1/";

    private static Retrofit mRetrofit;
    private static WebApiService mApiService;

    public static Retrofit getClient(@NonNull final Context context) {
        if (mRetrofit == null) {
            synchronized (ApiClient.class) {
                if (mRetrofit == null) {
                    OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
                    httpClientBuilder.addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                            String authToken = "Token " + prefs.getString(Constants.SP_LOGIN_TOKEN, "");
                            Request request = chain.request().newBuilder()
                                    .addHeader("Authorization", authToken).build();
                            return chain.proceed(request);
                        }
                    });
                    if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                            @Override
                            public void log(String message) {
                                Log.e("HTTP logging: ", message);
                            }
                        });
                        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
                        httpClientBuilder.addInterceptor(loggingInterceptor);
                    }
                    mRetrofit = new Retrofit.Builder()
                            .baseUrl(Base_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(httpClientBuilder.build())
                            .build();
                    mApiService = mRetrofit.create(WebApiService.class);
                }
            }
        }
        return mRetrofit;
    }

    @Provides
    public static WebApiService getApiService() {
        return mApiService;
    }
}
