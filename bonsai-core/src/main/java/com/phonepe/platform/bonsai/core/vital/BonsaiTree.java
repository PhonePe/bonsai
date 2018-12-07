package com.phonepe.platform.bonsai.core.vital;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.data.*;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.core.structures.CycleIdentifier;
import com.phonepe.platform.bonsai.core.structures.OrderedList;
import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import com.phonepe.platform.bonsai.core.vital.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.core.vital.blocks.Variation;
import com.phonepe.platform.bonsai.core.vital.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.core.vital.blocks.model.TreeKnot;
import com.phonepe.platform.bonsai.core.vital.provided.EdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KeyTreeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KnotStore;
import com.phonepe.platform.bonsai.core.vital.provided.VariationSelectorEngine;
import com.phonepe.platform.bonsai.json.eval.JsonPathSetup;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.ListNode;
import com.phonepe.platform.bonsai.models.MapNode;
import com.phonepe.platform.bonsai.models.ValueNode;
import com.phonepe.platform.query.dsl.Filter;
import com.phonepe.platform.query.dsl.FilterFieldIdentifier;

import java.util.*;
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
public class BonsaiTree<C extends Context> implements Bonsai<C> {

    private KeyTreeStore<String, String> keyTreeStore;
    private KnotStore<String, Knot> knotStore;
    private EdgeStore<String, Edge> edgeStore;
    private VariationSelectorEngine<C> variationSelectorEngine;
    private ComponentValidator componentValidator;
    private BonsaiProperties bonsaiProperties;
    private BonsaiIdGenerator bonsaiIdGenerator;

    public BonsaiTree(KeyTreeStore<String, String> keyTreeStore,
                      KnotStore<String, Knot> knotStore,
                      EdgeStore<String, Edge> edgeStore,
                      VariationSelectorEngine<C> variationSelectorEngine,
                      ComponentValidator componentValidator, BonsaiProperties bonsaiProperties,
                      BonsaiIdGenerator bonsaiIdGenerator) {
        this.keyTreeStore = keyTreeStore;
        this.knotStore = knotStore;
        this.edgeStore = edgeStore;
        this.variationSelectorEngine = variationSelectorEngine;
        this.componentValidator = componentValidator;
        this.bonsaiProperties = bonsaiProperties;
        this.bonsaiIdGenerator = bonsaiIdGenerator;
        JsonPathSetup.setup();
    }

    @Override
    public Knot createKnot(Knot knot) {
        return knotStore.mapKnot(knot.getId(), knot);
    }

    @Override
    public Knot createKnot(KnotData knotData) {
        componentValidator.validate(knotData);
        Knot knot = Knot.builder()
                        .id(bonsaiIdGenerator.newKnotId())
                        .knotData(knotData)
                        .version(System.currentTimeMillis())
                        .build();
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
        return knotStore.mapKnot(knotId, knot.updateVersion());
    }

    public List<Knot> deleteKnot(String id, boolean recursive) {
        List<Knot> deletedKnots = Lists.newArrayList();
        if (recursive) {
            /* this is a recursive delete operation */
            Knot knot = knotStore.getKnot(id);
            if (knot.getEdges() != null) {
                edgeStore.getAllEdges(knot.getEdges().stream().map(EdgeIdentifier::getId).collect(Collectors.toList()))
                         .values().stream().map(Edge::getKnotId)
                         .map(knotId -> deleteKnot(knotId, true))
                         .forEach(deletedKnots::addAll);
            }

        }
        deletedKnots.add(knotStore.deleteKnot(id));
        return deletedKnots;
    }

    @Override
    public String addVariation(String knotId, Variation variation) {
        componentValidator.validate(variation);
        if (!knotStore.containsKnot(knotId)) {
            return null; //todo change this
        }
        Knot knot = knotStore.getKnot(knotId);
        if (knot.getEdges() == null) {
            knot.setEdges(new OrderedList<>());
        }

        Edge edge = Edge.builder()
                        .edgeIdentifier(new EdgeIdentifier(bonsaiIdGenerator.newEdgeId(), variation.getPriority()))
                        .knotId(variation.getKnotId())
                        .version(System.currentTimeMillis())
                        .filters(variation.getFilters())
                        .build();

        /* if there is any edge with a different pivot (ie, condition is on a different field), in the inner layer, throw exception */
        validateConstraints(knot, edge);

        /* check for circular loops */
        checkForCycles(knot, edge);

        edgeStore.mapEdge(edge.getEdgeIdentifier().getId(), edge);
        knot.getEdges().add(edge.getEdgeIdentifier());
        knotStore.mapKnot(knot.getId(), knot);
        return edge.getEdgeIdentifier().getId();
    }

    @Override
    public Edge updateEdgeFilters(String knotId, String edgeId, List<Filter> filters) {
        Knot knot = knotStore.getKnot(knotId);
        Edge edge = edgeStore.getEdge(edgeId);
        if (edge == null) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, "No edge found for edgeId:" + edgeId);
        }
        edge.setFilters(filters);

        /* if there is any edge with a different pivot (ie, condition is on a different field), in the inner layer, throw exception */
        validateConstraints(knot, edge);

        return edgeStore.mapEdge(edgeId, edge.updateVersion());
    }

    @Override
    public Edge addEdgeFilters(String edgeId, List<Filter> filters) {
        Edge edge = edgeStore.getEdge(edgeId);
        if (edge == null) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, "No edge found for edgeId:" + edgeId);
        }
        edge.getFilters().addAll(filters);
        componentValidator.validate(edge);
        return edgeStore.mapEdge(edgeId, edge);
    }

    @Override
    public boolean unlinkVariation(String knotId, String edgeId) {
        Knot knot = knotStore.getKnot(knotId);
        knot.setEdges(knot.getEdges().stream().filter(eid -> !eid.getId().equals(edgeId))
                          .collect(Collectors.toCollection(OrderedList::new)));
        knotStore.mapKnot(knotId, knot);
        return true;
    }

    @Override
    public Edge createEdge(Edge edge) {
        return edgeStore.mapEdge(edge.getEdgeIdentifier().getId(), edge);
    }

    @Override
    public List<Knot> deleteVariation(String knotId, String edgeId, boolean recursive) {
        //todo check this piece
        unlinkVariation(knotId, edgeId);
        Edge edge = edgeStore.getEdge(edgeId);
        List<Knot> knots = deleteKnot(edge.getKnotId(), recursive);
        edgeStore.deleteEdge(edgeId);
        return knots;
    }

    @Override
    public Edge getEdge(String edgeId) {
        return edgeStore.getEdge(edgeId);
    }

    public LinkedHashMap<String, Edge> getAllEdges(List<String> edgeIds) {
        return edgeStore.getAllEdges(edgeIds);
    }

    @Override
    public Knot createMapping(String key, String knotId) {
//        Knot knot = knotStore.getKnot(knotId);
//        if (knot == null) {
//            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, "knotId:" + knotId +  " is invalid");
//        }
        if (keyTreeStore.containsKey(key)) {
            return knotStore.getKnot(keyTreeStore.getKeyTree(key));
        }
        String olderMappedKey = keyTreeStore.createKeyTree(key, knotId);
        return knotStore.getKnot(olderMappedKey);
    }

    @Override
    public Knot createMapping(String key, KnotData knotData) {
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
    public Knot removeMapping(String key) {
        return knotStore.getKnot(keyTreeStore.removeKeyTree(key));
    }

    @Override
    public KeyNode evaluate(String key, C context) {
        componentValidator.validate(context);

        /* if context preferences already contains the key, return it */
        if (context.getPreferences() != null && context.getPreferences().containsKey(key)) {
            return context.getPreferences().get(key);
        }

        /* if the matching Knot is null, return empty */
        String id = keyTreeStore.getKeyTree(key);

        /* if key mapping doesn't contain the key, return an empty KeyNode */
        if (Strings.isNullOrEmpty(id)) {
            return KeyNode.empty(key);
        }

        Knot knot = getMatchingNode(knotStore.getKnot(id), context);
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
     * @param knot    current node being traversed
     * @param context current context
     * @return node after traversal
     */
    private Knot getMatchingNode(Knot knot, C context) {
        if (knot == null) {
            return null;
        }
        OrderedList<EdgeIdentifier> edgeIdentifiers = knot.getEdges();
        if (edgeIdentifiers == null || edgeIdentifiers.isEmpty()) {
            /* base condition for the recursion */
            return knot;
        }
        List<Edge> edges = new ArrayList<>(edgeStore.getAllEdges(edgeIdentifiers.stream()
                                                                                .map(EdgeIdentifier::getId)
                                                                                .collect(Collectors.toList()))
                                                    .values());
        Optional<Edge> conditionSatisfyingEdge = variationSelectorEngine.match(context, edges);
        if (!conditionSatisfyingEdge.isPresent()) {
            /* base condition for the recursion */
            return knot;
        }
        /* recursively iterate over the edges knot */
        Knot iknot = knotStore.getKnot(conditionSatisfyingEdge.get().getKnotId());
        Knot matchingNode = getMatchingNode(iknot, context);
        if (matchingNode == null) {
            return knot;
        }
        return matchingNode;
    }

    private TreeKnot composeTreeKnot(String knotId) {
        if (Strings.isNullOrEmpty(knotId)) {
            return null;
        }
        Knot knot = knotStore.getKnot(knotId);

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
            Set<String> allFields = edgeStore.getAllEdges(knot.getEdges()
                                                              .stream()
                                                              .map(EdgeIdentifier::getId)
                                                              .collect(Collectors.toList()))
                                             .values()
                                             .stream()
                                             .flatMap(k -> k.getFilters()
                                                            .stream()
                                                            .map(filter -> filter.accept(new FilterFieldIdentifier()))
                                                            .reduce(Stream::concat)
                                                            .orElse(Stream.empty()))
                                             .collect(Collectors.toSet());
            if (allFields.size() > 1) {
                throw new BonsaiError(BonsaiErrorCode.INVALID_STATE,
                                      String.format("mutualExclusivitySettingTurnedOn but multiple fields exist for knot:%s fields:%s",
                                                    knot.getId(), allFields));
            }
            if (!allFields.isEmpty() &&
                    edge.getFilters().stream().anyMatch(filter -> !allFields.contains(filter.getField()))) {
                throw new BonsaiError(BonsaiErrorCode.VARIATION_MUTUAL_EXCLUSIVITY_CONSTRAINT_ERROR);
            }
        }
    }
}
