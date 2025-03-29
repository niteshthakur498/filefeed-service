package com.nitesh.filefeed.exception;

public class UnsupportedFileFormatException extends RuntimeException {

    // Constructor to accept the error message
    public UnsupportedFileFormatException(String message) {
        super(message);
    }

    // Constructor to accept the error message and the cause
    public UnsupportedFileFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
