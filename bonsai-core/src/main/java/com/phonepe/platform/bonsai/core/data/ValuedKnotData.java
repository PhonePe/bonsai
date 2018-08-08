package com.phonepe.platform.bonsai.core.data;

import com.phonepe.platform.bonsai.core.core.KnotData;
import com.phonepe.platform.bonsai.core.core.KnotDataVisitor;
import com.phonepe.platform.bonsai.core.data.value.Value;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author tushar.naik
 * @version 1.0  27/07/18 - 12:34 AM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ValuedKnotData extends KnotData {
    private Value value;

    public ValuedKnotData() {
        super(DataType.VALUED);
    }

    @Builder
    public ValuedKnotData(Value value) {
        super(DataType.VALUED);
        this.value = value;
    }

    @Override
    public <T> T accept(KnotDataVisitor<T> knotDataVisitor) {
        return knotDataVisitor.visit(this);
    }
}
