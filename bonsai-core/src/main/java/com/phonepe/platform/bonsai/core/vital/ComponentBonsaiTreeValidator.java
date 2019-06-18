package com.phonepe.platform.bonsai.core.vital;

import com.google.common.base.Strings;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.core.vital.blocks.Variation;
import com.phonepe.platform.query.dsl.FilterCounter;
import com.phonepe.platform.query.dsl.FilterFieldIdentifier;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is a final class on purpose
 * The outside user of this library is not supposed to override this
 *
 * @author tushar.naik
 * @version 1.0  23/08/18 - 1:07 PM
 */
public final class ComponentBonsaiTreeValidator implements BonsaiTreeValidator {
    private static final String ERROR_FIELD_STR = "field:";
    private BonsaiProperties bonsaiProperties;

    public ComponentBonsaiTreeValidator(BonsaiProperties bonsaiProperties) {
        this.bonsaiProperties = bonsaiProperties;
    }

    @Override
    public void validate(Knot knot) {
        checkNotNull(knot, "knot");
        checkNotNullOrEmpty(knot.getId(), "knot.id");
        checkNotNull(knot.getKnotData(), "knot.knotData");
        checkNotNull(knot.getKnotData().getKnotDataType(), "knot.knotData.knotDataType");
        checkCondition(knot.getVersion() >= 0, "knot.version cannot be less than 0");
        if (knot.getEdges() != null && knot.getEdges().size() > bonsaiProperties.getMaxAllowedVariationsPerKnot()) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, "variations exceed max allowed:" + bonsaiProperties.getMaxAllowedVariationsPerKnot());
        }
    }

    @Override
    public void validate(Knot knot, Knot knot2) {
        if (!TreeUtils.isKnotDataOfSimilarType(knot, knot2)) {
            throw new BonsaiError(BonsaiErrorCode.KNOT_RESOLUTION_ERROR,
                                  String.format("knotData class mismatch rootKnot:%s variationKnot:%s",
                                                knot.getKnotData().getClass(),
                                                knot2.getKnotData().getClass()));
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
                                        .map(filter -> filter.accept(new FilterFieldIdentifier()))
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
                                             .map(filter -> filter.accept(new FilterFieldIdentifier()))
                                             .reduce(Stream::concat)
                                             .orElse(Stream.empty())
                                             .collect(Collectors.toSet());
            if (!allFields.isEmpty() && allFields.size() > 1) {
                throw new BonsaiError(BonsaiErrorCode.VARIATION_MUTUAL_EXCLUSIVITY_CONSTRAINT_ERROR);
            }
        }
    }

    private static <T> void checkNotNull(T reference, String fieldName) {
        if (reference == null) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, ERROR_FIELD_STR + fieldName + " cannot be null");
        }
    }

    private static void checkNotNullOrEmpty(String reference, String fieldName) {
        if (Strings.isNullOrEmpty(reference)) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, ERROR_FIELD_STR + fieldName + " cannot be null");
        }
    }

    private static <T> void checkNotNullOrEmpty(Collection<T> reference, String fieldName) {
        if (reference == null || reference.isEmpty()) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, ERROR_FIELD_STR + fieldName + " cannot be null");
        }
    }

    private static void checkCondition(boolean condition, String errorReason) {
        if (!condition) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, errorReason);
        }
    }
}
