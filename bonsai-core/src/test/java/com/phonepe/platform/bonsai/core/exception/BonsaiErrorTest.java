package com.phonepe.platform.bonsai.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BonsaiErrorTest {

    @Test
    void given_bonsaiError_when_creatingBonsaiErrorObject_then_returnBonsaiErrorObject() {
        final BonsaiError bonsaiErrorWithOnlyErrorCode = new BonsaiError(BonsaiErrorCode.INVALID_INPUT);
        final BonsaiError bonsaiErrorWithCodeAndMessage =
                new BonsaiError(BonsaiErrorCode.INVALID_INPUT, "Invalid Input");
        final BonsaiError bonsaiErrorWithCodeMessageAndThrowable =
                new BonsaiError(BonsaiErrorCode.TREE_ALREADY_EXIST, "Tree Exist", new IllegalArgumentException());
        final BonsaiError bonsaiErrorWithCodeAndThrowable =
                new BonsaiError(BonsaiErrorCode.TREE_DOES_NOT_EXIST, new IllegalArgumentException());

        assertEquals(BonsaiErrorCode.INVALID_INPUT, bonsaiErrorWithOnlyErrorCode.getErrorCode());
        assertEquals(BonsaiErrorCode.INVALID_INPUT, bonsaiErrorWithCodeAndMessage.getErrorCode());
        assertEquals("Invalid Input", bonsaiErrorWithCodeAndMessage.getMessage());
        assertEquals(BonsaiErrorCode.TREE_ALREADY_EXIST, bonsaiErrorWithCodeMessageAndThrowable.getErrorCode());
        assertEquals("Tree Exist", bonsaiErrorWithCodeMessageAndThrowable.getMessage());
        assertEquals(BonsaiErrorCode.TREE_DOES_NOT_EXIST, bonsaiErrorWithCodeAndThrowable.getErrorCode());
    }

    @Test
    void given_bonsaiError_when_creatingBonsaiErrorObjectUsingStaticFunction_then_returnBonsaiErrorObject() {
        final BonsaiError bonsaiErrorWithThrowable = BonsaiError.propagate(new IllegalArgumentException());
        final BonsaiError bonsaiErrorWithMessageAndThrowable =
                BonsaiError.propagate("Invalid Message", new IllegalArgumentException());
        final BonsaiError bonsaiErrorWithCodeMessageAndThrowable =
                BonsaiError.propagate(BonsaiErrorCode.INVALID_INPUT, "Invalid Message", new IllegalArgumentException());

        assertEquals(BonsaiErrorCode.INTERNAL_SERVER_ERROR, bonsaiErrorWithThrowable.getErrorCode());
        assertEquals(BonsaiErrorCode.INTERNAL_SERVER_ERROR, bonsaiErrorWithMessageAndThrowable.getErrorCode());
        assertEquals(BonsaiErrorCode.INVALID_INPUT, bonsaiErrorWithCodeMessageAndThrowable.getErrorCode());

        final BonsaiError bonsaiErrorWithBonsaiErrorObject =
                BonsaiError.propagate(new BonsaiError(BonsaiErrorCode.TREE_DOES_NOT_EXIST));
        final BonsaiError bonsaiErrorWithMessageAndBonsaiErrorObject =
                BonsaiError.propagate("Invalid Message", new BonsaiError(BonsaiErrorCode.TREE_ALREADY_EXIST));
        final BonsaiError bonsaiErrorWithCodeMessageAndBonsaiErrorObject =
                BonsaiError.propagate(BonsaiErrorCode.KNOT_ABSENT, "Invalid Message",
                        new BonsaiError(BonsaiErrorCode.INVALID_INPUT));

        assertEquals(BonsaiErrorCode.TREE_DOES_NOT_EXIST, bonsaiErrorWithBonsaiErrorObject.getErrorCode());
        assertEquals(BonsaiErrorCode.TREE_ALREADY_EXIST, bonsaiErrorWithMessageAndBonsaiErrorObject.getErrorCode());
        assertEquals(BonsaiErrorCode.INVALID_INPUT, bonsaiErrorWithCodeMessageAndBonsaiErrorObject.getErrorCode());
    }
}