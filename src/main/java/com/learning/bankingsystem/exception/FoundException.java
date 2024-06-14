package com.learning.bankingsystem.exception;

import lombok.Getter;

@Getter
public class FoundException extends RuntimeException {

    private final String errorCode;
    private final String dynamicValue;

    public FoundException(String errorCode, String dynamicValue) {
        super(String.format("%s : %s", errorCode, dynamicValue));

        this.errorCode = errorCode;
        this.dynamicValue = dynamicValue;
    }
}