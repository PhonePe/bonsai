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

package com.phonepe.commons.bonsai.core.vital;

import com.phonepe.commons.bonsai.core.structures.MapEntry;
import com.phonepe.commons.bonsai.models.KeyNode;
import com.phonepe.commons.bonsai.models.ListNode;
import com.phonepe.commons.bonsai.models.MapNode;
import com.phonepe.commons.bonsai.models.NodeVisitor;
import com.phonepe.commons.bonsai.models.ValueNode;
import com.phonepe.commons.bonsai.models.blocks.Knot;
import com.phonepe.commons.bonsai.models.model.FlatNodeDetail;
import com.phonepe.commons.bonsai.models.model.FlatTreeRepresentation;
import com.phonepe.commons.bonsai.models.model.ListFlatNode;
import com.phonepe.commons.bonsai.models.model.MapFlatNode;
import com.phonepe.commons.bonsai.models.model.ValueFlatNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TreeUtils {

    /**
     * flatten the tree representation into a flat map representation
     *
     * @param keyNode tree of nodes
     * @return flattened tree
     */
    static FlatTreeRepresentation flatten(KeyNode keyNode) {
        Map<String, FlatNodeDetail> flatNodeDetailMap = new LinkedHashMap<>();
        flatten(keyNode, flatNodeDetailMap);
        return new FlatTreeRepresentation(keyNode.getKey(), flatNodeDetailMap);
    }

    /**
     * returns true if the knot data classes are the same
     *
     * @param knot1 first knot
     * @param knot2 second knot
     * @return true if the knot data class is the same
     */
    static boolean isKnotDataOfSimilarType(Knot knot1, Knot knot2) {
        return knot1.getKnotData().getClass().equals(knot2.getKnotData().getClass());
    }

    /**
     * this recursively flattens the entire tree, into the mapping map
     *
     * @param keyNode tree of nodes
     * @param mapping map where the items will be added
     */
    private static void flatten(KeyNode keyNode, Map<String, FlatNodeDetail> mapping) {
        if (keyNode == null || keyNode.getNode() == null) {
            return;
        }
        keyNode.getNode().accept(new NodeVisitor<Void>() {
            @Override
            public Void visit(ListNode listNode) {
                List<KeyNode> nodes = listNode.getNodes();
                List<String> flatNodes = nodes != null
                        ? nodes.stream()
                        .peek(k -> flatten(k, mapping)) // recursive action on every item in the list
                        .map(KeyNode::getKey)
                        .toList()
                        : Collections.emptyList();
                mapping.put(
                        keyNode.getKey(),
                        FlatNodeDetail.builder()
                                .flatNode(new ListFlatNode(flatNodes))
                                .path(keyNode.getEdgePath())
                                .version(listNode.getVersion())
                                .edges(keyNode.getEdges())
                                .build());
                return null;
            }

            @Override
            public Void visit(ValueNode valueNode) {
                mapping.put(keyNode.getKey(),
                        FlatNodeDetail.builder()
                                .flatNode(new ValueFlatNode(valueNode.getValue()))
                                .path(keyNode.getEdgePath())
                                .version(valueNode.getVersion())
                                .edges(keyNode.getEdges())
                                .build());
                return null;
            }

            @Override
            public Void visit(MapNode mapNode) {
                Map<String, KeyNode> nodeMap = mapNode.getNodeMap();
                Map<String, String> flatNodesMap = nodeMap != null
                        ? nodeMap.entrySet()
                        .stream()
                        .peek(entry -> flatten(entry.getValue(), mapping))// recursive action on every item in the list
                        .map(entry -> MapEntry.of(
                                entry.getKey(),
                                entry.getValue().getKey()))
                        .collect(MapEntry.mapCollector())
                        : null;
                mapping.put(
                        keyNode.getKey(),
                        FlatNodeDetail.builder()
                                .flatNode(new MapFlatNode(flatNodesMap))
                                .version(mapNode.getVersion())
                                .path(keyNode.getEdgePath())
                                .edges(keyNode.getEdges())
                                .build());
                return null;
            }
        });
    }
}
