package com.checkin.app.checkin.Data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.checkin.app.checkin.Auth.LoginActivity;
import com.checkin.app.checkin.BuildConfig;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.NoConnectivityException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static com.checkin.app.checkin.Utility.Constants.API_HOST;
import static com.checkin.app.checkin.Utility.Constants.API_PORT;

@Module
public class ApiClient {
    private static String getBaseUrl() {
        return "https://" + API_HOST + ":" + API_PORT + "/"; //+ "/v" + API_VERSION + "/";
    }

    private static WebApiService mApiService;

    private OkHttpClient provideClient(final Context context) {
        final OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        final AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        httpClientBuilder.addInterceptor(chain -> {
            Request request = chain.request();
            String authToken = null;
            if (accounts.length > 0) {
                try {
                    Bundle result = accountManager.getAuthToken(accounts[0], AccountManager.KEY_AUTHTOKEN, null, true, null, null).getResult();
                    authToken = result.getString(AccountManager.KEY_AUTHTOKEN);
                    if (authToken != null)
                        request = request.newBuilder()
                                .addHeader("Authorization", "Token " + authToken).build();
                } catch (AuthenticatorException | OperationCanceledException e) {
                    e.printStackTrace();
                }
            }
            Response response = chain.proceed(request);
            if (authToken != null && response.code() == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                accountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE, authToken);
//                context.startActivity(new Intent(context, LoginActivity.class));
            }
            return response;
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
                .addConverterFactory(JacksonConverterFactory.create(Converters.objectMapper))
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
