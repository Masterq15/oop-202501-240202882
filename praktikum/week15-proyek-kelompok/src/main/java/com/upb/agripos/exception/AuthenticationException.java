package com.upb.agripos.exception;

/**
 * AuthenticationException
 * Exception untuk autentikasi gagal
 */
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
