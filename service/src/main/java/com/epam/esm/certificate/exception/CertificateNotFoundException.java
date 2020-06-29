package com.epam.esm.certificate.exception;

public class CertificateNotFoundException extends RuntimeException {
    public CertificateNotFoundException() {
    }

    public CertificateNotFoundException(String s) {
        super(s);
    }

    public CertificateNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public CertificateNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public CertificateNotFoundException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
