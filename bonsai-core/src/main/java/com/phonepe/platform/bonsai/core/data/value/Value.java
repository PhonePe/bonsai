package com.phonepe.platform.bonsai.core.data.value;

import lombok.Data;

/**
 * @author tushar.naik
 * @version 1.0  27/07/18 - 2:33 AM
 */
@Data
public abstract class Value {
    public enum ValueType {
        DATA,
        REFERENCE
    }

    private ValueType valueType;

    protected Value(ValueType valueType) {
        this.valueType = valueType;
    }
}
