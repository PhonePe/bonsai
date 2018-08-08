package com.phonepe.platform.bonsai.core.data;

import com.phonepe.platform.bonsai.core.core.KnotData;
import com.phonepe.platform.bonsai.core.core.KnotDataVisitor;
import lombok.*;

import java.util.List;

/**
 * @author tushar.naik
 * @version 1.0  27/07/18 - 12:34 AM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MultiKnotData extends KnotData {
    private List<String> keys;

    public MultiKnotData() {
        super(DataType.MULTI_KNOT);
    }

    @Builder
    public MultiKnotData(@Singular List<String> keys) {
        super(DataType.MULTI_KNOT);
        this.keys = keys;
    }

    @Override
    public <T> T accept(KnotDataVisitor<T> knotDataVisitor) {
        return knotDataVisitor.visit(this);
    }
}
