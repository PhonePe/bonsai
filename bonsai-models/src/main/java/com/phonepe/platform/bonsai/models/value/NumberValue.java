package com.phonepe.platform.bonsai.models.value;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author tushar.naik
 * @version 1.0  2019-07-01 - 16:32
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NumberValue extends Value {
    @NotNull
    private final Number value;

    @Builder
    public NumberValue(@NotNull @JsonProperty("value") Number value) {
        super(ValueType.NUMBER);
        this.value = value;
    }

    @Override
    public <T> T accept(ValueVisitor<T> valueVisitor) {
        return valueVisitor.visit(this);
    }
}
