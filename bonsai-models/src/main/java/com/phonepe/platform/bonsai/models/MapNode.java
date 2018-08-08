package com.phonepe.platform.bonsai.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  27/07/18 - 2:10 AM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MapNode extends Node {
    private Map<String, KeyNode> nodeMap;

    public MapNode(String id, long version) {
        super(NodeType.MAP, id, version);
    }

    @Builder
    public MapNode(String id, long version, Map<String, KeyNode> nodeMap) {
        super(NodeType.MAP, id, version);
        this.nodeMap = nodeMap;
    }
}
