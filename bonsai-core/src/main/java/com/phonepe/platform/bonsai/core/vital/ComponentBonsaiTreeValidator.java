package com.phonepe.platform.bonsai.core.vital;

import com.google.common.base.Strings;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.blocks.delta.EdgeDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KeyMappingDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KnotDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import com.phonepe.platform.bonsai.models.data.KnotData;
import com.phonepe.platform.bonsai.models.data.KnotDataVisitor;
import com.phonepe.platform.bonsai.models.data.MapKnotData;
import com.phonepe.platform.bonsai.models.data.MultiKnotData;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.bonsai.models.value.Value;
import com.phonepe.platform.query.dsl.Filter;
import com.phonepe.platform.query.dsl.FilterCounter;
import com.phonepe.platform.query.dsl.FilterFieldIdentifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is a final class on purpose
 * The outside user of this library is not supposed to override this
 */
public final class ComponentBonsaiTreeValidator implements BonsaiTreeValidator {
    private static final String ERROR_FIELD_STR = "field:%s cannot be null";
    private static final FilterFieldIdentifier FIELD_IDENTIFIER = new FilterFieldIdentifier();

    private BonsaiProperties bonsaiProperties;

    public ComponentBonsaiTreeValidator(BonsaiProperties bonsaiProperties) {
        this.bonsaiProperties = bonsaiProperties;
    }

    private static <T> void checkNotNull(T reference, String fieldName) {
        if (reference == null) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, String.format(ERROR_FIELD_STR, fieldName));
        }
    }

    private static void checkNotNullOrEmpty(String reference, String fieldName) {
        if (Strings.isNullOrEmpty(reference)) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, String.format(ERROR_FIELD_STR, fieldName));
        }
    }

    private static <T> void checkNotNullOrEmpty(Collection<T> reference, String fieldName) {
        if (reference == null || reference.isEmpty()) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, String.format(ERROR_FIELD_STR, fieldName));
        }
    }

    private static void checkCondition(boolean condition, String errorReason) {
        if (!condition) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, errorReason);
        }
    }

    @Override
    public void validate(Knot knot) {
        checkNotNull(knot, "knot");
        checkNotNullOrEmpty(knot.getId(), "knot.id");
        checkNotNull(knot.getKnotData(), "knot.knotData");
        checkNotNull(knot.getKnotData().getKnotDataType(), "knot.knotData.knotDataType");
        validate(knot.getKnotData());
        checkCondition(knot.getVersion() >= 0, "knot.version cannot be less than 0");
        if (knot.getEdges() != null && knot.getEdges().size() > bonsaiProperties.getMaxAllowedVariationsPerKnot()) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, "variations exceed max allowed:" + bonsaiProperties.getMaxAllowedVariationsPerKnot());
        }
    }

    @Override
    public void validate(Knot existingKnot, Knot newKnot) {
        validate(existingKnot.getKnotData());
        validate(newKnot.getKnotData());
        if (!TreeUtils.isKnotDataOfSimilarType(existingKnot, newKnot)) {
            throw new BonsaiError(BonsaiErrorCode.KNOT_RESOLUTION_ERROR,
                    String.format("knotData class mismatch rootKnot:%s variationKnot:%s",
                            existingKnot.getKnotData().getClass(),
                            newKnot.getKnotData().getClass()));
        }
    }

    @Override
    public void validate(Edge edge) {
        checkNotNull(edge, "edge");
        checkNotNull(edge.getEdgeIdentifier(), "edge.identifier");
        checkNotNullOrEmpty(edge.getEdgeIdentifier().getId(), "edge.identifier.id");
        checkCondition(edge.getEdgeIdentifier().getPriority() >= 0, "edge.priority cannot be less than 0");
        checkCondition(edge.getVersion() >= 0, "edge.version cannot be less than 0");
        if (edge.getFilters() != null
                && edge.getFilters()
                .stream()
                .mapToInt(k -> k.accept(new FilterCounter()))
                .sum() > bonsaiProperties.getMaxAllowedConditionsPerEdge()) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, "filters exceed max allowed:" + bonsaiProperties.getMaxAllowedConditionsPerEdge());
        }
        if (bonsaiProperties.isMutualExclusivitySettingTurnedOn()) {
            Set<String> allFields = edge.getFilters()
                    .stream()
                    .map(filter -> filter.accept(FIELD_IDENTIFIER))
                    .reduce(Stream::concat)
                    .orElse(Stream.empty())
                    .collect(Collectors.toSet());
            if (!allFields.isEmpty() && allFields.size() > 1) {
                throw new BonsaiError(BonsaiErrorCode.VARIATION_MUTUAL_EXCLUSIVITY_CONSTRAINT_ERROR, "fields are not mutually exclusive fields:" + allFields);
            }
        }
    }

    @Override
    public void validate(Context context) {
        checkNotNull(context.getDocumentContext(), "context.documentContext");
    }

    @Override
    public void validate(Variation variation) {
        checkNotNull(variation, "variation");
        checkNotNull(variation.getKnotId(), "variation.knotId");
        checkNotNullOrEmpty(variation.getFilters(), "variation.filters");
        checkCondition(variation.getPriority() >= 0, "variation.priority cannot be less than 0");
        if (variation.getFilters()
                .stream()
                .mapToInt(k -> k.accept(new FilterCounter()))
                .sum() > bonsaiProperties.getMaxAllowedConditionsPerEdge()) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, "singleConditionEdgeSettingTurnedOn is turned on, variation has more than 1 filter");
        }
        if (bonsaiProperties.isMutualExclusivitySettingTurnedOn()) {
            Set<String> allFields = variation.getFilters()
                    .stream()
                    .map(filter -> filter.accept(FIELD_IDENTIFIER))
                    .reduce(Stream::concat)
                    .orElse(Stream.empty())
                    .collect(Collectors.toSet());
            if (!allFields.isEmpty() && allFields.size() > 1) {
                throw new BonsaiError(BonsaiErrorCode.VARIATION_MUTUAL_EXCLUSIVITY_CONSTRAINT_ERROR);
            }
        }
    }

    @Override
    public void validate(final KeyMappingDeltaOperation keyMappingDeltaOperation) {
        checkNotNull(keyMappingDeltaOperation, "keyMappingDeltaOperation : [Key to KnotId Mapping]");
        checkNotNull(keyMappingDeltaOperation.getDeltaOperationType(), "keyMappingDeltaOperation.deltaOperationType");
        checkNotNullOrEmpty(keyMappingDeltaOperation.getKeyId(), "keyMappingDeltaOperation.keyId");
        checkNotNullOrEmpty(keyMappingDeltaOperation.getKnotId(), "keyMappingDeltaOperation.knotId");
    }

    @Override
    public void validate(final KnotDeltaOperation knotDeltaOperation) {
        checkNotNull(knotDeltaOperation, "knotDeltaOperation");
        checkNotNull(knotDeltaOperation.getDeltaOperationType(), "knotDeltaOperation.deltaOperationType");

        final Knot knot = knotDeltaOperation.getKnot();
        checkNotNull(knot, "knotDeltaOperation.knot");
        checkNotNullOrEmpty(knot.getId(), "knotDeltaOperation.knot.Id");
        checkNotNull(knot.getKnotData(), "knotDeltaOperation.knot.knotData");
        checkNotNull(knot.getKnotData().getKnotDataType(), "knotDeltaOperation.knot.knotData.knotDataType");
        validate(knot.getKnotData());
        // This condition will ensure, the edge has been added/modified.
        checkCondition((0 == knot.getVersion()),
                "The version of [delta knot] should be zero.");
    }

    @Override
    public void validate(final EdgeDeltaOperation edgeDeltaOperation) {
        checkNotNull(edgeDeltaOperation, "edgeDeltaOperation");
        checkNotNull(edgeDeltaOperation.getDeltaOperationType(), "edgeDeltaOperation.deltaOperationType");

        final Edge edge = edgeDeltaOperation.getEdge();
        checkNotNull(edge, "edgeDeltaOperation.edge");
        checkNotNull(edge.getEdgeIdentifier(), "edgeDeltaOperation.edge.edgeIdentifier");
        checkNotNullOrEmpty(edge.getEdgeIdentifier().getId(), "edgeDeltaOperation.edge.edgeIdentifier.id");
        // This check is important to make sure edge contains the mimimum details to connected child KnotId.
        checkNotNullOrEmpty(edge.getKnotId(), "edgeDeltaOperation.edge.knotId");
        checkCondition(edge.getEdgeIdentifier().getPriority() >= 0,
                "edgeDeltaOperation.edge.priority should be more than 0");

        final List<Filter> filters = edge.getFilters();
        checkNotNullOrEmpty(filters, "edgeDeltaOperation.edge.filters");
        checkCondition(filters.stream().mapToInt(k -> k.accept(new FilterCounter())).sum() <=
                        bonsaiProperties.getMaxAllowedConditionsPerEdge(),
                String.format("edgeDeltaOperation.edge.filters exceed max allowed count: %d.",
                        bonsaiProperties.getMaxAllowedConditionsPerEdge()));
        if (bonsaiProperties.isMutualExclusivitySettingTurnedOn()) {
            final Set<String> allFields = filters.stream().map(filter -> filter.accept(new FilterFieldIdentifier()))
                    .reduce(Stream::concat).orElse(Stream.empty()).collect(Collectors.toSet());
            if (!allFields.isEmpty() && allFields.size() > 1) {
                throw new BonsaiError(BonsaiErrorCode.VARIATION_MUTUAL_EXCLUSIVITY_CONSTRAINT_ERROR,
                        "fields are not mutually exclusive fields:" + allFields);
            }
        }
        // This condition will ensure, the edge has been added/modified.
        checkCondition((0 == edge.getVersion()),
                "The version of [delta edge] should be zero.");
    }

    @Override
    public void validate(final TreeKnot treeKnot) {
        checkNotNull(treeKnot, "Root TreeKnot can't be null");
        checkNotNull(treeKnot.getId(), "Root TreeKnot:Id can't be null");
        checkNotNull(treeKnot.getKnotData(), "Root TreeKnot:KnotData can't be null");

        final KnotData.KnotDataType rootKnotDataType = treeKnot.getKnotData().getKnotDataType();
        Value.ValueType rootKnotValueType = Value.ValueType.BOOLEAN;
        if (KnotData.KnotDataType.VALUED == rootKnotDataType) {
            final ValuedKnotData valuedRootKnotData = (ValuedKnotData) treeKnot.getKnotData();
            rootKnotValueType = valuedRootKnotData.getValue().getValueType();

        }

        traverseAndValidateTree(treeKnot, rootKnotDataType, rootKnotValueType);
    }

    private void traverseAndValidateTree(final TreeKnot treeKnot,
                                         final KnotData.KnotDataType rootKnotDataType,
                                         final Value.ValueType rootKnotValueType) {
        checkNotNull(treeKnot, "TreeKnot can't be null");
        checkNotNull(treeKnot.getId(), "TreeKnot:Id can't be null");
        checkNotNull(treeKnot.getKnotData(), "TreeKnot:KnotData can't be null");

        final KnotData knotData = treeKnot.getKnotData();
        final KnotData.KnotDataType knotDataType = knotData.getKnotDataType();
        checkCondition((rootKnotDataType == knotDataType),
                String.format("Internal KnotDataType : [%s]  isn't same as Root KnotDataType : [%s].", knotDataType, rootKnotDataType));

        if (KnotData.KnotDataType.VALUED == knotDataType) {
            final ValuedKnotData valuedKnotData = (ValuedKnotData) treeKnot.getKnotData();
            final Value.ValueType valueType = valuedKnotData.getValue().getValueType();
            checkCondition((rootKnotValueType == valueType),
                    String.format("Internal KnotValueType : [%s] isn't same as Root KnotValueType : [%s].", valueType, rootKnotValueType));
        }

        final List<TreeEdge> treeEdgeList =
                ((treeKnot.getTreeEdges() == null) ? new ArrayList<>() : treeKnot.getTreeEdges());

        checkCondition(treeEdgeList.size() <= bonsaiProperties.getMaxAllowedVariationsPerKnot(),
                String.format("Number of variation of any knot can not exceed : %d",
                        bonsaiProperties.getMaxAllowedVariationsPerKnot()));

        /* Check validation on edges, especiall when mutual exclusive property is on. */
        final List<Filter> allDirectFilters = new ArrayList<>();
        for (TreeEdge treeEdge : treeEdgeList) {
            final List<Filter> filters = treeEdge.getFilters();
            checkNotNullOrEmpty(filters, "TreeEdge:Filters can't be empty");
            checkCondition(filters.stream()
                            .mapToInt(k -> k.accept(new FilterCounter()))
                            .sum() <= bonsaiProperties.getMaxAllowedConditionsPerEdge(),
                    String.format("TreeEdge:Filters exceed max allowed count: %d.",
                            bonsaiProperties.getMaxAllowedConditionsPerEdge()));
            if (bonsaiProperties.isMutualExclusivitySettingTurnedOn()) {
                final Set<String> allFields = filters.stream()
                        .map(filter -> filter.accept(new FilterFieldIdentifier()))
                        .reduce(Stream::concat)
                        .orElse(Stream.empty())
                        .collect(Collectors.toSet());
                if (!allFields.isEmpty() && allFields.size() > 1) {
                    throw new BonsaiError(BonsaiErrorCode.VARIATION_MUTUAL_EXCLUSIVITY_CONSTRAINT_ERROR,
                            "fields are not mutually exclusive :" + allFields);
                }
            }
            allDirectFilters.addAll(treeEdge.getFilters());
        }

        if (bonsaiProperties.isMutualExclusivitySettingTurnedOn()) {
            final Set<String> allFields = allDirectFilters.stream()
                    .map(filter -> filter.accept(new FilterFieldIdentifier()))
                    .reduce(Stream::concat)
                    .orElse(Stream.empty())
                    .collect(Collectors.toSet());
            if (!allFields.isEmpty() && allFields.size() > 1) {
                throw new BonsaiError(BonsaiErrorCode.VARIATION_MUTUAL_EXCLUSIVITY_CONSTRAINT_ERROR,
                        "fields are not mutually exclusive at same edge-level : " + allFields);
            }
        }

        /* Recursively Iterate */
        for (TreeEdge treeEdge : treeEdgeList) {
            final TreeKnot innerTreeKnot = treeEdge.getTreeKnot();
            traverseAndValidateTree(innerTreeKnot, rootKnotDataType, rootKnotValueType);
        }
    }

    private void validate(KnotData knotData) {
        knotData.accept(new KnotDataVisitor<Void>() {
            @Override
            public Void visit(ValuedKnotData valuedKnotData) {
                return null;
            }

            @Override
            public Void visit(MultiKnotData multiKnotData) {
                checkNotNull(multiKnotData.getKeys(), "knot.knotData.keys[]");
                return null;
            }

            @Override
            public Void visit(MapKnotData mapKnotData) {
                checkNotNull(mapKnotData.getMapKeys(), "knot.knotData.mapKeys");
                return null;
            }
        });
    }
}
