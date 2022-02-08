package com.phonepe.platform.bonsai.models;

import com.phonepe.platform.bonsai.models.value.Value;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ValueNode extends Node {
    private Value value;

    public ValueNode() {
        super(NodeType.VALUE, null, 0);
    }

    @Builder
    public ValueNode(String id, long version, Value value) {
        super(NodeType.VALUE, id, version);
        this.value = value;
    }

    @Override
    public <T> T accept(NodeVisitor<T> nodeVisitor) {
        return nodeVisitor.visit(this);
    }
}
