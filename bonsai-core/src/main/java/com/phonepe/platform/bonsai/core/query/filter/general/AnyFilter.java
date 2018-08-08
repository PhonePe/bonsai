package com.phonepe.platform.bonsai.core.query.filter.general;

import com.phonepe.platform.bonsai.core.query.filter.Filter;
import com.phonepe.platform.bonsai.core.query.filter.FilterOperator;
import com.phonepe.platform.bonsai.core.query.filter.FilterVisitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AnyFilter extends Filter {

    public AnyFilter() {
        super(FilterOperator.ANY, "dummy");
    }

    @Override
    public <V> V accept(FilterVisitor<V> visitor) {
        return visitor.visit(this);
    }
}

