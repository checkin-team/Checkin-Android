package com.checkin.app.checkin.Data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitLiveData<T> extends LiveData<ApiResponse<T>> implements Callback<T> {
    private final Call<T> mCall;
    private boolean mResponseDispatched = false;

    public RetrofitLiveData(Call<T> call) {
        mCall = call;
    }

    @Override
    protected void onActive() {
        super.onActive();
        if (!mResponseDispatched) {
            if (!mCall.isExecuted() && !mCall.isCanceled())
                mCall.enqueue(this);
            else
                mCall.clone().enqueue(this);
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (shouldCancel()) {
            cancel();
        }
    }

    protected void cancel() {
        if (!mCall.isCanceled())
            Log.d("Http Calls", "Call cancelled!");
        mCall.cancel();
    }

    protected boolean shouldCancel() {
        return !hasObservers();
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        postValue(new ApiResponse<>(response));
        mResponseDispatched = true;
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        postValue(new ApiResponse<>(t));
    }
}
