package com.phonepe.platform.bonsai.core.vital.provided;

import com.google.common.base.Strings;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.data.MapKnotData;
import com.phonepe.platform.bonsai.core.data.MultiKnotData;
import com.phonepe.platform.bonsai.core.data.ValuedKnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.core.structures.CycleIdentifier;
import com.phonepe.platform.bonsai.core.structures.OrderedList;
import com.phonepe.platform.bonsai.core.vital.*;
import com.phonepe.platform.bonsai.core.vital.provided.model.Converters;
import com.phonepe.platform.bonsai.core.vital.provided.model.AtomicKnot;
import com.phonepe.platform.bonsai.core.vital.provided.model.AtomicEdge;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.ListNode;
import com.phonepe.platform.bonsai.models.MapNode;
import com.phonepe.platform.bonsai.models.ValueNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This is where the cool stuff happens.
 * The entire forest is maintained here. The adjacency tree representation,
 * whose keys point to {@link Knot}s, which contain recursive references to keys in the tree.
 * <p>
 * {@link Knot}(Knot is latin for Node) is how the directed Tree is represented.
 * Every {@link Knot} containsKey a  bunch of directional {@link Edge}s,
 * which all point to a certain {@link Knot} if its conditions are met
 * <p>
 * Key mappings represent the tree
 * Id mappings are for quick lookups on ids.
 *
 * @author tushar.naik
 * @version 1.0  11/07/18 - 2:09 PM
 */
public class IBonsai implements Bonsai {

    private MappingStore<String, String> mappingStore;
    private KnotStore<String, AtomicKnot, AtomicEdge> knotStore;
    private EdgeStore<String, AtomicEdge> edgeEdgeStore;
    private RequirementConditionEngine edgeConditionEngine;
    private ComponentValidator componentValidator;

    public IBonsai(MappingStore<String, String> mappingStore,
                   KnotStore<String, AtomicKnot, AtomicEdge> knotStore,
                   EdgeStore<String, AtomicEdge> edgeEdgeStore,
                   BonsaiProperties bonsaiProperties) {
        this.mappingStore = mappingStore;
        this.knotStore = knotStore;
        this.edgeEdgeStore = edgeEdgeStore;
        this.edgeConditionEngine = new RequirementConditionEngine();
        this.componentValidator = new ComponentValidator(bonsaiProperties);
        JsonPathSetup.setup();
    }

    @Override
    public Knot create(KnotData knotData) {
        componentValidator.validate(knotData);
        return Knot.builder()
                   .id(BonsaiIdGen.newId())
                   .knotData(knotData)
                   .version(System.currentTimeMillis())
                   .build();
    }

    @Override
    public Knot createMapping(String key, KnotData knotData) {
        componentValidator.validate(knotData);
        if (mappingStore.containsKey(key)) {
            return Converters.toKnot(knotStore.get(mappingStore.get(key)));
        }
        Knot knot = create(knotData);
        mappingStore.map(key, knot.getId());
        knotStore.create(knot.getId(), Converters.toAtomicKnot(knot));
        return knot;
    }

    @Override
    public Knot createMapping(String key, Knot knot) throws BonsaiError {
        componentValidator.validate(knot);
        checkForCycles(Converters.toAtomicKnot(knot));
        knot.updateVersion();
        mappingStore.map(key, knot.getId());
        knotStore.create(knot.getId(), Converters.toAtomicKnot(knot));

        /* connect every edge in the knot */
        knot.getEdges().forEach(edge -> connect(knot.getId(), edge));
        return knot;
    }

    @Override
    public boolean add(Knot knot) throws BonsaiError {
        return knotStore.create(knot.getId(), Converters.toAtomicKnot(knot));
    }

    @Override
    public boolean connect(String id, Edge edge) throws BonsaiError {
        componentValidator.validate(edge);
        if (!knotStore.containsKey(id)) {
            return false;
        }
        AtomicKnot knot = knotStore.get(id);
        if (knot.getEdges() == null) {
            knot.setEdges(new OrderedList<>());
        }

        /* if there is any edge with the same pivot, in the inner layer, throw exception */
        knot.getEdges()
            .stream()
            .map(s -> edgeEdgeStore.get(s))
            .filter(mEdge1 -> mEdge1 != null && !mEdge1.getPivot().equals(edge.getPivot()))
            .forEach(mEdge1 -> {
                throw new BonsaiError(BonsaiErrorCode.EDGE_PIVOT_CONSTRAINT_ERROR);
            });

        /* check for circular loops */
        AtomicEdge atomicEdge = Converters.toAtomicEdge(edge);
        checkForCycles(knot, atomicEdge);

        edgeEdgeStore.map(edge.getId(), atomicEdge);
        knot.getEdges().add(edge.getId());
        knotStore.update(knot);
        return true;
    }

    @Override
    public Knot get(String key) {
        return Converters.toKnot(knotStore.get(mappingStore.get(key)));
    }

    @Override
    public Knot getForId(String id) {
        return Converters.toKnot(knotStore.get(id));
    }

    @Override
    public KeyNode evaluate(String key, Context context) {
        componentValidator.validate(context);

        /* if context preferences already containsKey the key, return it */
        if (context.getPreferences() != null && context.getPreferences().containsKey(key)) {
            return context.getPreferences().get(key);
        }

        /* if the matching Knot is null, return empty */
        String id = mappingStore.get(key);

        /* if key mapping doesn't contain the key, return an empty KeyNode */
        if (Strings.isNullOrEmpty(id)) {
            return KeyNode.empty(key);
        }

        AtomicKnot knot = getMatchingNode(knotStore.get(id), context);
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
    private void checkForCycles(AtomicKnot knot) throws BonsaiError {
        checkForCycles(knot, new CycleIdentifier<>());
    }

    private void checkForCycles(AtomicKnot knot, AtomicEdge edge) throws BonsaiError {
        if (knot == null || edge == null || Strings.isNullOrEmpty(edge.getKnotId())) {
            return;
        }
        CycleIdentifier<AtomicKnot> cycleIdentifier = new CycleIdentifier<>();
        cycleIdentifier.add(knot);
        checkForCycles(knotStore.get(edge.getKnotId()), cycleIdentifier);
    }

    /**
     * recursively check for cycles in the tree
     *
     * @param knot            current knot
     * @param cycleIdentifier ds to identify cycles among {@link Knot}s
     * @throws BonsaiError error incase cycles are detected {@link BonsaiErrorCode#CYCLE_DETECTED}
     */
    private void checkForCycles(AtomicKnot knot, CycleIdentifier<AtomicKnot> cycleIdentifier) throws BonsaiError {
        if (knot == null) {
            return;
        }
        /* get the knot again from the mapping, else the reference of the incoming Knot from the Edge, will not be complete */
        AtomicKnot knotFromStore = knotStore.get(knot.getId());
        if (knotFromStore == null) {
            return;
        }
        cycleIdentifier.add(knotFromStore);
        if (knotFromStore.getEdges() != null) {
            for (String edgeId : knotFromStore.getEdges()) {
                AtomicEdge mEdge = edgeEdgeStore.get(edgeId);
                if (mEdge != null && Strings.isNullOrEmpty(mEdge.getKnotId())) {
                    AtomicKnot edgeKnot = knotStore.get(mEdge.getKnotId());
                    checkForCycles(edgeKnot, cycleIdentifier);
                }
            }
        }
        knotFromStore.getKnotData().accept(new KnotDataVisitor<Void>() {
            @Override
            public Void visit(ValuedKnotData valuedKnotData) {
                return null;
            }

            @Override
            public Void visit(MultiKnotData multiKnotData) {
                multiKnotData.getKeys().stream()
                             .map(mappingStore::get)
                             .map(knotStore::get)
                             .filter(Objects::nonNull)
                             .forEach(knot -> checkForCycles(knot, cycleIdentifier));
                return null;
            }

            @Override
            public Void visit(MapKnotData mapKnotData) {
                mapKnotData.getMapKeys()
                           .values()
                           .stream()
                           .filter(Objects::nonNull)
                           .map(mappingStore::get)
                           .map(knotStore::get)
                           .forEach(knot -> checkForCycles(knot, cycleIdentifier));
                return null;
            }
        });
    }


    /**
     * this method recursively traverses the keyMapping, along all the matching / condition-satisfying edges
     * until the point where no edges are present or no satisfying edges are present
     *
     * @param knot    current node being traversed
     * @param context current context
     * @return node after traversal
     */
    private AtomicKnot getMatchingNode(AtomicKnot knot, Context context) {
        if (knot == null) {
            return null;
        }
        OrderedList<String> edges = knot.getEdges();
        if (edges == null || edges.isEmpty()) {
            /* base condition for the recursion */
            return knot;
        }
        List<AtomicEdge> atomicEdges = edgeEdgeStore.getAll(edges);
        Optional<AtomicEdge> conditionSatisfyingEdge = edgeConditionEngine.match(context, atomicEdges);
        if (!conditionSatisfyingEdge.isPresent()) {
            /* base condition for the recursion */
            return knot;
        }
        /* recursively iterate over the edges knot */
        AtomicKnot iknot = knotStore.get(conditionSatisfyingEdge.get().getKnotId());
        AtomicKnot matchingNode = getMatchingNode(iknot, context);
        if (matchingNode == null) {
            return knot;
        }
        return matchingNode;
    }
}
