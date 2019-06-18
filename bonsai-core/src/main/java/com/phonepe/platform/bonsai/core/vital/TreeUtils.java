package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.core.structures.MapEntry;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.models.*;
import com.phonepe.platform.bonsai.models.model.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tushar.naik
 * @version 1.0  2019-06-11 - 01:15
 */
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
                mapping.put(
                        keyNode.getKey(),
                        FlatNodeDetail.builder()
                                      .flatNode(
                                              new ListFlatNode(
                                                      listNode.getNodes()
                                                              .stream()
                                                              .peek(k -> flatten(k, mapping)) // recursive action on every item in the list
                                                              .map(KeyNode::getKey)
                                                              .collect(Collectors.toList())))
                                      .path(keyNode.getEdgePath())
                                      .version(listNode.getVersion())
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
                                          .build());
                return null;
            }

            @Override
            public Void visit(MapNode mapNode) {
                mapping.put(
                        keyNode.getKey(),
                        FlatNodeDetail.builder()
                                      .flatNode(
                                              new MapFlatNode(
                                                      mapNode.getNodeMap()
                                                             .entrySet()
                                                             .stream()
                                                             .peek(entry -> flatten(entry.getValue(), mapping))// recursive action on every item in the list
                                                             .map(entry -> MapEntry.of(
                                                                     entry.getKey(),
                                                                     entry.getValue().getKey()))
                                                             .collect(MapEntry.mapCollector())))
                                      .version(mapNode.getVersion())
                                      .path(keyNode.getEdgePath())
                                      .build());
                return null;
            }
        });
    }
}
