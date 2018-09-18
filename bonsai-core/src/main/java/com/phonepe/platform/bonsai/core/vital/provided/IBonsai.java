package com.phonepe.platform.bonsai.core.vital.provided;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.data.MapKnotData;
import com.phonepe.platform.bonsai.core.data.MultiKnotData;
import com.phonepe.platform.bonsai.core.data.ValuedKnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.core.query.filter.Filter;
import com.phonepe.platform.bonsai.core.structures.CycleIdentifier;
import com.phonepe.platform.bonsai.core.structures.OrderedList;
import com.phonepe.platform.bonsai.core.vital.*;
import com.phonepe.platform.bonsai.core.vital.provided.model.AtomicEdge;
import com.phonepe.platform.bonsai.core.vital.provided.model.AtomicKnot;
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
 * which all point to a certain {@link Knot} if its filters are met
 * <p>
 * Key mappings represent the tree
 * Id mappings are for quick lookups on ids.
 *
 * @author tushar.naik
 * @version 1.0  11/07/18 - 2:09 PM
 */
public class IBonsai implements Bonsai {

    private MappingStore<String, String> mappingStore;
    private KnotStore<String, AtomicKnot> knotStore;
    private EdgeStore<String, AtomicEdge> edgeStore;
    private RequirementConditionEngine edgeConditionEngine;
    private ComponentValidator componentValidator;
    private BonsaiProperties bonsaiProperties;
    private BonsaiIdGenerator bonsaiIdGenerator;

    public IBonsai(MappingStore<String, String> mappingStore,
                   KnotStore<String, AtomicKnot> knotStore,
                   EdgeStore<String, AtomicEdge> edgeStore,
                   BonsaiProperties bonsaiProperties) {
        this.mappingStore = mappingStore;
        this.knotStore = knotStore;
        this.edgeStore = edgeStore;
        this.edgeConditionEngine = new RequirementConditionEngine();
        this.componentValidator = new ComponentValidator(bonsaiProperties);
        this.bonsaiProperties = bonsaiProperties;
        this.bonsaiIdGenerator = new BonsaiIdGenerator() { //todo remove this
            @Override
            public String newEdgeId() {
                return BonsaiIdGen.newId();
            }

            @Override
            public String newKnotId() {
                return BonsaiIdGen.newId();
            }
        };
        JsonPathSetup.setup();
    }

    @Override
    public AtomicKnot createKnot(KnotData knotData) {
        componentValidator.validate(knotData);
        AtomicKnot atomicKnot = AtomicKnot.builder()
                                          .id(bonsaiIdGenerator.newKnotId())
                                          .knotData(knotData)
                                          .version(System.currentTimeMillis())
                                          .build();
        knotStore.mapKnot(atomicKnot.getId(), atomicKnot);
        return atomicKnot;
    }

    @Override
    public AtomicKnot getKnot(String knotId) {
        return knotStore.get(mappingStore.get(knotId));
    }

    @Override
    public boolean updateKnotData(String knotId, KnotData knotData) {
        AtomicKnot atomicKnot = knotStore.get(knotId);
        atomicKnot.setKnotData(knotData);
        return knotStore.mapKnot(knotId, atomicKnot.updateVersion());
    }

    public List<AtomicKnot> deleteKnot(String id, boolean recursive) {
        List<AtomicKnot> deletedKnots = Lists.newArrayList();
        if (recursive) {
            /* this is a recursive delete operation */
            AtomicKnot atomicKnot = knotStore.get(id);
            edgeStore.getAll(atomicKnot.getEdges())
                     .stream().map(AtomicEdge::getKnotId)
                     .map(knotId -> deleteKnot(knotId, true))
                     .forEach(deletedKnots::addAll);

        }
        deletedKnots.add(knotStore.delete(id));
        return deletedKnots;
    }

    @Override
    public String addVariation(String knotId, Variation variation) throws BonsaiError {
        componentValidator.validate(variation);
        if (!knotStore.containsKey(knotId)) {
            return null; //todo change this
        }
        AtomicKnot atomicKnot = knotStore.get(knotId);
        if (atomicKnot.getEdges() == null) {
            atomicKnot.setEdges(new OrderedList<>());
        }

        AtomicEdge atomicEdge = AtomicEdge.builder()
                                          .id(bonsaiIdGenerator.newEdgeId())
                                          .knotId(variation.getKnotId())
                                          .priority(variation.getPriority())
                                          .version(System.currentTimeMillis())
                                          .filters(variation.getFilters())
                                          .build();

        /* if there is any edge with a different pivot (ie, condition is on a different field), in the inner layer, throw exception */
        validateConstraints(atomicKnot, atomicEdge);

        /* check for circular loops */
        checkForCycles(atomicKnot, atomicEdge);

        edgeStore.mapEdge(atomicEdge.getId(), atomicEdge);
        atomicKnot.getEdges().add(atomicEdge.getId());
        knotStore.mapKnot(atomicKnot.getId(), atomicKnot);
        return atomicEdge.getId();
    }

    @Override
    public boolean updateEdgeFilters(String knotId, String edgeId, List<Filter> filters) {
        AtomicKnot atomicKnot = knotStore.get(knotId);
        AtomicEdge atomicEdge = edgeStore.get(edgeId);
        if (atomicEdge == null) {
            return false;
        }
        atomicEdge.setFilters(filters);

        /* if there is any edge with a different pivot (ie, condition is on a different field), in the inner layer, throw exception */
        validateConstraints(atomicKnot, atomicEdge);

        edgeStore.mapEdge(edgeId, atomicEdge.updateVersion());
        return true;
    }


    @Override
    public boolean addEdgeFilters(String edgeId, List<Filter> filters) {
        AtomicEdge atomicEdge = edgeStore.get(edgeId);
        if (atomicEdge == null) {
            return false;
        }
        atomicEdge.getFilters().addAll(filters);
        componentValidator.validate(atomicEdge);
        if (bonsaiProperties.isMutualExclusivitySettingTurnedOn()) {
            Set<String> allFields = atomicEdge.getFilters().stream().map(Filter::getField).collect(Collectors.toSet());
            if (!allFields.isEmpty() && allFields.size() > 1) {
                throw new BonsaiError(BonsaiErrorCode.EDGE_PIVOT_CONSTRAINT_ERROR);
            }
        }
        edgeStore.mapEdge(edgeId, atomicEdge);
        return true;
    }

    @Override
    public boolean unlinkVariation(String knotId, String edgeId) {
        AtomicKnot atomicKnot = knotStore.get(knotId);
        atomicKnot.setEdges(atomicKnot.getEdges().stream().filter(eid -> !eid.equals(edgeId))
                                      .collect(Collectors.toCollection(OrderedList::new)));
        return knotStore.mapKnot(knotId, atomicKnot);
    }


    @Override
    public List<AtomicKnot> deleteVariation(String knotId, String edgeId, boolean recursive) {
        //todo check this piece
        unlinkVariation(knotId, edgeId);
        AtomicEdge atomicEdge = edgeStore.get(edgeId);
        List<AtomicKnot> atomicKnots = deleteKnot(atomicEdge.getKnotId(), recursive);
        edgeStore.delete(edgeId);
        return atomicKnots;
    }

    @Override
    public AtomicEdge getEdge(String edgeId) {
        return edgeStore.get(edgeId);
    }


    @Override
    public AtomicKnot createMapping(String key, String knotId) {
        AtomicKnot atomicKnot = knotStore.get(knotId);
        if (atomicKnot == null) {
            //todo
        }
        if (mappingStore.containsKey(key)) {
            return knotStore.get(mappingStore.get(key));
        }
        String olderMappedKey = mappingStore.map(key, knotId);
        return knotStore.get(olderMappedKey);
    }

    @Override
    public AtomicKnot createMapping(String key, KnotData knotData) throws BonsaiError {
        AtomicKnot createdKnot = createKnot(knotData);
        createMapping(key, createdKnot.getId());
        return createdKnot;
    }

    @Override
    public AtomicKnot removeMapping(String key) throws BonsaiError {
        return knotStore.get(mappingStore.remove(key));
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
     * @param edge edge along which cycle is detected
     * @throws BonsaiError throws {@link BonsaiErrorCode#CYCLE_DETECTED}
     */
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
                AtomicEdge mEdge = edgeStore.get(edgeId);
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
        List<AtomicEdge> atomicEdges = edgeStore.getAll(edges);
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


    private void validateConstraints(AtomicKnot atomicKnot, AtomicEdge atomicEdge) {
        if (bonsaiProperties.isMutualExclusivitySettingTurnedOn()) {
            Set<String> allFields = edgeStore.getAll(atomicKnot.getEdges())
                                             .stream()
                                             .flatMap(k -> k.getFilters().stream().map(Filter::getField))
                                             .collect(Collectors.toSet());
            if (allFields.size() > 1) {
                throw new BonsaiError(BonsaiErrorCode.INVALID_STATE,
                                      String.format("mutualExclusivitySettingTurnedOn but multiple fields exist for knot:%s fields:%s",
                                                    atomicKnot.getId(), allFields));
            }
            if (!allFields.isEmpty() &&
                    atomicEdge.getFilters().stream().anyMatch(filter -> !allFields.contains(filter.getField()))) {
                throw new BonsaiError(BonsaiErrorCode.EDGE_PIVOT_CONSTRAINT_ERROR);
            }
        }
    }
}
