package com.nitesh.filefeed.exception;

public class FileNotReceivedException extends RuntimeException {

    // Constructor to accept the error message
    public FileNotReceivedException(String message) {
        super(message);
    }

    // Constructor to accept the error message and the cause
    public FileNotReceivedException(String message, Throwable cause) {
        super(message, cause);
    }
}
