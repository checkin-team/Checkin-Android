package com.checkin.app.checkin.Data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_CLIENT_TIMEOUT;

public class ApiResponse<T> {
    private final int mStatusCode;
    private Response<T> mResponse;
    @Nullable private final T data;
    @Nullable private final String errorMessage;
    @Nullable private final Throwable errorThrowable;

    public ApiResponse(Throwable error) {
        mResponse = null;
        mStatusCode = HTTP_CLIENT_TIMEOUT;
        data = null;
        errorMessage = error.getMessage();
        errorThrowable = error;
    }

    public ApiResponse(@NonNull Response<T> response) {
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
        if (errorMessage == null)
            return null;
        JsonNode data = Converters.getJsonNode(errorMessage);
        if (data == null)
            return errorMessage;
        Log.e("APIResponse", "Error data: " + data.toString());
        if (data.isObject()) {
            if (data.has("detail")) {
                Log.e("APIResponse", "Detail");
                return data.get("detail").asText();
            } else if (data.has("errors")) {
                Log.e("APIResponse", "Errors");
                return data.get("errors").get(0).asText();
            }
        } else if (data.isArray()) {
            Log.e("APIResponse", "Array");
            return data.get(0).asText();
        }
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
