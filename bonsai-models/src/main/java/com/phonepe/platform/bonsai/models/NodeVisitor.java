package com.phonepe.platform.bonsai.models;

public interface NodeVisitor<T> {
    T visit(ListNode listNode);

    T visit(ValueNode valueNode);

    T visit(MapNode mapNode);
}
