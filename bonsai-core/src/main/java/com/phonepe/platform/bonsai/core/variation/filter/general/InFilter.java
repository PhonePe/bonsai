package com.phonepe.platform.bonsai.core.variation.filter.general;

import com.phonepe.platform.bonsai.core.variation.filter.Filter;
import com.phonepe.platform.bonsai.core.variation.filter.FilterOperator;
import com.phonepe.platform.bonsai.core.variation.filter.FilterVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @author tushar.naik
 * @version 1.0  03/05/17 - 2:32 PM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InFilter extends Filter {

    private List<Object> values;

    public InFilter() {
        super(FilterOperator.IN);
    }

    public InFilter(String field, List<Object> values) {
        super(FilterOperator.IN, field);
        this.values = values;
    }

    @Override
    public <V> V accept(FilterVisitor<V> visitor) {
        return visitor.visit(this);
    }
}
