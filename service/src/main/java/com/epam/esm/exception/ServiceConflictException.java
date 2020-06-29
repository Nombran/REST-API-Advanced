package com.epam.esm.exception;

public class ServiceConflictException extends RuntimeException {
    public ServiceConflictException() {
    }

    public ServiceConflictException(String s) {
        super(s);
    }

    public ServiceConflictException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ServiceConflictException(Throwable throwable) {
        super(throwable);
    }

    public ServiceConflictException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
