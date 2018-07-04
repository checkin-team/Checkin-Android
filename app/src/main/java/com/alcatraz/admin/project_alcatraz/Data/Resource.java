package com.alcatraz.admin.project_alcatraz.Data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

// A generic class that describes data with a status.
public class Resource<T> {
    @NonNull public final Status status;
    @Nullable public final T data;
    @Nullable public final String message;

    public enum Status {
        SUCCESS,
        LOADING,
        ERROR_DISCONNECTED,
        ERROR_BAD_URL,
        ERROR_NOT_A_FEED,
        ERROR_UNKNOWN,
    }

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    @NonNull
    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    @NonNull
    public static <T> Resource<T> errorUnknown(String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR_UNKNOWN, data, msg);
    }

    @NonNull
    public static <T> Resource<T> errorDisconnected(@Nullable T data) {
        return new Resource<>(Status.ERROR_DISCONNECTED, data, null);
    }

    @NonNull
    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

}
