package com.phonepe.platform.bonsai.models;

import com.phonepe.platform.bonsai.models.value.Value;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author tushar.naik
 * @version 1.0  27/07/18 - 2:10 AM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ValueNode extends Node {
    private Value value;

    public ValueNode(String id, long version) {
        super(NodeType.VALUE, id, version);
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
