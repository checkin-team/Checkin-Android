package com.alcatraz.admin.project_alcatraz.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alcatraz.admin.project_alcatraz.BuildConfig;
import com.alcatraz.admin.project_alcatraz.Utility.Constants;
import com.alcatraz.admin.project_alcatraz.Utility.NoConnectivityException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.alcatraz.admin.project_alcatraz.Utility.Constants.API_HOST;
import static com.alcatraz.admin.project_alcatraz.Utility.Constants.API_PORT;
import static com.alcatraz.admin.project_alcatraz.Utility.Constants.API_VERSION;

@Module
public class ApiClient {
    public static String getBaseUrl() {
        return "http://" + API_HOST + ":" + API_PORT + "/"; //+ "/v" + API_VERSION + "/";
    }

    private static WebApiService mApiService;

    private OkHttpClient provideClient(final Context context) {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                Request request = chain.request();
                if (prefs.getBoolean(Constants.SP_LOGGED_IN, false)) {
                    String authToken = "Token " + prefs.getString(Constants.SP_LOGIN_TOKEN, "");
                    request.newBuilder()
                            .addHeader("Authorization", authToken).build();
                }
                return chain.proceed(request);
            }
        });
        httpClientBuilder
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);
        httpClientBuilder.addInterceptor(new NetworkConnectionInterceptor() {
            @Override
            public boolean isInternetAvailable() {
                return isNetworkConnected(context);
            }

            @Override
            public void onInternetUnavailable() throws NoConnectivityException {
                throw new NoConnectivityException();
            }
        });
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Log.e("HTTP logging: ", message));
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(loggingInterceptor);
        }
        return httpClientBuilder.build();
    }

    private Retrofit provideRetrofit(@NonNull final Context context) {
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(provideClient(context))
                .build();
    }

    @Provides
    public static WebApiService getApiService(Context context) {
        if (mApiService == null) {
            synchronized (ApiClient.class) {
                if (mApiService == null) {
                    mApiService = new ApiClient().provideRetrofit(context).create(WebApiService.class);
                }
            }
        }
        return mApiService;
    }

    public static abstract class NetworkConnectionInterceptor implements Interceptor {
        public abstract boolean isInternetAvailable();

        public abstract void onInternetUnavailable() throws NoConnectivityException;

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            if (!isInternetAvailable()) {
                onInternetUnavailable();
            }
            return chain.proceed(chain.request());
        }
    }

    public boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
