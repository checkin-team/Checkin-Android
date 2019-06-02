package com.checkin.app.checkin.Data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.checkin.app.checkin.Utility.NetworkIssueException;
import com.checkin.app.checkin.Utility.NoConnectivityException;
import com.checkin.app.checkin.Utility.RequestCanceledException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import retrofit2.Response;

import static java.net.HttpURLConnection.HTTP_CLIENT_TIMEOUT;

public class ApiResponse<T> {
    private final int mStatusCode;
    @Nullable
    private final T data;
    @Nullable
    private final String errorMessage;
    @Nullable
    private final JsonNode errorData;
    @Nullable
    private final Throwable errorThrowable;
    private Response<T> mResponse;

    public ApiResponse(@NonNull Throwable error) {
        mResponse = null;
        mStatusCode = HTTP_CLIENT_TIMEOUT;
        if (error instanceof NoConnectivityException) {
            errorThrowable = error;
        } else if (error instanceof IOException) {
            if ("Canceled".equals(error.getMessage()))
                errorThrowable = new RequestCanceledException(error);
            else
                errorThrowable = new NetworkIssueException(error);
        } else {
            errorThrowable = error;
        }
        data = null;
        errorMessage = errorThrowable.getMessage();
        errorData = null;
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
                if (str != null) {
                    errorMessage = str;
                    errorData = Converters.getJsonNode(errorMessage);
                } else {
                    errorData = null;
                    errorMessage = null;
                }
            }
        } else {
            errorMessage = null;
            errorData = null;
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
        if (errorData == null)
            return errorMessage;
        Log.e("APIResponse", "Error data: " + errorData.toString());
        if (errorData.isObject()) {
            if (errorData.has("detail")) {
                Log.d("APIResponse", "Detail");
                return errorData.get("detail").asText();
            } else if (errorData.has("errors")) {
                Log.d("APIResponse", "Errors");
                return errorData.get("errors").get(0).asText();
            } else if (errorData.has("title")) {
                return errorData.get("title").asText();
            }
        } else if (errorData.isArray()) {
            Log.d("APIResponse", "Array");
            return errorData.get(0).asText();
        }
        return errorMessage;
    }

    @Nullable
    public JsonNode getErrorData() {
        return errorData;
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
