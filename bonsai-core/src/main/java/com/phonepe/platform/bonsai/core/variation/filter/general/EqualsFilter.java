package com.phonepe.platform.bonsai.core.variation.filter.general;

import com.phonepe.platform.bonsai.core.variation.filter.Filter;
import com.phonepe.platform.bonsai.core.variation.filter.FilterOperator;
import com.phonepe.platform.bonsai.core.variation.filter.FilterVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * @author tushar.naik
 * @version 1.0  03/05/17 - 2:34 PM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EqualsFilter extends Filter {

    private Object value;

    public EqualsFilter() {
        super(FilterOperator.EQUALS);
    }

    public EqualsFilter(String field, Object value) {
        super(FilterOperator.EQUALS, field);
        this.value = value;
    }

    @Override
    public <V> V accept(FilterVisitor<V> visitor) {
        return visitor.visit(this);
    }
}
