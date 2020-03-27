package com.phonepe.platform.bonsai.core.vital;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.core.structures.ConflictResolver;
import com.phonepe.platform.bonsai.core.structures.CycleIdentifier;
import com.phonepe.platform.bonsai.core.visitor.delta.impl.SaveDataOperationIntoStoreVisitor;
import com.phonepe.platform.bonsai.core.visitor.delta.impl.TreeKnotDeltaOperationModifierVisitor;
import com.phonepe.platform.bonsai.core.vital.provided.EdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KeyTreeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KnotStore;
import com.phonepe.platform.bonsai.core.vital.provided.Stores;
import com.phonepe.platform.bonsai.core.vital.provided.VariationSelectorEngine;
import com.phonepe.platform.bonsai.json.eval.JsonPathSetup;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.ListNode;
import com.phonepe.platform.bonsai.models.MapNode;
import com.phonepe.platform.bonsai.models.ValueNode;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.blocks.delta.DeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVisitor;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVoidVisitor;
import com.phonepe.platform.bonsai.models.blocks.model.Converters;
import com.phonepe.platform.bonsai.models.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import com.phonepe.platform.bonsai.models.data.KnotData;
import com.phonepe.platform.bonsai.models.data.KnotDataVisitor;
import com.phonepe.platform.bonsai.models.data.MapKnotData;
import com.phonepe.platform.bonsai.models.data.MultiKnotData;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.bonsai.models.model.FlatTreeRepresentation;
import com.phonepe.platform.bonsai.models.structures.OrderedList;
import com.phonepe.platform.query.dsl.FilterFieldIdentifier;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is where the cool stuff happens.
 * The entire forest is maintained here. The adjacency tree representation,
 * whose keys point to {@link Knot}s, which contain recursive references to keys in the tree.
 * <p>
 * {@link Knot}(Knot is latin for Node) is how the directed Tree is represented.
 * Every {@link Knot} contains a bunch of directional {@link Edge}s,
 * which all point to a certain {@link Knot} if its filters are met
 * <p>
 * Key mappings represent the tree
 * Id mappings are for quick lookups on ids.
 *
 * @author tushar.naik
 * @version 1.0  11/07/18 - 2:09 PM
 */
@Slf4j
public class BonsaiTree<C extends Context> implements Bonsai<C> {

    private static final FilterFieldIdentifier FIELD_IDENTIFIER = new FilterFieldIdentifier();

    private final KeyTreeStore<String, String> keyTreeStore;
    private final KnotStore<String, Knot> knotStore;
    private final EdgeStore<String, Edge> edgeStore;
    private final VariationSelectorEngine<C> variationSelectorEngine;
    private final ComponentBonsaiTreeValidator componentValidator;
    private final BonsaiProperties bonsaiProperties;
    private final BonsaiIdGenerator bonsaiIdGenerator;
    private final ConflictResolver<Knot> knotConflictResolver;
    private final DeltaOperationVisitor<TreeKnot> treeKnotDeltaOperationModifier;
    private final DeltaOperationVoidVisitor saveDataOperationIntoStore;

    public BonsaiTree(final Stores<String, String, Knot, Edge> stores,
                      final VariationSelectorEngine<C> variationSelectorEngine,
                      final ComponentBonsaiTreeValidator componentValidator,
                      final BonsaiProperties bonsaiProperties,
                      final BonsaiIdGenerator bonsaiIdGenerator,
                      final ConflictResolver<Knot> knotConflictResolver) {
        this.keyTreeStore = stores.getKeyTreeStore();
        this.knotStore = stores.getKnotStore();
        this.edgeStore = stores.getEdgeStore();
        this.variationSelectorEngine = variationSelectorEngine;
        this.componentValidator = componentValidator;
        this.bonsaiProperties = bonsaiProperties;
        this.bonsaiIdGenerator = bonsaiIdGenerator;
        this.knotConflictResolver = knotConflictResolver;
        this.treeKnotDeltaOperationModifier = new TreeKnotDeltaOperationModifierVisitor(componentValidator, knotStore, edgeStore);
        this.saveDataOperationIntoStore = new SaveDataOperationIntoStoreVisitor(keyTreeStore, knotStore, edgeStore);
        JsonPathSetup.setup();
    }

    @Override
    public Knot createKnot(Knot knot) {
        componentValidator.validate(knot);
        return knotStore.mapKnot(knot.getId(), knot);
    }

    @Override
    public boolean containsKnot(String knotId) {
        return knotStore.containsKnot(knotId);
    }

    @Override
    public Knot createKnot(KnotData knotData) {
        Knot knot = Knot.builder()
                        .id(bonsaiIdGenerator.newKnotId())
                        .knotData(knotData)
                        .version(System.currentTimeMillis())
                        .build();
        componentValidator.validate(knot);
        knotStore.mapKnot(knot.getId(), knot);
        return knot;
    }

    @Override
    public Knot getKnot(String knotId) {
        return knotStore.getKnot(knotId);
    }

    @Override
    public Knot updateKnotData(String knotId, KnotData knotData) {
        Knot knot = knotStore.getKnot(knotId);
        knot.setKnotData(knotData);
        componentValidator.validate(knot);
        return knotStore.mapKnot(knotId, knot.updateVersion());
    }

    public TreeKnot deleteKnot(String id, boolean recursive) {
        Knot knot = knotStore.getKnot(id);
        if (knot != null) {
            TreeKnot treeKnot = Converters.toTreeKnot(knot);
            /* this is a recursive delete operation */
            if (recursive && knot.getEdges() != null) {
                //todo optimize and handle failures
                Map<String, Edge> allEdges = edgeStore
                        .getAllEdges(knot.getEdges()
                                         .stream()
                                         .map(EdgeIdentifier::getId)
                                         .collect(Collectors.toList()));
                List<TreeEdge> collectedTreeEdges = allEdges
                        .values()
                        .stream()
                        .map(edge -> deleteVariation(knot.getId(), edge.getEdgeIdentifier().getId(), true))
                        .collect(Collectors.toList());
                treeKnot.setTreeEdges(collectedTreeEdges);

            }
            knotStore.deleteKnot(id);
            return treeKnot;
        }
        return null;
    }

    @Override
    public boolean containsEdge(String edgeId) {
        return edgeStore.containsEdge(edgeId);
    }

    @Override
    public Edge addVariation(String knotId, Variation variation) {
        componentValidator.validate(variation);
        Knot knot = knotStore.getKnot(knotId);
        if (knot == null) {
            throw new BonsaiError(BonsaiErrorCode.KNOT_ABSENT, "root knot absent:" + knotId);
        }

        if (knot.getEdges() == null) {
            knot.setEdges(new OrderedList<>());
        }

        Knot variationKnot = knotStore.getKnot(variation.getKnotId());
        if (variationKnot == null) {
            throw new BonsaiError(BonsaiErrorCode.KNOT_ABSENT, "variation knot absent:" + variation.getKnotId());
        }
        componentValidator.validate(knot, variationKnot);

        Edge edge = Edge.builder()
                        .edgeIdentifier(new EdgeIdentifier(bonsaiIdGenerator.newEdgeId(),
                                                           bonsaiIdGenerator.newEdgeNumber(knot.getEdges()),
                                                           variation.getPriority()))
                        .knotId(variation.getKnotId())
                        .version(System.currentTimeMillis())
                        .live(variation.isLive())
                        .percentage(variation.getPercentage())
                        .filters(variation.getFilters())
                        .build();

        /* if there is any edge with a different pivot (ie, condition is on a different field), in the inner layer, throw exception */
        validateConstraints(knot, edge);

        /* check for circular loops */
        checkForCycles(knot, edge);

        knot.getEdges().add(edge.getEdgeIdentifier());
        componentValidator.validate(knot);

        edgeStore.mapEdge(edge.getEdgeIdentifier().getId(), edge);
        knotStore.mapKnot(knot.getId(), knot);
        return edge;
    }

    @Override
    public Edge updateVariation(final String knotId, final String edgeId, final Variation variation) {
        Knot knot = knotStore.getKnot(knotId);
        Edge edge = edgeStore.getEdge(edgeId);
        checkNull(edgeId, edge);
        edge.setLive(variation.isLive());
        edge.setPercentage(variation.getPercentage());
        edge.setFilters(variation.getFilters());
        edge.getEdgeIdentifier().setPriority(variation.getPriority());
        if (!Strings.isNullOrEmpty(variation.getKnotId())) {
            Knot variationKnot = knotStore.getKnot(variation.getKnotId());
            componentValidator.validate(knot, variationKnot);
            edge.setKnotId(variation.getKnotId());
        }
        componentValidator.validate(edge);

        /* if there is any edge with a different pivot (ie, condition is on a different field), in the inner layer, throw exception */
        validateConstraints(knot, edge);

        /* check for circular loops */
        checkForCycles(knot, edge);

        return edgeStore.mapEdge(edgeId, edge.updateVersion());
    }

    @Override
    public boolean unlinkVariation(String knotId, String edgeId) {
        Knot knot = knotStore.getKnot(knotId);
        knot.setEdges(knot.getEdges().stream().filter(eid -> !eid.getId().equals(edgeId))
                          .collect(Collectors.toCollection(OrderedList::new)));

        componentValidator.validate(knot);

        knotStore.mapKnot(knotId, knot);
        edgeStore.deleteEdge(edgeId);
        return true;
    }

    @Override
    public Edge createEdge(Edge edge) {
        return edgeStore.mapEdge(edge.getEdgeIdentifier().getId(), edge);
    }

    @Override
    public TreeEdge deleteVariation(String knotId, String edgeId, boolean recursive) {
        Edge edge = edgeStore.getEdge(edgeId);
        checkNull(edgeId, edge);
        TreeEdge treeEdge = Converters.toTreeEdge(edge);
        if (recursive) {
            TreeKnot treeKnot = deleteKnot(edge.getKnotId(), true);
            treeEdge.setTreeKnot(treeKnot);
        }
        unlinkVariation(knotId, edgeId);
        return treeEdge;
    }

    @Override
    public Edge getEdge(String edgeId) {
        return edgeStore.getEdge(edgeId);
    }

    public Map<String, Edge> getAllEdges(List<String> edgeIds) {
        return edgeStore.getAllEdges(edgeIds);
    }

    @Override
    public boolean containsKey(String key) {
        return keyTreeStore.containsKey(key);
    }

    @Override
    public Knot createMapping(String key, String knotId) {
        Knot knot = knotStore.getKnot(knotId);
        if (knot == null) {
            throw new BonsaiError(BonsaiErrorCode.KNOT_ABSENT, "knotId:" + knotId + " is invalid");
        }
        String olderKnotId = keyTreeStore.getKeyTree(key);
        if (!Strings.isNullOrEmpty(olderKnotId) && knotId.equals(olderKnotId)) {
            return knotStore.getKnot(olderKnotId);
        }
        String olderMappedKey = keyTreeStore.createKeyTree(key, knotId);
        return knotStore.getKnot(olderMappedKey);
    }

    @Override
    public Knot createMapping(String key, KnotData knotData) {
        checkMappingExistence(key);
        Knot createdKnot = createKnot(knotData);
        createMapping(key, createdKnot.getId());
        return createdKnot;
    }

    @Override
    public String getMapping(String key) {
        return keyTreeStore.getKeyTree(key);
    }

    @Override
    public TreeKnot getCompleteTree(String key) {
        String knotId = keyTreeStore.getKeyTree(key);
        return composeTreeKnot(knotId);
    }

    @Override
    public TreeKnot getCompleteTreeWithDeltaOperations(final String key,
                                                       final List<DeltaOperation> deltaOperationList) {
        final String knotId = keyTreeStore.getKeyTree(key);
        TreeKnot treeKnot = composeTreeKnot(knotId);

        for (DeltaOperation deltaOperation : deltaOperationList) {
            treeKnot = deltaOperation.accept(treeKnot, treeKnotDeltaOperationModifier);
        }

        return treeKnot;
    }

    @Override
    public TreeKnot applyDeltaOperations(final String key,
                                         final List<DeltaOperation> deltaOperationList) {
        final String knotId = keyTreeStore.getKeyTree(key);
        TreeKnot treeKnot = composeTreeKnot(knotId);

        for (DeltaOperation deltaOperation : deltaOperationList) {
            treeKnot = deltaOperation.accept(treeKnot, treeKnotDeltaOperationModifier);
            deltaOperation.accept(saveDataOperationIntoStore);
        }

        return treeKnot;
    }

    @Override
    public Knot removeMapping(String key) {
        return knotStore.getKnot(keyTreeStore.removeKeyTree(key));
    }

    @Override
    public KeyNode evaluate(String key, C context) {
        componentValidator.validate(context);

        /* if the matching Knot is null, return empty */
        String id = keyTreeStore.getKeyTree(key);

        /* if key mapping doesn't contain the key, return an empty KeyNode */
        if (Strings.isNullOrEmpty(id)) {
            log.warn("[evaluate] no knotId mapping found for key:" + key);
            return KeyNode.empty(key);
        }

        List<Integer> edgePath = Lists.newArrayList();
        Knot knot = getMatchingKnot(key, knotStore.getKnot(id), context, edgePath);
        if (knot == null) {
            log.warn("[evaluate] knotId:" + id + " null for key:" + key);
            return KeyNode.empty(key, edgePath);
        }

        return knot.getKnotData().accept(new KnotDataVisitor<KeyNode>() {
            @Override
            public KeyNode visit(ValuedKnotData valuedKnotData) {
                return new KeyNode(key,
                                   ValueNode.builder()
                                            .id(knot.getId())
                                            .version(knot.getVersion())
                                            .value(valuedKnotData.getValue())
                                            .build(),
                                   edgePath);
            }

            @Override
            public KeyNode visit(MultiKnotData multiKnotData) {
                /* recursively evaluate the list of keys in MultiKnot */
                List<String> keys = multiKnotData.getKeys();
                List<KeyNode> nodes = keys != null
                        ? keys.stream()
                              .map(key -> evaluate(key, context))
                              .collect(Collectors.toList())
                        : null;
                return new KeyNode(key,
                                   ListNode.builder()
                                           .id(knot.getId())
                                           .version(knot.getVersion())
                                           .nodes(nodes)
                                           .build(),
                                   edgePath);
            }

            @Override
            public KeyNode visit(MapKnotData mapKnotData) {
                /* recursively evaluate the keys withing the MapKnot data */
                Map<String, String> mapKeys = mapKnotData.getMapKeys();
                Map<String, KeyNode> nodeMap = mapKeys != null
                        ? mapKeys.entrySet()
                                 .stream()
                                 .collect(Collectors.toMap(Map.Entry::getKey,
                                                           entry -> evaluate(entry.getValue(), context)))
                        : null;
                return new KeyNode(key,
                                   MapNode.builder()
                                          .id(knot.getId())
                                          .version(knot.getVersion())
                                          .nodeMap(nodeMap)
                                          .build(),
                                   edgePath);
            }
        });
    }

    @Override
    public FlatTreeRepresentation evaluateFlat(String key, C context) {
        KeyNode evaluate = evaluate(key, context);
        return TreeUtils.flatten(evaluate);
    }

    /**
     * throw an exception if there is a cycle detected in the Bonsai
     *
     * @param knot knot from where cycle needs to be detected
     * @param edge edge along which cycle is detected
     * @throws BonsaiError throws {@link BonsaiErrorCode#CYCLE_DETECTED}
     */
    private void checkForCycles(Knot knot, Edge edge) {
        if (knot == null || edge == null || Strings.isNullOrEmpty(edge.getKnotId())) {
            return;
        }
        CycleIdentifier<Knot> cycleIdentifier = new CycleIdentifier<>();
        cycleIdentifier.add(knot);
        checkForCycles(knotStore.getKnot(edge.getKnotId()), cycleIdentifier);
    }

    /**
     * recursively check for cycles in the tree
     *
     * @param knot            current knot
     * @param cycleIdentifier ds to identify cycles among {@link Knot}s
     * @throws BonsaiError error incase cycles are detected {@link BonsaiErrorCode#CYCLE_DETECTED}
     */
    private void checkForCycles(Knot knot, CycleIdentifier<Knot> cycleIdentifier) {
        if (knot == null) {
            return;
        }
        /* get the knot again from the mapping, else the reference of the incoming Knot from the Edge, will not be complete */
        Knot knotFromStore = knotStore.getKnot(knot.getId());
        if (knotFromStore == null) {
            return;
        }
        cycleIdentifier.add(knotFromStore);
        if (knotFromStore.getEdges() != null) {
            for (EdgeIdentifier edgeIdentifier : knotFromStore.getEdges()) {
                Edge mEdge = edgeStore.getEdge(edgeIdentifier.getId());
                if (mEdge != null && Strings.isNullOrEmpty(mEdge.getKnotId())) {
                    Knot edgeKnot = knotStore.getKnot(mEdge.getKnotId());
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
                             .map(keyTreeStore::getKeyTree)
                             .map(knotStore::getKnot)
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
                           .map(keyTreeStore::getKeyTree)
                           .map(knotStore::getKnot)
                           .forEach(knot -> checkForCycles(knot, cycleIdentifier));
                return null;
            }
        });
    }


    /**
     * this method recursively traverses the keyMapping, along all the matching / condition-satisfying edges
     * until the point where no edges are present or no satisfying edges are present
     *
     * @param key
     * @param knot    current node being traversed
     * @param context current context
     * @return node after traversal
     */
    private Knot getMatchingKnot(String key, final Knot knot, final C context, List<Integer> path) {
        if (knot == null) {
            return null;
        }
        OrderedList<EdgeIdentifier> edgeIdentifiers = knot.getEdges();
        if (edgeIdentifiers == null || edgeIdentifiers.isEmpty()) {
            /* base condition for the recursion */
            return knotPreferenceCheck(key, knot, context);
        }
        List<Edge> edges = new ArrayList<>(edgeStore.getAllEdges(edgeIdentifiers.stream()
                                                                                .map(EdgeIdentifier::getId)
                                                                                .collect(Collectors.toList()))
                                                    .values());
        Optional<Edge> conditionSatisfyingEdge = variationSelectorEngine.match(context, edges);
        if (!conditionSatisfyingEdge.isPresent()) {
            /* base condition for the recursion */
            return knotPreferenceCheck(key, knot, context);
        }
        /* recursively iterate over the edges knot */
        Edge edge = conditionSatisfyingEdge.get();
        Knot iknot = knotStore.getKnot(edge.getKnotId());
        path.add(edge.getEdgeIdentifier().getNumber());
        Knot matchingNode = getMatchingKnot(key, iknot, context, path);
        if (matchingNode == null) {
            return knotPreferenceCheck(key, knot, context);
        }
        return knotPreferenceCheck(key, matchingNode, context);
    }

    /**
     * checks if {@link Context#getPreferences()} already has the key and {@link Knot},
     * If so, uses the {@link ConflictResolver} to choose one of the two Knots
     *
     * @param key     key being evaluated
     * @param knot    knot that matched
     * @param context context containing preferences
     * @return either preference Knot of the Knot sent in
     */
    private Knot knotPreferenceCheck(String key, Knot knot, C context) {
        /* if context preferences already contains the key, return it */
        if (context.getPreferences() != null && context.getPreferences().containsKey(key)) {
            Knot preferenceKnot = context.getPreferences().get(key);
            return knotConflictResolver.resolveConflict(preferenceKnot, knot);
        }
        return knot;
    }

    private void checkMappingExistence(String key) {
        final String existingMapping = getMapping(key);
        if (existingMapping != null) {
            throw new BonsaiError(BonsaiErrorCode.MAPPING_ALREADY_PRESENT, "knotId:" + existingMapping
                    + " is already mapping with key:" + key + ". Consider updating the same");
        }
    }

    private TreeKnot composeTreeKnot(String knotId) {
        if (Strings.isNullOrEmpty(knotId)) {
            return null;
        }
        Knot knot = knotStore.getKnot(knotId);

        if (knot == null) {
            return TreeKnot.builder().id(knotId).build();
        }

        TreeKnot.TreeKnotBuilder profoundKnotBuilder
                = TreeKnot.builder()
                          .id(knot.getId())
                          .version(knot.getVersion())
                          .knotData(knot.getKnotData());

        if (knot.getEdges() != null) {
            List<TreeEdge> treeEdges
                    = edgeStore.getAllEdges(knot.getEdges().stream()
                                                .map(EdgeIdentifier::getId)
                                                .collect(Collectors.toList()))
                               .values()
                               .stream()
                               .map(edge -> TreeEdge.builder()
                                                    .edgeIdentifier(edge.getEdgeIdentifier())
                                                    .live(edge.isLive())
                                                    .percentage(edge.getPercentage())
                                                    .filters(edge.getFilters())
                                                    .version(edge.getVersion())
                                                    .treeKnot(composeTreeKnot(edge.getKnotId()))
                                                    .build())
                               .collect(Collectors.toList());
            profoundKnotBuilder.treeEdges(treeEdges);
        }
        return profoundKnotBuilder.build();
    }

    private void validateConstraints(Knot knot, Edge edge) {
        if (bonsaiProperties.isMutualExclusivitySettingTurnedOn()) {
            Map<String, Edge> allEdges = edgeStore.getAllEdges(
                    knot.getEdges()
                        .stream()
                        .map(EdgeIdentifier::getId)
                        .filter(e -> !edge.getEdgeIdentifier()
                                          .getId()
                                          .equals(e))// all edges that arent the current edge being added
                        .collect(Collectors.toList()));
            Set<String> existingEdgeFields = allEdges.values()
                                                     .stream()
                                                     .flatMap(k -> k.getFilters()
                                                                    .stream()
                                                                    .map(filter -> filter.accept(FIELD_IDENTIFIER))
                                                                    .reduce(Stream::concat)
                                                                    .orElse(Stream.empty()))
                                                     .collect(Collectors.toSet());
            if (existingEdgeFields.size() > 1) {
                throw new BonsaiError(BonsaiErrorCode.INVALID_STATE,
                                      String.format("mutualExclusivitySettingTurnedOn but multiple fields exist for knot:%s fields:%s",
                                                    knot.getId(), existingEdgeFields));
            }
            if (!existingEdgeFields.isEmpty() && anyFieldNotInExistingEdgeFields(edge, existingEdgeFields)) {

                Set<String> edgeFields = edge.getFilters()
                                             .stream()
                                             /* if any edge's filter's field, isn't part of the existing set, throw exception */
                                             .flatMap(filter -> filter.accept(FIELD_IDENTIFIER))
                                             .collect(Collectors.toSet());
                throw new BonsaiError(BonsaiErrorCode.VARIATION_MUTUAL_EXCLUSIVITY_CONSTRAINT_ERROR,
                                      "existing:" + existingEdgeFields + " new:" + edgeFields);
            }
        }
    }

    private void checkNull(String edgeId, Edge edge) {
        if (edge == null) {
            throw new BonsaiError(BonsaiErrorCode.EDGE_ABSENT, "No edge found for edgeId:" + edgeId);
        }
    }

    private boolean anyFieldNotInExistingEdgeFields(Edge edge, Set<String> allFields) {
        return edge.getFilters()
                   .stream()
                   /* if any edge's filter's field, isn't part of the existing set, throw exception */
                   .anyMatch(filter -> !allFields.containsAll(
                           filter.accept(FIELD_IDENTIFIER).collect(Collectors.toSet())));
    }
}
