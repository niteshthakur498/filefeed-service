package com.nitesh.filefeed.exception;

import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle FileNotFoundException and send appropriate response.
     */
    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleFileNotFoundException(FileNotFoundException e) {
        // Log the exception message (you can add more logging if needed)
        return new ResponseEntity<>("File not found: " + e.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handle general runtime exceptions and return a response with INTERNAL_SERVER_ERROR status.
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        // Log the exception message (you can add more logging if needed)
        return new ResponseEntity<>("An error occurred while processing the request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle File size limit exceeded exception
     */
    @ExceptionHandler(UnsupportedFileFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleUnSupportedFileFormatException(UnsupportedFileFormatException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle File size limit exceeded exception
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ResponseEntity<String> handleFileSizeLimitExceededException(MaxUploadSizeExceededException e) {
        return new ResponseEntity<>("File size exceeds the maximum limit", HttpStatus.PAYLOAD_TOO_LARGE);
    }

    /**
     * Handle generic exceptions like ResponseStatusException and return the appropriate status code.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException e) {
        return new ResponseEntity<>(e.getReason(), e.getStatusCode());
    }

    /**
     * Handle any other exceptions that are not explicitly handled above.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        // Log the exception (for debugging)
        return new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

