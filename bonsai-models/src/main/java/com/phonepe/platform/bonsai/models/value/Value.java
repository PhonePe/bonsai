package com.phonepe.platform.bonsai.models.value;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * @author tushar.naik
 * @version 1.0  27/07/18 - 2:33 AM
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "valueType")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "DATA", value = DataValue.class),
        @JsonSubTypes.Type(name = "REFERENCE", value = ReferenceValue.class)
})
public abstract class Value {
    public enum ValueType {
        DATA,
        REFERENCE
    }

    private ValueType valueType;

    protected Value(ValueType valueType) {
        this.valueType = valueType;
    }

    public abstract <T> T accept(ValueVisitor<T> valueVisitor);
}
