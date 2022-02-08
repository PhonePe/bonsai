package com.phonepe.platform.bonsai.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MapNode extends Node {
    private Map<String, KeyNode> nodeMap;

    public MapNode() {
        super(NodeType.MAP, null, 0);
    }

    @Builder
    public MapNode(String id, long version, Map<String, KeyNode> nodeMap) {
        super(NodeType.MAP, id, version);
        this.nodeMap = nodeMap;
    }

    @Override
    public <T> T accept(NodeVisitor<T> nodeVisitor) {
        return nodeVisitor.visit(this);
    }
}
