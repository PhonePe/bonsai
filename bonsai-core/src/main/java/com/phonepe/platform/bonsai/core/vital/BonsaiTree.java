package com.phonepe.platform.bonsai.core.vital;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.models.BonsaiConstants;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.core.visitor.delta.impl.TreeKnotStateDeltaOperationModifierVisitor;
import com.phonepe.platform.bonsai.core.vital.provided.EdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KeyTreeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KnotStore;
import com.phonepe.platform.bonsai.core.vital.provided.Stores;
import com.phonepe.platform.bonsai.core.vital.provided.VariationSelectorEngine;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.ListNode;
import com.phonepe.platform.bonsai.models.MapNode;
import com.phonepe.platform.bonsai.models.TreeKnotState;
import com.phonepe.platform.bonsai.models.ValueNode;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.blocks.delta.DeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KeyMappingDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.visitor.DeltaOperationVisitor;
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
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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
    private final DeltaOperationVisitor<TreeKnotState> treeKnotDeltaOperationModifier;

    public BonsaiTree(final Stores<String, String, Knot, Edge> stores,
                      final VariationSelectorEngine<C> variationSelectorEngine,
                      final ComponentBonsaiTreeValidator componentValidator,
                      final BonsaiProperties bonsaiProperties,
                      final BonsaiIdGenerator bonsaiIdGenerator) {
        this.keyTreeStore = stores.getKeyTreeStore();
        this.knotStore = stores.getKnotStore();
        this.edgeStore = stores.getEdgeStore();
        this.variationSelectorEngine = variationSelectorEngine;
        this.componentValidator = componentValidator;
        this.bonsaiProperties = bonsaiProperties;
        this.bonsaiIdGenerator = bonsaiIdGenerator;
        this.treeKnotDeltaOperationModifier = new TreeKnotStateDeltaOperationModifierVisitor(
                componentValidator, knotStore, edgeStore
        );
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
    public Knot createKnot(final KnotData knotData, Map<String, Object> properties) {
        if (properties == null) {
            properties = new HashMap<>();
        }

        final Knot knot = Knot.builder()
                              .id(bonsaiIdGenerator.newKnotId())
                              .knotData(knotData)
                              .properties(properties)
                              .version(System.currentTimeMillis())
                              .build();

        componentValidator.validate(knot);
        validateKnotData(knot);

        knotStore.mapKnot(knot.getId(), knot);

        return knot;
    }

    @Override
    public Knot getKnot(String knotId) {
        return knotStore.getKnot(knotId);
    }

    @Override
    public Knot updateKnotData(String knotId, KnotData knotData, Map<String, Object> properties) {
        Knot knot = knotStore.getKnot(knotId);
        knot.setKnotData(knotData);

        /* Setting the properties, only if it non-null. otherwise, let the older copy stay. */
        if (properties != null) {
            knot.setProperties(properties);
        }

        /* Validate the correctness and check for cycle. */
        componentValidator.validate(knot);
        validateKnotData(knot);
        checkForCyclesTraversingKnotData(knot, knotId);

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
                .properties(variation.getProperties())
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

        /* Set the properties, only if it non-null. otherwise, let the older copy stay. */
        if (variation.getProperties() != null) {
            edge.setProperties(variation.getProperties());
        }

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
    public void unlinkVariation(String knotId, String edgeId) {
        Knot knot = knotStore.getKnot(knotId);
        knot.setEdges(knot.getEdges().stream().filter(eid -> !eid.getId().equals(edgeId))
                .collect(Collectors.toCollection(OrderedList::new)));

        componentValidator.validate(knot);

        knotStore.mapKnot(knotId, knot);
        edgeStore.deleteEdge(edgeId);
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
    public Knot createMapping(String key, KnotData knotData, Map<String, Object> properties) {
        checkMappingExistence(key);
        Knot createdKnot = createKnot(knotData, properties);
        createMapping(key, createdKnot.getId());
        return createdKnot;
    }

    @Override
    public String getMapping(String key) {
        return keyTreeStore.getKeyTree(key);
    }

    @Override
    public Knot removeMapping(String key) {
        return knotStore.getKnot(keyTreeStore.removeKeyTree(key));
    }

    @Override
    public TreeKnot getCompleteTree(String key) {
        String knotId = keyTreeStore.getKeyTree(key);
        return composeTreeKnot(knotId);
    }

    @Override
    public Knot createCompleteTree(final TreeKnot treeKnot) {
        if (treeKnot == null) {
            return null;
        }
        final Knot knot = createOrUpdateKnotFromTreeKnot(treeKnot);

        final Set<String> extraEdgeIds = knot.getEdges() != null ?
                knot.getEdges().stream().map(EdgeIdentifier::getId).collect(Collectors.toSet()) :
                new HashSet<>();

        final String parentKnotId = knot.getId();

        final List<TreeEdge> treeEdges = (treeKnot.getTreeEdges() == null)
                ? new ArrayList<>()
                : treeKnot.getTreeEdges();
        treeKnot.setTreeEdges(treeEdges);
        for (TreeEdge childTreeEdge : treeKnot.getTreeEdges()) {
            /* remove edges which are also part of the treeknot being saved (they are not extra) */
            extraEdgeIds.remove(childTreeEdge.getEdgeIdentifier().getId());

            /* first create the inner knots completely ----> POST-ORDER recursion */
            final Knot childKnot = createCompleteTree(childTreeEdge.getTreeKnot());

            /* then create/update the edges if necessary, only if  */
            if (childKnot != null) {
                createOrUpdateEdgeFromTreeEdge(parentKnotId, childTreeEdge, childKnot);
            }
        }
        extraEdgeIds.forEach(edgeId -> deleteVariation(parentKnotId, edgeId, true));
        return knot;
    }

    @Override
    public TreeKnotState getCompleteTreeWithDeltaOperations(final String key,
                                                            final List<DeltaOperation> deltaOperationList) {
        final String knotId = keyTreeStore.getKeyTree(key);
        final TreeKnot initialTreeKnot = composeTreeKnot(knotId);
        TreeKnotState metaData = TreeKnotState.builder()
                .treeKnot(initialTreeKnot)
                .build();
        for (DeltaOperation deltaOperation : deltaOperationList) {
            metaData = deltaOperation.accept(metaData, treeKnotDeltaOperationModifier);
        }

        final TreeKnot modifiedTreeKnot = metaData.getTreeKnot();
        componentValidator.validate(modifiedTreeKnot);
        return metaData;
    }

    @Override
    public TreeKnotState applyDeltaOperations(final String key, final List<DeltaOperation> deltaOperationList) {
        final TreeKnotState metaData = getCompleteTreeWithDeltaOperations(key, deltaOperationList);
        Knot rootKnot = createCompleteTree(metaData.getTreeKnot());
        if (!containsKey(key)) {
            KeyMappingDeltaOperation keyMappingDeltaOperation = (KeyMappingDeltaOperation) deltaOperationList.get(0);
            createMapping(keyMappingDeltaOperation.getKeyId(), rootKnot.getId());
        }
        return metaData;
    }

    @Override
    public KeyNode evaluate(String key, C context) {
        boolean isMDCContextSet = setMDCContext();

        try {
            componentValidator.validate(context);

            /* if the matching Knot is null, return empty */
            final String id = keyTreeStore.getKeyTree(key);

            /* if key mapping doesn't contain the key, return an empty KeyNode */
            if (Strings.isNullOrEmpty(id)) {
                log.warn("[bonsai][evaluate][{}] no knotId mapping found", key);
                return KeyNode.empty(key);
            }

            final List<Integer> edgePath = Lists.newArrayList();
            final List<Edge> edges = Lists.newArrayList();
            if (context.getPreferences() != null && context.getPreferences().containsKey(key)) {
                return getKeyNode(key, context, edgePath, edges, context.getPreferences().get(key));
            }
            final Knot knot = getMatchingKnot(key, knotStore.getKnot(id), context, edgePath, edges);
            if (knot == null) {
                log.warn("[bonsai][evaluate][{}] knotId:{} is null", key, id);
                return KeyNode.empty(key, edgePath);
            }

            return getKeyNode(key, context, edgePath, edges, knot);
        } finally {
            if (isMDCContextSet) {
                removeMDCContext();
            }
        }
    }

    @Override
    public FlatTreeRepresentation evaluateFlat(String key, C context) {
        KeyNode evaluate = evaluate(key, context);
        return TreeUtils.flatten(evaluate);
    }

    private KeyNode getKeyNode(final String key, final C context,
                               final List<Integer> edgePath, final List<Edge> edges,
                               final Knot knot) {
        return knot.getKnotData().accept(new KnotDataVisitor<KeyNode>() {
            @Override
            public KeyNode visit(final ValuedKnotData valuedKnotData) {
                return new KeyNode(key,
                                   ValueNode.builder()
                                           .id(knot.getId())
                                           .version(knot.getVersion())
                                           .value(valuedKnotData.getValue())
                                           .build(),
                                   edgePath,
                                   edges);
            }

            @Override
            public KeyNode visit(final MultiKnotData multiKnotData) {
                /* recursively evaluate the list of keys in MultiKnot */
                final List<String> keys = multiKnotData.getKeys();
                final List<KeyNode> nodes = keys != null
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
                                   edgePath,
                                   edges);
            }

            @Override
            public KeyNode visit(final MapKnotData mapKnotData) {
                /* recursively evaluate the keys withing the MapKnot data */
                final Map<String, String> mapKeys = mapKnotData.getMapKeys();
                final Map<String, KeyNode> nodeMap = mapKeys != null
                                                     ? mapKeys.entrySet()
                                                             .stream()
                                                             .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                       entry -> evaluate(
                                                                                               entry.getValue(),
                                                                                               context)))
                                                     : null;
                return new KeyNode(key,
                                   MapNode.builder()
                                           .id(knot.getId())
                                           .version(knot.getVersion())
                                           .nodeMap(nodeMap)
                                           .build(),
                                   edgePath,
                                   edges);
            }
        });
    }

    private boolean setMDCContext() {
        try {
            String requestId = MDC.get(BonsaiConstants.EVALUATION_ID);
            if (requestId == null) {
                requestId = UUID.randomUUID().toString();
                MDC.put(BonsaiConstants.EVALUATION_ID, requestId);
                return true;
            }
        } catch (Exception e) {
            log.error("Failed to set MDC context", e);
        }
        return false;
    }

    private void removeMDCContext() {
        MDC.remove(BonsaiConstants.EVALUATION_ID);
    }

    private Knot createOrUpdateKnotFromTreeKnot(TreeKnot treeKnot) {
        Knot knot = getKnot(treeKnot.getId());
        if (knot != null) {
            if (treeKnot.getVersion() != knot.getVersion()) {
                knot = updateKnotData(treeKnot.getId(), treeKnot.getKnotData(), treeKnot.getProperties());
            }
        } else {
            knot = createKnot(treeKnot.getKnotData(), treeKnot.getProperties());
        }
        return knot;
    }

    private void createOrUpdateEdgeFromTreeEdge(final String parentKnotId, final TreeEdge treeEdge,
                                                final Knot treeCreatedKnot) {
        final Edge edge = getEdge(treeEdge.getEdgeIdentifier().getId());
        if (edge != null && treeEdge.getVersion() == edge.getVersion()) {
            return;
        }
        final String childKnotId = treeCreatedKnot.getId();
        if (edge != null) {
            updateVariation(parentKnotId, treeEdge.getEdgeIdentifier().getId(),
                    Converters.toVariation(childKnotId, treeEdge));
        } else {
            addVariation(parentKnotId, Converters.toVariation(childKnotId, treeEdge));
        }
    }

    /**
     * Function to validate no non-existing keys are entering into our system.
     *
     * @param knot - knot which is to be validated.
     */
    private void validateKnotData(final Knot knot) {
        knot.getKnotData().accept(new KnotDataVisitor<Void>() {
            @Override
            public Void visit(final ValuedKnotData valuedKnotData) {
                return null;
            }

            @Override
            public Void visit(final MultiKnotData multiKnotData) {
                validateKnotData(multiKnotData.getKeys());
                return null;
            }

            @Override
            public Void visit(final MapKnotData mapKnotData) {
                final List<String> keyList = new ArrayList<>(mapKnotData.getMapKeys().values());
                validateKnotData(keyList);
                return null;
            }
        });
    }

    private void validateKnotData(final List<String> keyList) {
        keyList.forEach(key -> {
            final String internalKnotId = keyTreeStore.getKeyTree(key);
            if (internalKnotId == null || internalKnotId.isEmpty()) {
                throw new BonsaiError(BonsaiErrorCode.KNOT_ABSENT,
                        "%s is not present in our ecosystem".formatted(key));
            }

            final Knot internalKnot = knotStore.getKnot(internalKnotId);
            if (internalKnot == null) {
                throw new BonsaiError(BonsaiErrorCode.KNOT_ABSENT,
                        "%s is not present in our ecosystem".formatted(key));
            }
        });
    }

    /**
     * Throw an exception if there is a cycle detected in the Bonsai
     *
     * @param knot knot from where cycle needs to be detected
     * @param edge edge along which cycle is detected
     * @throws BonsaiError throws {@link BonsaiErrorCode#CYCLE_DETECTED}
     */
    private void checkForCycles(final Knot knot, final Edge edge) {
        if (knot == null) {
            return;
        }

        if (edge != null && !Strings.isNullOrEmpty(edge.getKnotId())) {
            final Knot directVariationKnot = knotStore.getKnot(edge.getKnotId());
            checkForCycles(directVariationKnot, knot.getId());
        }
    }

    /**
     * Recursively check for cycles in the tree.
     *
     * @param knot          - current knot
     * @param initialKnotId - identifier of initial Knot
     * @throws BonsaiError - error in-case cycles are detected {@link BonsaiErrorCode#CYCLE_DETECTED}
     */
    private void checkForCycles(final Knot knot, final String initialKnotId) {
        if (knot == null) {
            return;
        } else if (knot.getId().equals(initialKnotId)) {
            throw new BonsaiError(BonsaiErrorCode.CYCLE_DETECTED, "Cycle identified at knotId:" + knot.getId());
        }

        if (knot.getEdges() != null) {
            for (EdgeIdentifier edgeIdentifier : knot.getEdges()) {
                Edge mEdge = edgeStore.getEdge(edgeIdentifier.getId());
                if (mEdge != null && !Strings.isNullOrEmpty(mEdge.getKnotId())) {
                    Knot edgeKnot = knotStore.getKnot(mEdge.getKnotId());
                    checkForCycles(edgeKnot, initialKnotId);
                }
            }
        }

        checkForCyclesTraversingKnotData(knot, initialKnotId);
    }

    /**
     * Function to traverse inside the data of knot's to find anomaly.
     *
     * @param knot          - current knot.
     * @param initialKnotId - identifier of initial knot.
     */
    private void checkForCyclesTraversingKnotData(final Knot knot, final String initialKnotId) {
        knot.getKnotData().accept(new KnotDataVisitor<Void>() {
            @Override
            public Void visit(final ValuedKnotData valuedKnotData) {
                return null;
            }

            @Override
            public Void visit(final MultiKnotData multiKnotData) {
                multiKnotData.getKeys().stream()
                             .map(keyTreeStore::getKeyTree)
                             .map(knotStore::getKnot)
                             .filter(Objects::nonNull)
                             .forEach(internalKnot -> checkForCycles(internalKnot, initialKnotId));
                return null;
            }

            @Override
            public Void visit(final MapKnotData mapKnotData) {
                mapKnotData.getMapKeys()
                           .values()
                           .stream()
                           .filter(Objects::nonNull)
                           .map(keyTreeStore::getKeyTree)
                           .map(knotStore::getKnot)
                           .filter(Objects::nonNull)
                           .forEach(internalKnot -> checkForCycles(internalKnot, initialKnotId));
                return null;
            }
        });
    }

    /**
     * this method recursively traverses the keyMapping, along all the matching / condition-satisfying edges
     * until the point where no edges are present or no satisfying edges are present
     *
     * @param key     key being evaluated (will later be used for preference check)
     * @param knot    current node being traversed
     * @param context current context
     * @return node after traversal
     */
    private Knot getMatchingKnot(final String key, final Knot knot, final C context, final List<Integer> path, final List<Edge> edgeList) {
        if (knot == null) {
            if (log.isDebugEnabled()) {
                log.debug("[bonsai][getMatchingKnot][{}][{}] no knot", context.id(), key);
            }
            return null;
        }
        OrderedList<EdgeIdentifier> edgeIdentifiers = knot.getEdges();
        if (edgeIdentifiers == null || edgeIdentifiers.isEmpty()) {
            /* base condition for the recursion */
            if (log.isDebugEnabled()) {
                log.debug("[bonsai][getMatchingKnot][{}][{}] no edges", context.id(), key);
            }
            return knot;
        }
        List<Edge> edges = new ArrayList<>(edgeStore.getAllEdges(edgeIdentifiers.stream()
                        .map(EdgeIdentifier::getId)
                        .collect(Collectors.toList()))
                .values());
        Optional<Edge> conditionSatisfyingEdge = variationSelectorEngine.match(context, edges);
        if (conditionSatisfyingEdge.isEmpty()) {
            /* base condition for the recursion */
            if (log.isDebugEnabled()) {
                log.debug("[bonsai][getMatchingKnot][{}][{}] no edge satisfies conditions", context.id(), key);
            }
            return knot;
        }
        /* recursively iterate over the matching edge knot on the RHS (knot --edge--> rhsKnot) */
        Edge edge = conditionSatisfyingEdge.get();
        Knot rhsKnot = knotStore.getKnot(edge.getKnotId());
        path.add(edge.getEdgeIdentifier().getNumber());
        if (log.isDebugEnabled()) {
            log.debug("[bonsai][getMatchingKnot][{}][{}] edge condition satisfied: {}, knot: {} ", context.id(), key,
                    edge.getEdgeIdentifier().getId(), rhsKnot.getId());
        }
        if (Objects.isNull(edge.getProperties())) {
            edge.setProperties(Maps.newHashMap());
        }
        edge.getProperties().put("key", key);
        edgeList.add(edge);
        /* recursion happening here */
        Knot matchingNode = getMatchingKnot(key, rhsKnot, context, path, edgeList);
        if (matchingNode == null) {
            /* no more matching knots on the RHS, use previous knot */
            return knot;
        }
        return matchingNode;
    }

    private void checkMappingExistence(final String key) {
        final String existingMapping = getMapping(key);
        if (existingMapping != null) {
            throw new BonsaiError(BonsaiErrorCode.MAPPING_ALREADY_PRESENT, "knotId:" + existingMapping
                    + " is already mapping with key:" + key + ". Consider updating the same");
        }
    }

    private TreeKnot composeTreeKnot(final String knotId) {
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
                .knotData(knot.getKnotData())
                .properties(knot.getProperties());

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
                            .properties(edge.getProperties())
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
                        "mutualExclusivitySettingTurnedOn but multiple fields exist for knot:%s fields:%s".formatted(
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
