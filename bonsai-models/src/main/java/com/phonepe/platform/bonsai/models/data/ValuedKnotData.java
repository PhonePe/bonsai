package com.phonepe.platform.bonsai.models.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.phonepe.platform.bonsai.models.value.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author tushar.naik
 * @version 1.0  27/07/18 - 12:34 AM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ValuedKnotData extends KnotData {
    private Value value;

    public ValuedKnotData() {
        super(KnotDataType.VALUED);
    }

    @Builder
    public ValuedKnotData(Value value) {
        super(KnotDataType.VALUED);
        this.value = value;
    }

    @Override
    public <T> T accept(KnotDataVisitor<T> knotDataVisitor) {
        return knotDataVisitor.visit(this);
    }

    public static ValuedKnotData stringValue(String data) {
        return new ValuedKnotData(new StringValue(data));
    }

    public static ValuedKnotData numberValue(Number data) {
        return new ValuedKnotData(new NumberValue(data));
    }

    public static ValuedKnotData booleanValue(boolean data) {
        return new ValuedKnotData(new BooleanValue(data));
    }

    public static ValuedKnotData byteValue(byte[] data) {
        return new ValuedKnotData(new ByteValue(data));
    }

    public static ValuedKnotData jsonValue(JsonNode data) {
        return new ValuedKnotData(new JsonValue(data));
    }
}
