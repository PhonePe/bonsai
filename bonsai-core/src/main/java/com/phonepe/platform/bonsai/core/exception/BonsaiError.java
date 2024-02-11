package com.phonepe.platform.bonsai.core.exception;

import lombok.Getter;

/**
 * An error class to capture error code and root cause exceptions
 * This class is used to mask all internal errors
 */
public class BonsaiError extends RuntimeException {
    @Getter
    private final BonsaiErrorCode errorCode;

    public BonsaiError(BonsaiErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public BonsaiError(BonsaiErrorCode errorCode, String message, Throwable e) {
        super(message, e);
        this.errorCode = errorCode;
    }

    public BonsaiError(BonsaiErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BonsaiError(BonsaiErrorCode errorCode, Throwable e) {
        super(e);
        this.errorCode = errorCode;
    }

    public static BonsaiError propagate(Throwable e) {
        if (e instanceof BonsaiError error) {
            return error;
        }
        return new BonsaiError(BonsaiErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), e);
    }

    public static BonsaiError propagate(String message, Throwable e) {
        if (e instanceof BonsaiError error) {
            return error;
        }
        return new BonsaiError(BonsaiErrorCode.INTERNAL_SERVER_ERROR, message + " Error:" + e.getMessage(), e);
    }

    public static BonsaiError propagate(BonsaiErrorCode errorCode, String message, Throwable e) {
        if (e instanceof BonsaiError error) {
            return error;
        }
        return new BonsaiError(errorCode, message + " Error:" + e.getMessage(), e);
    }
}
