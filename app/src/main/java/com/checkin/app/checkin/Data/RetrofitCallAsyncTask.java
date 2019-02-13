package com.checkin.app.checkin.Data;

import android.os.AsyncTask;

import com.checkin.app.checkin.Utility.ProgressRequestBody;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class RetrofitCallAsyncTask<T> extends AsyncTask<Call<T>, Void, Void> {
    private ProgressRequestBody.UploadCallbacks mListener;

    public RetrofitCallAsyncTask(ProgressRequestBody.UploadCallbacks listener) {
        mListener = listener;
    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(Call<T>... calls) {
        for (Call call: calls) {
            try {
                Response response = call.execute();
                if (mListener != null) {
                    if (response.isSuccessful())
                        mListener.onSuccess();
                    else
                        mListener.onFailure();
                }
            } catch (IOException e) {
                e.printStackTrace();
                mListener.onFailure();
            }
        }
        return null;
    }
}
