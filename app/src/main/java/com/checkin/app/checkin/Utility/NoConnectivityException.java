package com.checkin.app.checkin.Utility;

import java.io.IOException;

public class NoConnectivityException extends IOException {
    public NoConnectivityException() {
    }

    public NoConnectivityException(String message) {
        super(message);
    }

    public NoConnectivityException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoConnectivityException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "No network available, please check your WiFi or Data connection.";
    }
}
