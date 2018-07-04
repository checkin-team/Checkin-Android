package com.alcatraz.admin.project_alcatraz.Data;

import android.support.annotation.Nullable;

import java.io.IOException;

import retrofit2.Response;

public class ApiResponse<T> {
    public final int statusCode;
    @Nullable public final T data;
    @Nullable public final String errorMessage;

    public ApiResponse(Throwable error) {
        statusCode = 500;
        data = null;
        errorMessage = error.getMessage();
    }

    public ApiResponse(Response<T> response) throws IOException {
        statusCode = response.code();
        if (response.errorBody() != null) {
            errorMessage = response.errorBody().string();
        } else {
            errorMessage = null;
        }
        data = response.body();
    }

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }
}
