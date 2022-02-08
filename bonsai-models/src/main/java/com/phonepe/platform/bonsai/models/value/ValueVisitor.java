package com.phonepe.platform.bonsai.models.value;

public interface ValueVisitor<T> {
    T visit(NumberValue numberValue);

    T visit(StringValue stringValue);

    T visit(BooleanValue booleanValue);

    T visit(ByteValue byteValue);

    T visit(JsonValue jsonValue);
}
