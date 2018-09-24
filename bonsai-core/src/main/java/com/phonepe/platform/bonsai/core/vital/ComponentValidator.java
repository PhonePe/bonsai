package com.phonepe.platform.bonsai.core.vital;

import com.google.common.base.Strings;
import com.phonepe.platform.bonsai.core.data.KnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.core.variation.filter.FilterCounter;
import com.phonepe.platform.bonsai.core.variation.filter.FilterFieldIdentifier;
import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import com.phonepe.platform.bonsai.core.vital.blocks.Variation;

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
public final class ComponentValidator implements Validator {
    private BonsaiProperties bonsaiProperties;

    public ComponentValidator(BonsaiProperties bonsaiProperties) {
        this.bonsaiProperties = bonsaiProperties;
    }

    @Override
    public void validate(KnotData knotData) {
        checkNotNull(knotData, "knotData");
        checkNotNull(knotData.getDataType(), "knotData.dataType");
    }

    @Override
    public void validate(Edge edge) {
        checkNotNull(edge, "edge");
        checkNotNull(edge.getEdgeIdentifier(), "edge.identifier");
        checkNotNullOrEmpty(edge.getEdgeIdentifier().getId(), "edge.id");
        checkCondition(edge.getEdgeIdentifier().getPriority() >= 0, "edge.priority cannot be less than 0");
        checkCondition(edge.getVersion() >= 0, "edge.version cannot be less than 0");
        if (bonsaiProperties.isSingleConditionEdgeSettingTurnedOn()
                && edge.getFilters() != null
                && edge.getFilters()
                       .stream()
                       .mapToInt(k -> k.accept(new FilterCounter()))
                       .sum() > 1) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, "singleConditionEdgeSettingTurnedOn is turned on, edge has more than 1 filters");
        }
        if (bonsaiProperties.isMutualExclusivitySettingTurnedOn()) {
            Set<String> allFields = edge.getFilters()
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

    @Override
    public void validate(Context context) {
        checkNotNull(context.getDocumentContext(), "context.documentContext");
    }

    @Override
    public void validate(Variation variation) {
        checkNotNull(variation, "variation");
        checkNotNull(variation.getKnotId(), "variation.knotId");
        checkNotNullOrEmpty(variation.getFilters(), "variation.filters"); //todo check this
        checkCondition(variation.getPriority() >= 0, "variation.priority cannot be less than 0");
        if (bonsaiProperties.isSingleConditionEdgeSettingTurnedOn()
                && variation.getFilters()
                            .stream()
                            .mapToInt(k -> k.accept(new FilterCounter()))
                            .sum() > 1) {
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
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, "field:" + fieldName + " cannot be null");
        }
    }

    private static void checkNotNullOrEmpty(String reference, String fieldName) {
        if (Strings.isNullOrEmpty(reference)) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, "field:" + fieldName + " cannot be null");
        }
    }

    private static <T> void checkNotNullOrEmpty(Collection<T> reference, String fieldName) {
        if (reference == null || reference.isEmpty()) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, "field:" + fieldName + " cannot be null");
        }
    }

    private static <T> void checkCondition(boolean condition, String errorReason) {
        if (!condition) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, errorReason);
        }
    }
}
