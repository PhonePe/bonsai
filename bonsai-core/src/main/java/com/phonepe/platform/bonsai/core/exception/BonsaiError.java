package com.phonepe.platform.bonsai.core.exception;

import lombok.Getter;

/**
 * The grand Bonsai framework error
 *
 * @author tushar.naik
 * @version 1.0  27/07/18 - 11:03 AM
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
        if (e instanceof BonsaiError) {
            return (BonsaiError) e;
        }
        return new BonsaiError(BonsaiErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), e);
    }

    public static BonsaiError propagate(String message, Throwable e) {
        if (e instanceof BonsaiError) {
            return (BonsaiError) e;
        }
        return new BonsaiError(BonsaiErrorCode.INTERNAL_SERVER_ERROR, message + " Error:" + e.getMessage(), e);
    }

    public static BonsaiError propagate(BonsaiErrorCode errorCode, String message, Throwable e) {
        if (e instanceof BonsaiError) {
            return (BonsaiError) e;
        }
        return new BonsaiError(errorCode, message + " Error:" + e.getMessage(), e);
    }
}
