package com.epam.esm.service;

public class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException(String s) {
        super(s);
    }
}
