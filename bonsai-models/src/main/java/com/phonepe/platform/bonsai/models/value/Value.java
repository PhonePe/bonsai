package com.phonepe.platform.bonsai.models.value;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "valueType")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "NUMBER", value = NumberValue.class),
        @JsonSubTypes.Type(name = "STRING", value = StringValue.class),
        @JsonSubTypes.Type(name = "BOOLEAN", value = BooleanValue.class),
        @JsonSubTypes.Type(name = "JSON", value = JsonValue.class),
        @JsonSubTypes.Type(name = "BYTE", value = ByteValue.class)
})
public abstract class Value {
    private ValueType valueType;

    protected Value(ValueType valueType) {
        this.valueType = valueType;
    }

    public abstract <T> T accept(ValueVisitor<T> valueVisitor);

    public enum ValueType {
        NUMBER,
        STRING,
        BOOLEAN,
        JSON,
        BYTE
    }
}
