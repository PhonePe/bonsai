/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.phonepe.platform.bonsai.models;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
