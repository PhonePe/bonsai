package com.phonepe.platform.bonsai.core.variation.filter.general;

import com.phonepe.platform.bonsai.core.variation.filter.Filter;
import com.phonepe.platform.bonsai.core.variation.filter.FilterOperator;
import com.phonepe.platform.bonsai.core.variation.filter.FilterVisitor;
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

