package com.epam.esm.order;

public class OrderConflictException extends RuntimeException {
    public OrderConflictException(String s) {
        super(s);
    }
}
