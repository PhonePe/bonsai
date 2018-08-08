package com.phonepe.platform.bonsai.models;

import lombok.Data;

/**
 * @author tushar.naik
 * @version 1.0  12/07/18 - 4:06 PM
 */
@Data
public abstract class Node {

    public enum NodeType {
        VALUE,
        LIST,
        MAP
    }

    private NodeType nodeType;
    private String id;
    private long version;

    protected Node(NodeType nodeType, String id, long version) {
        this.nodeType = nodeType;
        this.id = id;
        this.version = version;
    }
}
