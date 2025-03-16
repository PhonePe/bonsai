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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "nodeType")
@JsonSubTypes({
        @JsonSubTypes.Type(name = "VALUE", value = ValueNode.class),
        @JsonSubTypes.Type(name = "LIST", value = ListNode.class),
        @JsonSubTypes.Type(name = "MAP", value = MapNode.class)
})
public abstract class Node {

    private NodeType nodeType;
    private String id;
    private long version;

    protected Node(NodeType nodeType, String id, long version) {
        this.nodeType = nodeType;
        this.id = id;
        this.version = version;
    }

    public abstract <T> T accept(NodeVisitor<T> nodeVisitor);

    public enum NodeType {
        VALUE,
        LIST,
        MAP
    }
}
