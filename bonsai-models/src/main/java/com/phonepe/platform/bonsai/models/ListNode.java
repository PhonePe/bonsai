package com.phonepe.platform.bonsai.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ListNode extends Node {
    private List<KeyNode> nodes;

    public ListNode() {
        super(NodeType.LIST, null, 0);
    }

    @Builder
    public ListNode(String id, long version, @Singular List<KeyNode> nodes) {
        super(NodeType.LIST, id, version);
        this.nodes = nodes;
    }

    @Override
    public <T> T accept(NodeVisitor<T> nodeVisitor) {
        return nodeVisitor.visit(this);
    }
}
