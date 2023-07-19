package com.phonepe.platform.bonsai.models.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.phonepe.platform.bonsai.models.value.BooleanValue;
import com.phonepe.platform.bonsai.models.value.ByteValue;
import com.phonepe.platform.bonsai.models.value.JsonValue;
import com.phonepe.platform.bonsai.models.value.NumberValue;
import com.phonepe.platform.bonsai.models.value.ObjectValue;
import com.phonepe.platform.bonsai.models.value.StringValue;
import com.phonepe.platform.bonsai.models.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ValueFlatNode extends FlatNode {

    private Value value;

    public ValueFlatNode() {
        super(FlatNodeType.VALUE);
    }

    public ValueFlatNode(Value value) {
        super(FlatNodeType.VALUE);
        this.value = value;
    }

    @Override
    public <T> T accept(FlatNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public static ValueFlatNode stringValue(String data) {
        return new ValueFlatNode(new StringValue(data));
    }

    public static ValueFlatNode numberValue(Number data) {
        return new ValueFlatNode(new NumberValue(data));
    }

    public static ValueFlatNode booleanValue(boolean data) {
        return new ValueFlatNode(new BooleanValue(data));
    }

    public static ValueFlatNode byteValue(byte[] data) {
        return new ValueFlatNode(new ByteValue(data));
    }

    public static ValueFlatNode jsonValue(JsonNode data) {
        return new ValueFlatNode(new JsonValue(data));
    }
    public static ValueFlatNode objectValue(Object data) {
        return new ValueFlatNode(new ObjectValue(data));
    }
}
