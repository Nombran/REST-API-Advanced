package com.epam.esm.tag;

public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException() {
    }

    public TagNotFoundException(String s) {
        super(s);
    }

    public TagNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public TagNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public TagNotFoundException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
