package com.phonepe.platform.bonsai.core.query.filter.general;


import com.phonepe.platform.bonsai.core.query.filter.Filter;
import com.phonepe.platform.bonsai.core.query.filter.FilterOperator;
import com.phonepe.platform.bonsai.core.query.filter.FilterVisitor;
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
public class MissingFilter extends Filter {

    public MissingFilter() {
        super(FilterOperator.MISSING);
    }

    public MissingFilter(String field) {
        super(FilterOperator.MISSING, field);
    }

    @Override
    public <V> V accept(FilterVisitor<V> visitor) {
        return visitor.visit(this);
    }

}
