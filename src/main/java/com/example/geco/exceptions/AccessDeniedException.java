package com.example.geco.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super();
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}