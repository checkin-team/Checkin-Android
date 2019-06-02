package com.checkin.app.checkin.Data;

import android.accounts.AccountManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.checkin.app.checkin.Auth.AuthPreferences;
import com.checkin.app.checkin.BuildConfig;
import com.checkin.app.checkin.Utility.Constants;
import com.checkin.app.checkin.Utility.NoConnectivityException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static com.checkin.app.checkin.Utility.Constants.API_HOST;
import static com.checkin.app.checkin.Utility.Constants.API_PROTOCOL;
import static com.checkin.app.checkin.Utility.Utils.isNetworkConnected;
import static com.facebook.FacebookSdk.getCacheDir;

@Module
public class ApiClient {
    private static WebApiService sApiService;

    private static String getBaseUrl() {
        return API_PROTOCOL + API_HOST + "/";
    }

    private static String getUserAgentHeader() {
        return BuildConfig.APPLICATION_ID + "/" + BuildConfig.VERSION_NAME +
                " (Linux; Android API " + Build.VERSION.SDK_INT + ")";
    }

    private static String getAcceptHeader() {
        return "application/json; version=" + Constants.API_VERSION;
    }

    @Provides
    public static WebApiService getApiService(Context context) {
        if (sApiService == null) {
            synchronized (ApiClient.class) {
                if (sApiService == null) {
                    sApiService = new ApiClient().provideRetrofit(context).create(WebApiService.class);
                }
            }
        }
        return sApiService;
    }

    private OkHttpClient provideClient(final Context context) {
        int cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache = new Cache(getCacheDir(), cacheSize);

        final OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        handleNetworkInterceptor(httpClientBuilder, context);
        handleHeadersInterceptor(httpClientBuilder, context);

        httpClientBuilder
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .cache(cache);
        if (BuildConfig.DEBUG) {
            // Logging interceptor
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Log.e("HTTP logging: ", message));
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(loggingInterceptor);
        }
        return httpClientBuilder.build();
    }

    private void handleHeadersInterceptor(OkHttpClient.Builder clientBuilder, Context context) {
        clientBuilder.addInterceptor(chain -> {
            Request.Builder requestBuilder = chain.request().newBuilder()
                    .addHeader("Accept", getAcceptHeader())
                    .addHeader("User-Agent", getUserAgentHeader());
            String authToken = AuthPreferences.getAuthToken(context);
            if (authToken != null)
                requestBuilder.addHeader("Authorization", "Token " + authToken);
            Request request = requestBuilder.build();
            Response response = chain.proceed(request);
            if (authToken != null && response.code() == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                AccountManager.get(context).invalidateAuthToken(Constants.ACCOUNT_TYPE, authToken);
            }
            return response;
        });
    }

    private void handleNetworkInterceptor(OkHttpClient.Builder clientBuilder, Context context) {
        clientBuilder.addInterceptor(new NetworkConnectionInterceptor() {
            @Override
            public boolean isInternetAvailable() {
                return isNetworkConnected(context);
            }

            @Override
            public void onInternetUnavailable() throws NoConnectivityException {
                throw new NoConnectivityException();
            }
        });
    }

    private Retrofit provideRetrofit(@NonNull final Context context) {
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create(Converters.objectMapper))
                .client(provideClient(context))
                .build();
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
}
