package com.example.kstream.common.exception;

public class ApplicativeException extends RuntimeException  {

    private final ApplicativeError applicativeError;

    public ApplicativeException(ApplicativeError applicativeError) {
        this.applicativeError = applicativeError;
    }

    public ApplicativeError getApplicativeError() {
        return applicativeError;
    }
}