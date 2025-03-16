/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.phonepe.platform.bonsai.core.exception;

import lombok.Getter;

/**
 * An error class to capture error code and root cause exceptions
 * This class is used to mask all internal errors
 */
@Getter
public class BonsaiError extends RuntimeException {
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
