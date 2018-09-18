package com.phonepe.platform.bonsai.models.value;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author tushar.naik
 * @version 1.0  27/07/18 - 2:33 AM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DataValue extends Value {
    private Object data;

    protected DataValue() {
        super(ValueType.DATA);
    }

    @Builder
    public DataValue(Object data) {
        super(ValueType.DATA);
        this.data = data;
    }

    @Override
    public <T> T accept(ValueVisitor<T> valueVisitor) {
        return valueVisitor.visit(this);
    }
}
