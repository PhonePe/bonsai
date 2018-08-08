package com.phonepe.platform.bonsai.models;

import lombok.*;

import java.util.List;

/**
 * @author tushar.naik
 * @version 1.0  27/07/18 - 2:10 AM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ListNode extends Node {
    private List<KeyNode> nodes;

    public ListNode(String id, long version) {
        super(NodeType.LIST, id, version);
    }

    @Builder
    public ListNode(String id, long version, @Singular List<KeyNode> nodes) {
        super(NodeType.LIST, id, version);
        this.nodes = nodes;
    }
}
