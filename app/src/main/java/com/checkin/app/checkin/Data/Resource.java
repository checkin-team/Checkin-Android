package com.checkin.app.checkin.Data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.checkin.app.checkin.Utility.NoConnectivityException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

// A generic class that describes data with a status.
public class Resource<T> {
    private static final String TAG = Resource.class.getSimpleName();
    @NonNull public final Status status;
    @Nullable public final T data;
    @Nullable public final String message;

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
        ERROR_UNKNOWN,
    }

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public JsonNode getErrorBody() {
        try {
            return Converters.objectMapper.readTree(message);
        } catch (IOException e) {
            Log.e(TAG, "message not a valid JSON data.");
        }
        return null;
    }

    @NonNull
    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    @NonNull
    public static <T> Resource<T> error(Status status, String msg, @Nullable T data) {
        return new Resource<>(status, data, msg);
    }

    @NonNull
    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return error(Status.ERROR_UNKNOWN, msg, data);
    }

    @NonNull
    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    public static <T> Resource<T> noRequest() {
        return new Resource<>(Status.NO_REQUEST, null, null);
    }

    public static <T> Resource<T> createResource(@NonNull ApiResponse<T> apiResponse) {
        Resource<T> resource;
        if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
            resource = success(apiResponse.getData());
        } else if (apiResponse.getErrorThrowable() != null) {
            if (apiResponse.getErrorThrowable() instanceof NoConnectivityException) {
                Log.e(TAG, apiResponse.getErrorMessage());
                resource = error(Status.ERROR_DISCONNECTED, apiResponse.getErrorMessage(), apiResponse.getData());
            } else {
                resource = error(Status.ERROR_UNKNOWN, apiResponse.getErrorMessage(), apiResponse.getData());
            }
        } else if (apiResponse.hasStatus(HTTP_NOT_FOUND)) {
            resource = error(Status.ERROR_NOT_FOUND, apiResponse.getErrorMessage(), apiResponse.getData());
        } else if (apiResponse.hasStatus(HTTP_BAD_REQUEST)) {
            resource = error(Status.ERROR_INVALID_REQUEST, apiResponse.getErrorMessage(), apiResponse.getData());
        } else if (apiResponse.hasStatus(HTTP_UNAUTHORIZED)) {
            resource = error(Status.ERROR_UNAUTHORIZED, apiResponse.getErrorMessage(), apiResponse.getData());
        } else if (apiResponse.hasStatus(HTTP_FORBIDDEN)) {
            resource = error(Status.ERROR_FORBIDDEN, apiResponse.getErrorMessage(), apiResponse.getData());
        }
        else {
            resource = error(Status.ERROR_UNKNOWN, apiResponse.getErrorMessage(), apiResponse.getData());
        }
        return resource;
    }

    public static <X, T> Resource<X> cloneResource(Resource<T> resource, X data) {
        return new Resource<>(resource.status, data, resource.message);
    }
}
