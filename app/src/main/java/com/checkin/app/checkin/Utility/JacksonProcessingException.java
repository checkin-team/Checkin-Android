package com.checkin.app.checkin.Utility;


import com.fasterxml.jackson.core.JsonProcessingException;

public class JacksonProcessingException extends JsonProcessingException {

    public JacksonProcessingException(String message) {
        super(message);
    }

    public JacksonProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public JacksonProcessingException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "An unexpected error has occurred.";
    }
}