package com.checkin.app.checkin.Utility;

import java.io.IOException;

public class RequestCanceledException extends IOException {
    public RequestCanceledException() {
    }

    public RequestCanceledException(String message) {
        super(message);
    }

    public RequestCanceledException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestCanceledException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "Request canceled before execution.";
    }
}
