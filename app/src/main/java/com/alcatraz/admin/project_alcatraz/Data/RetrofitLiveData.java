package com.alcatraz.admin.project_alcatraz.Data;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitLiveData<T> extends LiveData<ApiResponse<T>> {
    private final Call<T> mCall;

    private final Callback<T> mCallback = new Callback<T>() {
        @Override
        public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
            try {
                postValue(new ApiResponse<>(response));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        if (!mCall.isCanceled() && !mCall.isExecuted()) {
            mCall.enqueue(mCallback);
        }
    }

    public void cancel() {
        if (!mCall.isCanceled())
            mCall.cancel();
    }
}
