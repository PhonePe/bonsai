package com.phonepe.platform.bonsai.core.vital;

import com.google.common.base.Strings;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;

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
    public void validate(Knot knot) {
        checkNotNull(knot, "knot");
        checkNotNullOrEmpty(knot.getId(), "knot.id");
        validate(knot.getKnotData());
    }

    @Override
    public void validate(Edge edge) {
        checkNotNull(edge, "edge");
        checkNotNullOrEmpty(edge.getId(), "edge.id");
        checkNotNullOrEmpty(edge.getPivot(), "edge.pivot");
        if (bonsaiProperties.isSinglePivotEdgeSetting() && edge.getConditions().size() > 1) {
            throw new BonsaiError(BonsaiErrorCode.INVALID_INPUT, "singlePivotEdgeSetting is turned on, edge has more than 1 conditions");
        }
    }

    @Override
    public void validate(Context context) {
        checkNotNull(context.getDocumentContext(), "context.documentContext");
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
}
