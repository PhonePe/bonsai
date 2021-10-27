package com.phonepe.platform.bonsai.models.value;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ByteValue extends Value {
    @NotNull
    private final byte[] value;

    public ByteValue(@NotNull @JsonProperty("value") byte[] value) {
        super(ValueType.BYTE);
        this.value = value;
    }

    @Override
    public <T> T accept(ValueVisitor<T> valueVisitor) {
        return valueVisitor.visit(this);
    }
}
