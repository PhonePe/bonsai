package com.phonepe.platform.bonsai.models.value;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author tushar.naik
 * @version 1.0  2019-07-01 - 16:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BooleanValue extends Value{
    private final boolean value;

    public BooleanValue(@JsonProperty("value")boolean value) {
        super(ValueType.BOOLEAN);
        this.value = value;
    }

    @Override
    public <T> T accept(ValueVisitor<T> valueVisitor) {
        return valueVisitor.visit(this);
    }
}
