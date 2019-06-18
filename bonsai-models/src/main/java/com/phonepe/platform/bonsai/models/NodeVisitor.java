package com.phonepe.platform.bonsai.models;

/**
 * @author tushar.naik
 * @version 1.0  23/08/18 - 3:27 AM
 */
public interface NodeVisitor<T> {
    T visit(ListNode listNode);

    T visit(ValueNode valueNode);

    T visit(MapNode mapNode);
}
