package com.upb.agripos.exception;

/**
 * ValidationException
 * Exception untuk validasi data yang gagal
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
