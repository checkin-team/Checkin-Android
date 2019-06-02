package com.checkin.app.checkin.Utility;

import java.io.IOException;

public class NetworkIssueException extends IOException {
    public NetworkIssueException() {
    }

    public NetworkIssueException(String message) {
        super(message);
    }

    public NetworkIssueException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkIssueException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "Something went wrong with internet. Please try again later.";
    }
}
