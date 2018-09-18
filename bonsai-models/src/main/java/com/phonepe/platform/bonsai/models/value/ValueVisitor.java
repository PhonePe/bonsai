package com.phonepe.platform.bonsai.models.value;

/**
 * @author tushar.naik
 * @version 1.0  05/09/18 - 2:00 PM
 */
public interface ValueVisitor<T> {
    T visit(DataValue dataValue);

    T visit(ReferenceValue referenceValue);
}
