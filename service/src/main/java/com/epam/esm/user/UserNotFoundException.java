package com.epam.esm.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
    }

    public UserNotFoundException(String s) {
        super(s);
    }

    public UserNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public UserNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public UserNotFoundException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
