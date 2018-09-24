package com.phonepe.platform.bonsai.core.variation.filter.general;

import com.phonepe.platform.bonsai.core.variation.filter.Filter;
import com.phonepe.platform.bonsai.core.variation.filter.FilterOperator;
import com.phonepe.platform.bonsai.core.variation.filter.FilterVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author tushar.naik
 * @version 1.0  03/05/17 - 2:47 PM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NotEqualsFilter extends Filter {

    private Object value;

    public NotEqualsFilter() {
        super(FilterOperator.NOT_EQUALS);
    }

    public NotEqualsFilter(String field, String value) {
        super(FilterOperator.NOT_EQUALS, field);
        this.value = value;
    }

    @Override
    public <V> V accept(FilterVisitor<V> visitor) {
        return visitor.visit(this);
    }
}
