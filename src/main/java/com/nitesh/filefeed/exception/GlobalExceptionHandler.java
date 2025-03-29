package com.nitesh.filefeed.exception;

import com.nitesh.filefeed.dto.ErrorDetail;
import com.nitesh.filefeed.dto.ResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle FileNotReceivedException and send appropriate response.
     */
    @ExceptionHandler(FileNotReceivedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseWrapper<String>> handleFileNotReceivedException(FileNotReceivedException e) {
        // Create error details
        List<ErrorDetail> errorDetails = List.of(new ErrorDetail("FILE_NOT_RECEIVED", "File not received: " + e.getMessage()));
        // Wrap response in ResponseWrapper
        ResponseWrapper<String> responseWrapper = new ResponseWrapper<>(
                HttpStatus.BAD_REQUEST.value(),
                "File not received",
                null,
                errorDetails
        );
        return new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle FileNotFoundException and send appropriate response.
     */
    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ResponseWrapper<String>> handleFileNotFoundException(FileNotFoundException e) {
        // Create error details
        List<ErrorDetail> errorDetails = List.of(new ErrorDetail("FILE_NOT_FOUND", "File not found: " + e.getMessage()));
        // Wrap response in ResponseWrapper
        ResponseWrapper<String> responseWrapper = new ResponseWrapper<>(
                HttpStatus.NOT_FOUND.value(),
                "File not found",
                null,
                errorDetails
        );
        return new ResponseEntity<>(responseWrapper, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle general runtime exceptions and return a response with INTERNAL_SERVER_ERROR status.
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ResponseWrapper<String>> handleRuntimeException(RuntimeException e) {
        // Create error details
        List<ErrorDetail> errorDetails = List.of(new ErrorDetail("RUNTIME_ERROR", "An error occurred: " + e.getMessage()));
        // Wrap response in ResponseWrapper
        ResponseWrapper<String> responseWrapper = new ResponseWrapper<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                null,
                errorDetails
        );
        return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle UnsupportedFileFormatException and send appropriate response.
     */
    @ExceptionHandler(UnsupportedFileFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseWrapper<String>> handleUnSupportedFileFormatException(UnsupportedFileFormatException e) {
        // Create error details
        List<ErrorDetail> errorDetails = List.of(new ErrorDetail("UNSUPPORTED_FILE_FORMAT", e.getMessage()));
        // Wrap response in ResponseWrapper
        ResponseWrapper<String> responseWrapper = new ResponseWrapper<>(
                HttpStatus.BAD_REQUEST.value(),
                "Unsupported file format",
                null,
                errorDetails
        );
        return new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle MaxUploadSizeExceededException and send appropriate response.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ResponseEntity<ResponseWrapper<String>> handleFileSizeLimitExceededException(MaxUploadSizeExceededException e) {
        // Create error details
        List<ErrorDetail> errorDetails = List.of(new ErrorDetail("FILE_SIZE_EXCEEDED", "File size exceeds the maximum allowed limit"));
        // Wrap response in ResponseWrapper
        ResponseWrapper<String> responseWrapper = new ResponseWrapper<>(
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                "File size exceeds the maximum limit",
                null,
                errorDetails
        );
        return new ResponseEntity<>(responseWrapper, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    /**
     * Handle generic ResponseStatusException and return the appropriate status code.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ResponseWrapper<String>> handleResponseStatusException(ResponseStatusException e) {
        // Create error details
        List<ErrorDetail> errorDetails = List.of(new ErrorDetail("RESPONSE_STATUS_EXCEPTION", e.getReason()));
        // Wrap response in ResponseWrapper
        ResponseWrapper<String> responseWrapper = new ResponseWrapper<>(
                e.getStatusCode().value(),
                e.getReason(),
                null,
                errorDetails
        );
        return new ResponseEntity<>(responseWrapper, e.getStatusCode());
    }

    /**
     * Handle any other exceptions that are not explicitly handled above.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<String>> handleException(Exception e) {
        // Create error details
        List<ErrorDetail> errorDetails = List.of(new ErrorDetail("UNKNOWN_ERROR", "An unexpected error occurred: " + e.getMessage()));
        // Wrap response in ResponseWrapper
        ResponseWrapper<String> responseWrapper = new ResponseWrapper<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                null,
                errorDetails
        );
        return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

