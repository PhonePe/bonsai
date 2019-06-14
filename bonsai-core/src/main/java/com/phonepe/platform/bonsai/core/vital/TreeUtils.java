package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.core.structures.MapEntry;
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
public class TreeUtils {

    public static FlatTreeRepresentation flatten(KeyNode keyNode) {
        Map<String, FlatNodeDetail> flatNodeDetailMap = new LinkedHashMap<>();
        flatten(keyNode, flatNodeDetailMap);
        return new FlatTreeRepresentation(keyNode.getKey(), flatNodeDetailMap);
    }

    private static void flatten(KeyNode keyNode, Map<String, FlatNodeDetail> mapping) {
        if (keyNode == null || keyNode.getNode() == null) {
            return;
        }
        keyNode.getNode().accept(new NodeVisitor<Void>() {
            @Override
            public Void visit(ListNode listNode) {
                mapping.put(keyNode.getKey(),
                            FlatNodeDetail.builder()
                                          .flatNode(
                                                  new ListFlatNode(listNode.getNodes()
                                                                           .stream()
                                                                           // recursive action on every item in the list
                                                                           .peek(k -> flatten(k, mapping))
                                                                           .map(KeyNode::getKey)
                                                                           .collect(Collectors.toList())))
//                                          .path() todo
                                          .build());
                return null;
            }

            @Override
            public Void visit(ValueNode valueNode) {
                mapping.put(keyNode.getKey(),
                            FlatNodeDetail.builder()
                                          .flatNode(new ValueFlatNode(valueNode.getValue()))
//                                          .path() todo
                                          .build());
                return null;
            }

            @Override
            public Void visit(MapNode mapNode) {
                mapping.put(keyNode.getKey(),
                            FlatNodeDetail.builder()
                                          .flatNode(
                                                  new MapFlatNode(mapNode.getNodeMap()
                                                                         .entrySet()
                                                                         .stream()
                                                                         // recursive action on every item in the list
                                                                         .peek(entry -> flatten(entry.getValue(), mapping))
                                                                         .map(entry -> MapEntry.of(
                                                                                 entry.getKey(),
                                                                                 entry.getValue().getKey()))
                                                                         .collect(MapEntry.mapCollector())))
//                                          .path() todo
                                          .build());
                return null;
            }
        });
    }
}
