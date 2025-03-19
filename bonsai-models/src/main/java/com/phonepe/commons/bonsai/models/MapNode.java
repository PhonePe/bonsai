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

package com.phonepe.commons.bonsai.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MapNode extends Node {
    private Map<String, KeyNode> nodeMap;

    public MapNode() {
        super(NodeType.MAP, null, 0);
    }

    @Builder
    public MapNode(String id, long version, Map<String, KeyNode> nodeMap) {
        super(NodeType.MAP, id, version);
        this.nodeMap = nodeMap;
    }

    @Override
    public <T> T accept(NodeVisitor<T> nodeVisitor) {
        return nodeVisitor.visit(this);
    }
}
