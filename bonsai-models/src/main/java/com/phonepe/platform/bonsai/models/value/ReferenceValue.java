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
public class ReferenceValue extends Value {
    private String reference;

    protected ReferenceValue() {
        super(ValueType.REFERENCE);
    }

    @Builder
    public ReferenceValue(String reference) {
        super(ValueType.REFERENCE);
        this.reference = reference;
    }
}
