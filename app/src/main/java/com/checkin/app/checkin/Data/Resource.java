package com.checkin.app.checkin.Data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.checkin.app.checkin.Utility.NoConnectivityException;
import com.fasterxml.jackson.databind.JsonNode;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_ACCEPTABLE;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

// A generic class that describes data with a status.
public class Resource<T> {
    private static final String TAG = Resource.class.getSimpleName();
    @NonNull
    public final Status status;
    @Nullable
    public final T data;
    @Nullable
    private final JsonNode errorBody;
    @Nullable
    public final String message;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message, @Nullable JsonNode errorBody) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.errorBody = errorBody;
    }

    @NonNull
    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null, null);
    }

    @NonNull
    public static <T> Resource<T> error(Status status, String msg, @Nullable T data, @Nullable JsonNode errorBody) {
        return new Resource<>(status, data, msg, errorBody);
    }

    @NonNull
    public static <T> Resource<T> error(String msg, @Nullable T data, @Nullable JsonNode errorBody) {
        return error(Status.ERROR_UNKNOWN, msg, data, errorBody);
    }

    @NonNull
    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return error(msg, data, null);
    }

    @NonNull
    public static <T> Resource<T> errorNotFound(String msg, @Nullable JsonNode errorBody) {
        return error(Status.ERROR_NOT_FOUND, msg, null, errorBody);
    }

    @NonNull
    public static <T> Resource<T> errorNotFound(String msg) {
        return error(msg, null);
    }

    @NonNull
    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null, null);
    }

    @NonNull
    public static <T> Resource<T> noRequest() {
        return new Resource<>(Status.NO_REQUEST, null, null, null);
    }

    public static <T> Resource<T> createResource(@NonNull ApiResponse<T> apiResponse) {
        Resource<T> resource;
        if (apiResponse.isSuccessful()) {
            resource = success(apiResponse.getData());
        } else if (apiResponse.getErrorThrowable() != null) {
            Log.e(TAG, apiResponse.getErrorThrowable().getMessage(), apiResponse.getErrorThrowable());
            if (apiResponse.getErrorThrowable() instanceof NoConnectivityException) {
                Log.e(TAG, apiResponse.getErrorMessage());
                resource = error(Status.ERROR_DISCONNECTED, apiResponse.getErrorMessage(), apiResponse.getData(), null);
            } else {
                resource = error(Status.ERROR_UNKNOWN, apiResponse.getErrorMessage(), apiResponse.getData(), apiResponse.getErrorData());
            }
        } else if (apiResponse.hasStatus(HTTP_NOT_FOUND)) {
            resource = error(Status.ERROR_NOT_FOUND, apiResponse.getErrorMessage(), apiResponse.getData(), apiResponse.getErrorData());
        } else if (apiResponse.hasStatus(HTTP_BAD_REQUEST)) {
            resource = error(Status.ERROR_INVALID_REQUEST, apiResponse.getErrorMessage(), apiResponse.getData(), apiResponse.getErrorData());
        } else if (apiResponse.hasStatus(HTTP_UNAUTHORIZED)) {
            resource = error(Status.ERROR_UNAUTHORIZED, apiResponse.getErrorMessage(), apiResponse.getData(), apiResponse.getErrorData());
        } else if (apiResponse.hasStatus(HTTP_FORBIDDEN)) {
            resource = error(Status.ERROR_FORBIDDEN, apiResponse.getErrorMessage(), apiResponse.getData(), apiResponse.getErrorData());
        } else if (apiResponse.hasStatus(HTTP_NOT_ACCEPTABLE)) {
            resource = error(Status.ERROR_NOT_ACCEPTABLE, apiResponse.getErrorMessage(), apiResponse.getData(), apiResponse.getErrorData());
        } else {
            resource = error(Status.ERROR_UNKNOWN, apiResponse.getErrorMessage(), apiResponse.getData(), apiResponse.getErrorData());
        }
        return resource;
    }

    public static <X, T> Resource<X> cloneResource(Resource<T> resource, X data) {
        return new Resource<>(resource.status, data, resource.message, resource.errorBody);
    }

    @Nullable
    public JsonNode getErrorBody() {
        return errorBody;
    }

    public enum Status {
        NO_REQUEST,
        SUCCESS,
        LOADING,
        ERROR_RESPONSE_INVALID,
        ERROR_DISCONNECTED,
        ERROR_NOT_FOUND,
        ERROR_INVALID_REQUEST,
        ERROR_FORBIDDEN,
        ERROR_UNAUTHORIZED,
        ERROR_CANCELLED,
        ERROR_NOT_ACCEPTABLE,
        ERROR_UNKNOWN,
    }
}
