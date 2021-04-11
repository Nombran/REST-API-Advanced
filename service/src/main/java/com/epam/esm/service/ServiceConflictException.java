package com.epam.esm.service;

public class ServiceConflictException extends RuntimeException {
    public ServiceConflictException(String s) {
        super(s);
    }
}
