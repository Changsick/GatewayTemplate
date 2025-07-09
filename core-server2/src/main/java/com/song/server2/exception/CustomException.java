package com.song.server2.exception;

public class CustomException extends RuntimeException {
    private final String location;
    private final String errorCode;
    private final String errorMessage;

    public CustomException(String location, String errorCode, String errorMessage) {
        super(errorMessage);
        this.location = location;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getLocation() {
        return location;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }
}
