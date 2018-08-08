package com.phonepe.platform.bonsai.core.query.filter.general;

import com.phonepe.platform.bonsai.core.query.filter.Filter;
import com.phonepe.platform.bonsai.core.query.filter.FilterOperator;
import com.phonepe.platform.bonsai.core.query.filter.FilterVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author tushar.naik
 * @version 1.0  03/05/17 - 2:43 PM
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ContainsFilter extends Filter {

    private String value;

    public ContainsFilter() {
        super(FilterOperator.CONTAINS);
    }

    public ContainsFilter(String field, String value) {
        super(FilterOperator.CONTAINS, field);
        this.value = value;
    }

    @Override
    public <V> V accept(FilterVisitor<V> visitor) {
        return visitor.visit(this);
    }
}
