package com.phonepe.platform.bonsai.core.variation.filter.numeric;

import com.phonepe.platform.bonsai.core.variation.filter.FilterOperator;
import com.phonepe.platform.bonsai.core.variation.filter.FilterVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author tushar.naik
 * @version 1.0  03/05/17 - 2:37 PM
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GreaterEqualFilter extends NumericBinaryFilter {

    public GreaterEqualFilter() {
        super(FilterOperator.GREATER_EQUAL);
    }

    public GreaterEqualFilter(String field, Number value) {
        super(FilterOperator.GREATER_EQUAL, field, value);
    }

    @Override
    public <V> V accept(FilterVisitor<V> visitor) {
        return visitor.visit(this);
    }
}
