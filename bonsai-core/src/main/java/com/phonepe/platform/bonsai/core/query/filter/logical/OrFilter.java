package com.phonepe.platform.bonsai.core.query.filter.logical;

import com.phonepe.platform.bonsai.core.query.filter.Filter;
import com.phonepe.platform.bonsai.core.query.filter.FilterOperator;
import com.phonepe.platform.bonsai.core.query.filter.FilterVisitor;
import lombok.*;

import java.util.List;

/**
 * @author tushar.naik
 * @version 1.0  08/05/17 - 2:22 PM
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OrFilter extends Filter {

    @Singular
    @Getter
    List<Filter> filters;

    protected OrFilter() {
        super(FilterOperator.OR);
    }

    @Builder
    public OrFilter(@Singular List<Filter> filters) {
        super(FilterOperator.OR);
        this.filters = filters;
    }

    @Override
    public <V> V accept(FilterVisitor<V> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean validate(){
        return filters.stream().map(Filter::validate).reduce((x, y) -> x && y).orElse(false);
    }
}
