package com.checkin.app.checkin.data.network

import android.accounts.AccountManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.checkin.app.checkin.Auth.AuthPreferences
import com.checkin.app.checkin.BuildConfig
import com.checkin.app.checkin.Utility.Constants
import com.checkin.app.checkin.Utility.Utils.isNetworkConnected
import com.checkin.app.checkin.data.Converters
import com.checkin.app.checkin.misc.exceptions.NoConnectivityException
import com.facebook.FacebookSdk.getCacheDir
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection


class ApiClient {
    private fun provideClient(context: Context): OkHttpClient {
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        val cache = Cache(getCacheDir(), cacheSize.toLong())

        val httpClientBuilder = OkHttpClient.Builder()
        handleNetworkInterceptor(httpClientBuilder, context)
        handleHeadersInterceptor(httpClientBuilder, context)

        httpClientBuilder
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .cache(cache)
        if (BuildConfig.DEBUG) {
            // Logging interceptor
            val loggingInterceptor = HttpLoggingInterceptor { message -> Log.e("HTTP logging: ", message) }
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClientBuilder.addInterceptor(loggingInterceptor)
        }
        return httpClientBuilder.build()
    }

    private fun handleHeadersInterceptor(clientBuilder: OkHttpClient.Builder, context: Context) {
        clientBuilder.addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
                    .addHeader("Accept", acceptHeader)
                    .addHeader("User-Agent", userAgentHeader)
            val authToken = AuthPreferences.getAuthToken(context)
            if (authToken != null)
                requestBuilder.addHeader("Authorization", "Token $authToken")
            val request = requestBuilder.build()
            val response = chain.proceed(request)
            if (authToken != null && response.code() == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                AccountManager.get(context).invalidateAuthToken(Constants.ACCOUNT_TYPE, authToken)
            }
            response
        }
    }

    private fun handleNetworkInterceptor(clientBuilder: OkHttpClient.Builder, context: Context) {
        clientBuilder.addInterceptor(object : NetworkConnectionInterceptor() {
            override val isInternetAvailable: Boolean
                get() = isNetworkConnected(context)

            @Throws(NoConnectivityException::class)
            override fun onInternetUnavailable() {
                throw NoConnectivityException()
            }
        })
    }

    private fun provideRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create(Converters.objectMapper))
                .client(provideClient(context))
                .build()
    }

    abstract class NetworkConnectionInterceptor : Interceptor {
        abstract val isInternetAvailable: Boolean

        @Throws(NoConnectivityException::class)
        abstract fun onInternetUnavailable()

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            if (!isInternetAvailable) {
                onInternetUnavailable()
            }
            return chain.proceed(chain.request())
        }
    }

    @Module
    companion object {
        private var sApiService: WebApiService? = null

        private val baseUrl = "${Constants.API_PROTOCOL}${Constants.API_HOST}/"

        private val userAgentHeader: String
            get() = BuildConfig.APPLICATION_ID + "/" + BuildConfig.VERSION_NAME +
                    " (Linux; Android API " + Build.VERSION.SDK_INT + ")"

        private val acceptHeader: String
            get() = "application/json; version=" + Constants.API_VERSION

        @Provides
        fun getApiService(context: Context): WebApiService {
            if (sApiService == null) {
                synchronized(ApiClient::class.java) {
                    if (sApiService == null) {
                        sApiService = ApiClient().provideRetrofit(context).create(WebApiService::class.java)
                    }
                }
            }
            return sApiService!!
        }
    }
}
