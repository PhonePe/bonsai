package com.phonepe.platform.bonsai.models;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractNodeVisitor<T> implements NodeVisitor<T> {
    private final T defaultValue;

    public AbstractNodeVisitor() {
        this(null);
    }

    public AbstractNodeVisitor(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public T visit(ListNode listNode) {
        return defaultValue;
    }

    @Override
    public T visit(ValueNode valueNode) {
        return defaultValue;
    }

    @Override
    public T visit(MapNode mapNode) {
        return defaultValue;
    }
}
