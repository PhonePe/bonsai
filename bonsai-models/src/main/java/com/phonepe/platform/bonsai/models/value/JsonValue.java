package com.phonepe.platform.bonsai.models.value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author tushar.naik
 * @version 1.0  2019-07-01 - 16:43
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JsonValue extends Value {
    @NotNull
    private final JsonNode value;

    public JsonValue(@NotNull @JsonProperty("value") JsonNode value) {
        super(ValueType.JSON);
        this.value = value;
    }

    @Override
    public <T> T accept(ValueVisitor<T> valueVisitor) {
        return valueVisitor.visit(this);
    }
}
