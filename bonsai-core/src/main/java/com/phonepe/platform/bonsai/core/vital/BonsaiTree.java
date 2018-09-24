package com.phonepe.platform.bonsai.core.vital;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.data.*;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.core.structures.CycleIdentifier;
import com.phonepe.platform.bonsai.core.structures.OrderedList;
import com.phonepe.platform.bonsai.core.variation.filter.FilterFieldIdentifier;
import com.phonepe.platform.bonsai.core.variation.jsonpath.JsonPathSetup;
import com.phonepe.platform.bonsai.core.variation.filter.Filter;
import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import com.phonepe.platform.bonsai.core.vital.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.core.vital.blocks.Variation;
import com.phonepe.platform.bonsai.core.vital.provided.EdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KnotStore;
import com.phonepe.platform.bonsai.core.vital.provided.MappingStore;
import com.phonepe.platform.bonsai.core.vital.provided.VariationSelectorEngine;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.ListNode;
import com.phonepe.platform.bonsai.models.MapNode;
import com.phonepe.platform.bonsai.models.ValueNode;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class BonsaiTree<C extends Context> implements Bonsai<C> {

    private MappingStore<String, String> mappingStore;
    private KnotStore<String, Knot> knotStore;
    private EdgeStore<String, Edge> edgeStore;
    private VariationSelectorEngine<C> variationSelectorEngine;
    private ComponentValidator componentValidator;
    private BonsaiProperties bonsaiProperties;
    private BonsaiIdGenerator bonsaiIdGenerator;

    public BonsaiTree(MappingStore<String, String> mappingStore,
                      KnotStore<String, Knot> knotStore,
                      EdgeStore<String, Edge> edgeStore,
                      VariationSelectorEngine variationSelectorEngine,
                      ComponentValidator componentValidator, BonsaiProperties bonsaiProperties,
                      BonsaiIdGenerator bonsaiIdGenerator) {
        this.mappingStore = mappingStore;
        this.knotStore = knotStore;
        this.edgeStore = edgeStore;
        this.variationSelectorEngine = variationSelectorEngine;
        this.componentValidator = componentValidator;
        this.bonsaiProperties = bonsaiProperties;
        this.bonsaiIdGenerator = bonsaiIdGenerator;
        JsonPathSetup.setup();
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
        return knotStore.get(mappingStore.get(knotId));
    }

    @Override
    public boolean updateKnotData(String knotId, KnotData knotData) {
        Knot knot = knotStore.get(knotId);
        knot.setKnotData(knotData);
        return knotStore.mapKnot(knotId, knot.updateVersion());
    }

    public List<Knot> deleteKnot(String id, boolean recursive) {
        List<Knot> deletedKnots = Lists.newArrayList();
        if (recursive) {
            /* this is a recursive delete operation */
            Knot knot = knotStore.get(id);
            edgeStore.getAll(knot.getEdges().stream().map(EdgeIdentifier::getId).collect(Collectors.toList()))
                     .stream().map(Edge::getKnotId)
                     .map(knotId -> deleteKnot(knotId, true))
                     .forEach(deletedKnots::addAll);

        }
        deletedKnots.add(knotStore.delete(id));
        return deletedKnots;
    }

    @Override
    public String addVariation(String knotId, Variation variation) {
        componentValidator.validate(variation);
        if (!knotStore.containsKey(knotId)) {
            return null; //todo change this
        }
        Knot knot = knotStore.get(knotId);
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
    public boolean updateEdgeFilters(String knotId, String edgeId, List<Filter> filters) {
        Knot knot = knotStore.get(knotId);
        Edge edge = edgeStore.get(edgeId);
        if (edge == null) {
            return false;
        }
        edge.setFilters(filters);

        /* if there is any edge with a different pivot (ie, condition is on a different field), in the inner layer, throw exception */
        validateConstraints(knot, edge);

        edgeStore.mapEdge(edgeId, edge.updateVersion());
        return true;
    }


    @Override
    public boolean addEdgeFilters(String edgeId, List<Filter> filters) {
        Edge edge = edgeStore.get(edgeId);
        if (edge == null) {
            return false;
        }
        edge.getFilters().addAll(filters);
        componentValidator.validate(edge);
        edgeStore.mapEdge(edgeId, edge);
        return true;
    }

    @Override
    public boolean unlinkVariation(String knotId, String edgeId) {
        Knot knot = knotStore.get(knotId);
        knot.setEdges(knot.getEdges().stream().filter(eid -> !eid.getId().equals(edgeId))
                          .collect(Collectors.toCollection(OrderedList::new)));
        return knotStore.mapKnot(knotId, knot);
    }


    @Override
    public List<Knot> deleteVariation(String knotId, String edgeId, boolean recursive) {
        //todo check this piece
        unlinkVariation(knotId, edgeId);
        Edge edge = edgeStore.get(edgeId);
        List<Knot> knots = deleteKnot(edge.getKnotId(), recursive);
        edgeStore.delete(edgeId);
        return knots;
    }

    @Override
    public Edge getEdge(String edgeId) {
        return edgeStore.get(edgeId);
    }


    @Override
    public Knot createMapping(String key, String knotId) {
        Knot knot = knotStore.get(knotId);
        if (knot == null) {
            //todo
        }
        if (mappingStore.containsKey(key)) {
            return knotStore.get(mappingStore.get(key));
        }
        String olderMappedKey = mappingStore.map(key, knotId);
        return knotStore.get(olderMappedKey);
    }

    @Override
    public Knot createMapping(String key, KnotData knotData) {
        Knot createdKnot = createKnot(knotData);
        createMapping(key, createdKnot.getId());
        return createdKnot;
    }

    @Override
    public Knot removeMapping(String key) {
        return knotStore.get(mappingStore.remove(key));
    }

    @Override
    public KeyNode evaluate(String key, C context) {
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

        Knot knot = getMatchingNode(knotStore.get(id), context);
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
        checkForCycles(knotStore.get(edge.getKnotId()), cycleIdentifier);
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
        Knot knotFromStore = knotStore.get(knot.getId());
        if (knotFromStore == null) {
            return;
        }
        cycleIdentifier.add(knotFromStore);
        if (knotFromStore.getEdges() != null) {
            for (EdgeIdentifier edgeIdentifier : knotFromStore.getEdges()) {
                Edge mEdge = edgeStore.get(edgeIdentifier.getId());
                if (mEdge != null && Strings.isNullOrEmpty(mEdge.getKnotId())) {
                    Knot edgeKnot = knotStore.get(mEdge.getKnotId());
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
    private Knot getMatchingNode(Knot knot, C context) {
        if (knot == null) {
            return null;
        }
        OrderedList<EdgeIdentifier> edgeIdentifiers = knot.getEdges();
        if (edgeIdentifiers == null || edgeIdentifiers.isEmpty()) {
            /* base condition for the recursion */
            return knot;
        }
        List<Edge> edges = edgeStore.getAll(edgeIdentifiers.stream()
                                                           .map(EdgeIdentifier::getId)
                                                           .collect(Collectors.toList()));
        Optional<Edge> conditionSatisfyingEdge = variationSelectorEngine.match(context, edges);
        if (!conditionSatisfyingEdge.isPresent()) {
            /* base condition for the recursion */
            return knot;
        }
        /* recursively iterate over the edges knot */
        Knot iknot = knotStore.get(conditionSatisfyingEdge.get().getKnotId());
        Knot matchingNode = getMatchingNode(iknot, context);
        if (matchingNode == null) {
            return knot;
        }
        return matchingNode;
    }


    private void validateConstraints(Knot knot, Edge edge) {
        if (bonsaiProperties.isMutualExclusivitySettingTurnedOn()) {
            Set<String> allFields = edgeStore.getAll(knot.getEdges()
                                                         .stream()
                                                         .map(EdgeIdentifier::getId)
                                                         .collect(Collectors.toList()))
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
