package com.checkin.app.checkin.Data;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitLiveData<T> extends LiveData<ApiResponse<T>> {
    private final Call<T> mCall;

    private final Callback<T> mCallback = new Callback<T>() {
        @Override
        public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
            postValue(new ApiResponse<>(response));
        }

        @Override
        public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
            postValue(new ApiResponse<>(t));
        }
    };

    public RetrofitLiveData(Call<T> call) {
        mCall = call;
    }

    @Override
    protected void onActive() {
        super.onActive();
        if (!mCall.isCanceled() && !mCall.isExecuted()) {
            Log.e("Http calls", "Call enqueued");
            mCall.enqueue(mCallback);
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (!hasObservers()) {
            cancel();
        }
    }

    public void cancel() {
        if (!mCall.isCanceled())
            Log.e("Http Calls", "Call cancelled!");
            mCall.cancel();
    }
}
