//package com.phonepe.platform.bonsai.core.vital.adjacency;
//
//import com.phonepe.platform.bonsai.core.Bonsai;
//import com.phonepe.platform.bonsai.core.data.MapKnotData;
//import com.phonepe.platform.bonsai.core.data.MultiKnotData;
//import com.phonepe.platform.bonsai.core.data.ValuedKnotData;
//import com.phonepe.platform.bonsai.core.exception.BonsaiError;
//import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
//import com.phonepe.platform.bonsai.core.query.filter.Filter;
//import com.phonepe.platform.bonsai.core.structures.CycleIdentifier;
//import com.phonepe.platform.bonsai.core.structures.OrderedList;
//import com.phonepe.platform.bonsai.core.vital.*;
//import com.phonepe.platform.bonsai.models.KeyNode;
//import com.phonepe.platform.bonsai.models.ListNode;
//import com.phonepe.platform.bonsai.models.MapNode;
//import com.phonepe.platform.bonsai.models.ValueNode;
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * This is where the cool stuff happens.
// * The entire forest is maintained here. The adjacency tree representation,
// * whose keys point to {@link Knot}s, which contain recursive references to keys in the tree.
// * <p>
// * {@link Knot}(Knot is latin for Node) is how the directed Tree is represented.
// * Every {@link Knot} contains a  bunch of directional {@link Edge}s,
// * which all point to a certain {@link Knot} if its filters are met
// * <p>
// * Key mappings represent the tree
// * Id mappings are for quick lookups on ids.
// *
// * @author tushar.naik
// * @version 1.0  11/07/18 - 2:09 PM
// */
//@Slf4j
//public class AdjacencyBonsai implements Bonsai {
//
//    private final Map<String, Knot> keyMapping;
//    private final Map<String, Knot> idMapping;
//    private final EdgeConditionEngine edgeConditionEngine;
//    private final ComponentValidator componentValidator;
//    private final BonsaiProperties bonsaiProperties;
//
//    public AdjacencyBonsai(BonsaiProperties bonsaiProperties) {
//        this.bonsaiProperties = bonsaiProperties;
//        this.keyMapping = new HashMap<>();
//        this.idMapping = new HashMap<>();
//        this.edgeConditionEngine = new EdgeConditionEngine();
//        this.componentValidator = new ComponentValidator(bonsaiProperties);
//        JsonPathSetup.setup();
//    }
//
//    @Override
//    public Knot create(KnotData knotData) {
////        System.out.println("Creating knotData:" + knotData);
//        componentValidator.validate(knotData);
//        return Knot.builder()
//                   .id(BonsaiIdGen.newId())
//                   .knotData(knotData)
//                   .version(System.currentTimeMillis())
//                   .build();
//    }
//
//    @Override
//    public Knot createMapping(String key, KnotData knotData) {
//        componentValidator.validate(knotData);
//        if (keyMapping.containsKey(key)) {
//            return keyMapping.get(key);
//        }
//        Knot knot = create(knotData);
//        keyMapping.put(key, knot);
//        idMapping.put(knot.getId(), knot);
//        log.info("Created knot:{}", knot);
//        return knot;
//    }
//
//    @Override
//    public Knot createMapping(String key, Knot knot) throws BonsaiError {
//        componentValidator.validate(knot);
//        checkForCycles(knot);
//        knot.updateVersion();
//        keyMapping.put(key, knot);
//        idMapping.put(knot.getId(), knot);
//        log.info("Created mapping key:{} knot:{}", key, knot);
//        return knot;
//    }
//
//    @Override
//    public boolean add(Knot knot) throws BonsaiError {
//        idMapping.put(knot.getId(), knot);
//        log.info("Added knot:{}", knot);
//        return true;
//    }
//
//    @Override
//    public boolean addVariation(String id, Edge edge) throws BonsaiError {
//        componentValidator.validate(edge);
//        if (!idMapping.containsKey(id)) {
//            return false;
//        }
//        Knot knot = idMapping.get(id);
//        if (knot.getEdges() == null) {
//            knot.setEdges(new OrderedList<>());
//        }
//
//        if (bonsaiProperties.isMutualExclusivitySettingTurnedOn()) {
//            Set<String> allFields = knot.getEdges()
//                                        .stream()
//                                        .flatMap(k -> k.getFilters().stream().map(Filter::getField))
//                                        .collect(Collectors.toSet());
//            if (allFields.size() > 1) {
//                throw new BonsaiError(BonsaiErrorCode.INVALID_STATE,
//                                      String.format("mutualExclusivitySettingTurnedOn but multiple fields exist for knot:%s fields:%s",
//                                                    knot.getId(), allFields));
//            }
//            if (!allFields.isEmpty() &&
//                    edge.getFilters().stream().anyMatch(filter -> !allFields.contains(filter.getField()))) {
//                throw new BonsaiError(BonsaiErrorCode.EDGE_PIVOT_CONSTRAINT_ERROR);
//            }
//        }
//        checkForCycles(knot, edge);
//        knot.getEdges().add(edge);
//        log.info("Connected knot:{} edge:{}", knot.getKnotData(), edge.getId());
////        System.out.println(String.format("Connected knot:%s edge:%s", knot.getId(), edge));
//        return true;
//    }
//
//    @Override
//    public Knot get(String key) {
//        return keyMapping.get(key);
//    }
//
//    @Override
//    public Knot getForId(String id) {
//        return idMapping.get(id);
//    }
//
//    @Override
//    public boolean updateEdge(String edgeId, List<Filter> filter) {
//        return false;
//    }
//
//    @Override
//    public boolean disconnect(Edge edge) {
//        return false;
//    }
//
//    @Override
//    public boolean update(Knot knot) {
//        return false;
//    }
//
//    @Override
//    public boolean destroy(Knot knot, boolean recursive) {
//        return false;
//    }
//
//    @Override
//    public KeyNode evaluate(String key, Context context) {
//        log.info("Evaluating key:{} context:{}");
//        componentValidator.validate(context);
//
//        /* if context preferences already contains the key, return it */
//        if (context.getPreferences() != null && context.getPreferences().containsKey(key)) {
//            return context.getPreferences().get(key);
//        }
//
//        /* if key mapping doesn't contain the key, return an empty KeyNode */
//        if (!keyMapping.containsKey(key)) {
//            return KeyNode.empty(key);
//        }
//
//        /* if the matching Knot is null, return empty */
//        Knot knot = getMatchingNode(keyMapping.get(key), context);
//        if (knot == null) {
//            return KeyNode.empty(key);
//        }
//
//        return knot.getKnotData().accept(new KnotDataVisitor<KeyNode>() {
//            @Override
//            public KeyNode visit(ValuedKnotData valuedKnotData) {
//                return new KeyNode(key,
//                                   ValueNode.builder()
//                                            .id(knot.getId())
//                                            .version(knot.getVersion())
//                                            .value(valuedKnotData.getValue())
//                                            .build());
//            }
//
//            @Override
//            public KeyNode visit(MultiKnotData multiKnotData) {
//                /* recursively evaluate the list of keys in MultiKnot */
//                return new KeyNode(key,
//                                   ListNode.builder()
//                                           .id(knot.getId())
//                                           .version(knot.getVersion())
//                                           .nodes(multiKnotData
//                                                          .getKeys()
//                                                          .stream()
//                                                          .map(key -> evaluate(key, context))
//                                                          .collect(Collectors.toList()))
//                                           .build());
//            }
//
//            @Override
//            public KeyNode visit(MapKnotData mapKnotData) {
//                /* recursively evaluate the keys withing the MapKnot data */
//                return new KeyNode(key,
//                                   MapNode.builder()
//                                          .id(knot.getId())
//                                          .version(knot.getVersion())
//                                          .nodeMap(mapKnotData
//                                                           .getMapKeys()
//                                                           .entrySet()
//                                                           .stream()
//                                                           .collect(Collectors.toMap(Map.Entry::getKey,
//                                                                                     entry -> evaluate(entry.getValue(), context))))
//                                          .build());
//            }
//        });
//    }
//
//    /**
//     * throw an exception if there is a cycle detected in the Bonsai
//     *
//     * @param knot knot from where cycle needs to be detected
//     * @throws BonsaiError throws {@link BonsaiErrorCode#CYCLE_DETECTED}
//     */
//    private void checkForCycles(Knot knot) throws BonsaiError {
//        checkForCycles(knot, new CycleIdentifier<>());
//    }
//
//    private void checkForCycles(Knot knot, Edge edge) throws BonsaiError {
//        if (knot == null || edge == null || edge.getKnot() == null) {
//            return;
//        }
//        CycleIdentifier<Knot> cycleIdentifier = new CycleIdentifier<>();
//        cycleIdentifier.add(knot);
//        checkForCycles(edge.getKnot(), cycleIdentifier);
//    }
//
//    /**
//     * recursively check for cycles in the tree
//     *
//     * @param knot            current knot
//     * @param cycleIdentifier ds to identify cycles among {@link Knot}s
//     * @throws BonsaiError error incase cycles are detected {@link BonsaiErrorCode#CYCLE_DETECTED}
//     */
//    private void checkForCycles(Knot knot, CycleIdentifier<Knot> cycleIdentifier) throws BonsaiError {
//        if (knot == null) {
//            return;
//        }
//        /* get the knot again from the mapping, else the reference of the incoming Knot from the Edge, will not be complete */
//        Knot knotFromStore = idMapping.get(knot.getId());
//        if (knotFromStore == null) {
//            return;
//        }
//        cycleIdentifier.add(knotFromStore);
//        if (knotFromStore.getEdges() != null) {
//            for (Edge edge1 : knot.getEdges()) {
//                if (edge1.getKnot() != null) {
//                    checkForCycles(edge1.getKnot(), cycleIdentifier);
//                }
//            }
//        }
//        knotFromStore.getKnotData().accept(new KnotDataVisitor<Void>() {
//            @Override
//            public Void visit(ValuedKnotData valuedKnotData) {
//                return null;
//            }
//
//            @Override
//            public Void visit(MultiKnotData multiKnotData) {
//                multiKnotData.getKeys().stream()
//                             .map(keyMapping::get)
//                             .filter(Objects::nonNull)
//                             .forEach(knot -> checkForCycles(knot, cycleIdentifier));
//                return null;
//            }
//
//            @Override
//            public Void visit(MapKnotData mapKnotData) {
//                mapKnotData.getMapKeys()
//                           .values()
//                           .stream()
//                           .filter(Objects::nonNull)
//                           .map(keyMapping::get)
//                           .forEach(knot -> checkForCycles(knot, cycleIdentifier));
//                return null;
//            }
//        });
//    }
//
//    /**
//     * this method recursively traverses the keyMapping, along all the matching / condition-satisfying edges
//     * until the point where no edges are present or no satisfying edges are present
//     *
//     * @param knot    current node being traversed
//     * @param context current context
//     * @return node after traversal
//     */
//    private Knot getMatchingNode(Knot knot, Context context) {
//        log.info("Matching node for knot:{} context:{}", knot, context);
//        if (knot == null) {
//            return null;
//        }
//        OrderedList<Edge> edges = knot.getEdges();
//        if (edges == null || edges.isEmpty()) {
//            /* base condition for the recursion */
//            return knot;
//        }
//        Optional<Edge> conditionSatisfyingEdge = edgeConditionEngine.match(context, edges);
//        if (!conditionSatisfyingEdge.isPresent()) {
//            /* base condition for the recursion */
//            return knot;
//        }
//        /* recursively iterate over the edges knot */
//        Knot matchingNode = getMatchingNode(conditionSatisfyingEdge.get().getKnot(), context);
//        if (matchingNode == null) {
//            return knot;
//        }
//        return matchingNode;
//    }
//}
