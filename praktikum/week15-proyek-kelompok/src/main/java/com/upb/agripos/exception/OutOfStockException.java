package com.upb.agripos.exception;

/**
 * OutOfStockException
 * Exception ketika stok produk tidak mencukupi
 */
public class OutOfStockException extends Exception {
    public OutOfStockException(String message) {
        super(message);
    }

    public OutOfStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
