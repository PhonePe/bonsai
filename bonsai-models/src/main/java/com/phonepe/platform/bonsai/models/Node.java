package com.phonepe.platform.bonsai.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * @author tushar.naik
 * @version 1.0  12/07/18 - 4:06 PM
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "nodeType")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "VALUE", value = ValueNode.class),
        @JsonSubTypes.Type(name = "LIST", value = ListNode.class),
        @JsonSubTypes.Type(name = "MAP", value = MapNode.class)
})
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

    public abstract <T> T accept(NodeVisitor<T> nodeVisitor);
}
