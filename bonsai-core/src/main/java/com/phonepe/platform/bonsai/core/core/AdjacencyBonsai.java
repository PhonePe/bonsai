package com.phonepe.platform.bonsai.core.core;

import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.data.MapKnotData;
import com.phonepe.platform.bonsai.core.data.MultiKnotData;
import com.phonepe.platform.bonsai.core.data.ValuedKnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.core.structures.CycleIdentifier;
import com.phonepe.platform.bonsai.core.structures.OrderedList;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.ListNode;
import com.phonepe.platform.bonsai.models.MapNode;
import com.phonepe.platform.bonsai.models.ValueNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This is where the cool stuff happens.
 * The entire forest is maintained here. The adjacency tree representation,
 * whose {@link Knot}s contain recursive references to the same Node
 * Id mappings are for quick lookups on ids.
 * {@link Knot}(Knot is latin for Node) is how the directed Tree is represented.
 *
 * @author tushar.naik
 * @version 1.0  11/07/18 - 2:09 PM
 */
public class AdjacencyBonsai implements Bonsai {

    private Map<String, Knot> keyMapping;
    private Map<String, Knot> idMapping;
    private EdgeConditionEngine edgeConditionEngine;

    public AdjacencyBonsai() {
        this.keyMapping = new HashMap<>();
        this.idMapping = new HashMap<>();
        this.edgeConditionEngine = new EdgeConditionEngine();
    }

    @Override
    public Knot create(KnotData knotData) {
        return Knot.builder()
                   .id(UUID.randomUUID().toString())
                   .knotData(knotData)
                   .version(System.currentTimeMillis())
                   .build();
    }

    @Override
    public Knot add(String key, KnotData knotData) {
        if (keyMapping.containsKey(key)) {
            return keyMapping.get(key);
        }
        Knot knot = create(knotData);
        keyMapping.put(key, knot);
        idMapping.put(knot.getId(), knot);
        return knot;
    }

    @Override
    public Knot add(String key, Knot knot) throws BonsaiError {
        checkForCycles(knot);
        knot.updateVersion();
        keyMapping.put(key, knot);
        idMapping.put(knot.getId(), knot);
        return knot;
    }

    @Override
    public boolean connect(String id, Edge edge) throws BonsaiError {
        if (!idMapping.containsKey(id)) {
            return false;
        }
        Knot knot = idMapping.get(id);
        if (knot.getEdges() == null) {
            knot.setEdges(new OrderedList<>());
        }

        knot.getEdges().stream().findAny().ifPresent(anyEdge -> {
            if (!anyEdge.getPivot().equals(edge.getPivot())) {
                throw new BonsaiError(BonsaiErrorCode.EDGE_PIVOT_CONSTRAINT_ERROR);
            }
        });

        CycleIdentifier<Knot> knotCycleIdentifier = new CycleIdentifier<>();
        knotCycleIdentifier.add(knot);
        checkForCycles(edge, knotCycleIdentifier);

        knot.getEdges().add(edge);
        return true;
    }

    @Override
    public Knot get(String key) {
        return keyMapping.get(key);
    }

    @Override
    public Knot getForId(String id) {
        return idMapping.get(id);
    }

    @Override
    public KeyNode evaluate(String key, Context context) {

        /* if context preferences already contains the key, return it */
        if (context.getPreferences().containsKey(key)) {
            return context.getPreferences().get(key);
        }

        /* if key mapping doesn't contain the key, return an empty KeyNode */
        if (!keyMapping.containsKey(key)) {
            return KeyNode.empty(key);
        }

        /* if the matching Knot is null, return empty */
        Knot knot = getMatchingNode(keyMapping.get(key), context);
        if (knot == null) {
            return KeyNode.empty(key);
        }

        return knot.getKnotData().accept(new KnotDataVisitor<KeyNode>() {
            @Override
            public KeyNode visit(ValuedKnotData valuedKnotData) {
                return new KeyNode(key,
                                   ValueNode.builder()
                                            .id(knot.getId())
                                            .version(knot.getVersion())
                                            .value(valuedKnotData.getValue())
                                            .build());
            }

            @Override
            public KeyNode visit(MultiKnotData multiKnotData) {
                /* recursively evaluate the list of keys in MultiKnot */
                return new KeyNode(key,
                                   ListNode.builder()
                                           .id(knot.getId())
                                           .version(knot.getVersion())
                                           .nodes(multiKnotData
                                                          .getKeys()
                                                          .stream()
                                                          .map(key -> evaluate(key, context))
                                                          .collect(Collectors.toList()))
                                           .build());
            }

            @Override
            public KeyNode visit(MapKnotData mapKnotData) {
                /* recursively evaluate the keys withing the MapKnot data */
                return new KeyNode(key,
                                   MapNode.builder()
                                          .id(knot.getId())
                                          .version(knot.getVersion())
                                          .nodeMap(mapKnotData
                                                           .getMapKeys()
                                                           .entrySet()
                                                           .stream()
                                                           .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                     entry -> evaluate(entry.getValue(), context))))
                                          .build());
            }
        });
    }

    /**
     * throw an exception if there is a cycle detected in the Bonsai
     *
     * @param knot knot from where cycle needs to be detected
     * @throws BonsaiError throws {@link BonsaiErrorCode#CYCLE_DETECTED}
     */
    private void checkForCycles(Knot knot) throws BonsaiError {
        CycleIdentifier<Knot> knotCycleIdentifier = new CycleIdentifier<>();
        knotCycleIdentifier.add(knot);
        for (Edge edge : knot.getEdges()) {
            checkForCycles(edge, knotCycleIdentifier);
        }
    }

    /**
     * recursively check for cycles in the tree
     *
     * @param edge            current edge
     * @param cycleIdentifier ds to identify cycles among {@link Knot}s
     * @throws BonsaiError error incase cycles are detected {@link BonsaiErrorCode#CYCLE_DETECTED}
     */
    private void checkForCycles(Edge edge, CycleIdentifier<Knot> cycleIdentifier) throws BonsaiError {
        if (edge == null || edge.getKnot() == null) {
            return;
        }
        cycleIdentifier.add(edge.getKnot());
        if (edge.getKnot().getEdges() != null) {
            for (Edge edge1 : edge.getKnot().getEdges()) {
                checkForCycles(edge1, cycleIdentifier);
            }
        }
    }

    /**
     * this method recursively traverses the keyMapping, along all the matching / condition-satisfying edges
     * until the point where no edges are present or no satisfying edges are present
     *
     * @param knot    current node being traversed
     * @param context current context
     * @return node after traversal
     */
    private Knot getMatchingNode(Knot knot, Context context) {
        if (knot == null) {
            return null;
        }
        OrderedList<Edge> edges = knot.getEdges();
        if (edges == null || edges.isEmpty()) {
            /* base condition for the recursion */
            return knot;
        }
        Optional<Edge> conditionSatisfyingEdge = edgeConditionEngine.match(context, edges);
        if (!conditionSatisfyingEdge.isPresent()) {
            /* base condition for the recursion */
            return knot;
        }
        /* recursively iterate over the edges knot */
        Knot matchingNode = getMatchingNode(conditionSatisfyingEdge.get().getKnot(), context);
        if (matchingNode == null) {
            return knot;
        }
        return matchingNode;
    }
}
