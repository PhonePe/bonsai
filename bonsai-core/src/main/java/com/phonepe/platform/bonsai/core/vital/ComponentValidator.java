package com.phonepe.platform.bonsai.core.vital;

import com.google.common.base.Strings;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.core.query.filter.Filter;
import com.phonepe.platform.bonsai.core.vital.provided.model.AtomicEdge;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tushar.naik
 * @version 1.0  23/08/18 - 1:07 PM
 */
public class ComponentValidator implements Validator {
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
    public void validate(AtomicEdge atomicEdge) {
        checkNotNull(atomicEdge, "atomicEdge");
        checkNotNullOrEmpty(atomicEdge.getId(), "atomicEdge.id");
        if (bonsaiProperties.isSingleConditionEdgeSettingTurnedOn() && atomicEdge.getFilters().size() > 1) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, "singleConditionEdgeSettingTurnedOn is turned on, atomicEdge has more than 1 filters");
        } else if (bonsaiProperties.isMutualExclusivitySettingTurnedOn()) {
            Set<String> allFields = atomicEdge.getFilters().stream().map(Filter::getField).collect(Collectors.toSet());
            if (!allFields.isEmpty() && allFields.size() > 1) {
                throw new BonsaiError(BonsaiErrorCode.EDGE_PIVOT_CONSTRAINT_ERROR);
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
        if (bonsaiProperties.isSingleConditionEdgeSettingTurnedOn() && variation.getFilters().size() > 1) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, "singleConditionEdgeSettingTurnedOn is turned on, variation has more than 1 filter");
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

    private static<T> void checkNotNullOrEmpty(Collection<T> reference, String fieldName) {
        if (reference == null || reference.isEmpty()) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, "field:" + fieldName + " cannot be null");
        }
    }
}
