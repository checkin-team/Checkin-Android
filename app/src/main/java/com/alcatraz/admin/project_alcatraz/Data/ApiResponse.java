package com.alcatraz.admin.project_alcatraz.Data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alcatraz.admin.project_alcatraz.Utility.Util;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Map;

import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_CLIENT_TIMEOUT;

public class ApiResponse<T> {
    private final int mStatusCode;
    private Response<T> mResponse;
    @Nullable private final T data;
    @Nullable private final String errorMessage;
    @Nullable private final Throwable errorThrowable;

    ApiResponse(Throwable error) {
        mResponse = null;
        mStatusCode = HTTP_CLIENT_TIMEOUT;
        data = null;
        errorMessage = error.getMessage();
        errorThrowable = error;
    }

    ApiResponse(@NonNull Response<T> response) {
        mResponse = response;
        mStatusCode = response.code();
        errorThrowable = null;
        String str = null;
        if (response.errorBody() != null) {
            try {
                str = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                errorMessage = str;
            }
        } else {
            errorMessage = null;
        }
        data = response.body();
    }

    public boolean isSuccessful() {
        return mResponse != null && mResponse.isSuccessful();
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public String getErrorMessage() {
        JsonObject data = Converters.getJsonObject(errorMessage);
        if (data == null)
            return errorMessage;
        if (data.has("detail"))
            return data.get("detail").getAsString();
        else if (data.has("errors"))
            return data.get("errors").getAsString();
        else
            return errorMessage;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public boolean hasStatus(int statusCode) {
        return mStatusCode == statusCode;
    }

    @Nullable
    public Throwable getErrorThrowable() {
        return errorThrowable;
    }
}
