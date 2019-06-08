package com.phonepe.platform.bonsai.models;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author tushar.naik
 * @version 1.0  23/08/18 - 3:36 AM
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NodeVisitors {

    public static boolean isList(Node node) {
        return node.accept(new NodeVisitor<Boolean>() {
            @Override
            public Boolean visit(ListNode listNode) {
                return true;
            }

            @Override
            public Boolean visit(ValueNode valueNode) {
                return false;
            }

            @Override
            public Boolean visit(MapNode mapNode) {
                return false;
            }
        });
    }

    public static boolean isValue(Node node) {
        return node.accept(new NodeVisitor<Boolean>() {
            @Override
            public Boolean visit(ListNode listNode) {
                return false;
            }

            @Override
            public Boolean visit(ValueNode valueNode) {
                return true;
            }

            @Override
            public Boolean visit(MapNode mapNode) {
                return false;
            }
        });
    }

    public static boolean isMap(Node node) {
        return node.accept(new NodeVisitor<Boolean>() {
            @Override
            public Boolean visit(ListNode listNode) {
                return false;
            }

            @Override
            public Boolean visit(ValueNode valueNode) {
                return false;
            }

            @Override
            public Boolean visit(MapNode mapNode) {
                return true;
            }
        });
    }
}
