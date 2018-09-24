package com.phonepe.platform.bonsai.core.variation.filter.numeric;

import com.phonepe.platform.bonsai.core.variation.filter.Filter;
import com.phonepe.platform.bonsai.core.variation.filter.FilterOperator;
import com.phonepe.platform.bonsai.core.variation.filter.FilterVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author tushar.naik
 * @version 1.0  03/05/17 - 2:42 PM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BetweenFilter extends Filter {

    private Number from;

    private Number to;

    public BetweenFilter() {
        super(FilterOperator.BETWEEN);
    }

    public BetweenFilter(String field, Number from, Number to) {
        super(FilterOperator.BETWEEN, field);
        this.from = from;
        this.to = to;
    }

    @Override
    public <V> V accept(FilterVisitor<V> visitor) {
        return visitor.visit(this);
    }
}
