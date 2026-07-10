package com.sah.security;

public class IdentificatorNotFoundException extends RuntimeException {
    public IdentificatorNotFoundException(String message) {
        super(message);
    }
    public IdentificatorNotFoundException(String msg, Throwable cause) { super(msg, cause);}
}
