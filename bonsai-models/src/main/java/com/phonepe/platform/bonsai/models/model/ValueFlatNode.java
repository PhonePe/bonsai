package com.phonepe.platform.bonsai.models.model;

import com.phonepe.platform.bonsai.models.value.Value;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author tushar.naik
 * @version 1.0  2019-06-11 - 00:55
 */
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
}
