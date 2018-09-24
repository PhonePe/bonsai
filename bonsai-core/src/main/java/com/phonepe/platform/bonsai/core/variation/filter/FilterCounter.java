package com.phonepe.platform.bonsai.core.variation.filter;

import com.phonepe.platform.bonsai.core.variation.filter.general.*;
import com.phonepe.platform.bonsai.core.variation.filter.logical.AndFilter;
import com.phonepe.platform.bonsai.core.variation.filter.logical.OrFilter;
import com.phonepe.platform.bonsai.core.variation.filter.numeric.*;

/**
 * @author tushar.naik
 * @version 1.0  19/09/18 - 3:27 PM
 */
public class FilterCounter implements FilterVisitor<Integer> {

    @Override
    public Integer visit(ContainsFilter filter) {
        return 1;
    }

    @Override
    public Integer visit(LessThanFilter filter) {
        return 1;
    }

    @Override
    public Integer visit(LessEqualFilter filter) {
        return 1;
    }

    @Override
    public Integer visit(GreaterThanFilter filter) {
        return 1;
    }

    @Override
    public Integer visit(BetweenFilter filter) {
        return 1;
    }

    @Override
    public Integer visit(GreaterEqualFilter filter) {
        return 1;
    }

    @Override
    public Integer visit(NotInFilter filter) {
        return 1;
    }

    @Override
    public Integer visit(NotEqualsFilter filter) {
        return 1;
    }

    @Override
    public Integer visit(MissingFilter filter) {
        return 1;
    }

    @Override
    public Integer visit(InFilter filter) {
        return 1;
    }

    @Override
    public Integer visit(ExistsFilter filter) {
        return 1;
    }

    @Override
    public Integer visit(EqualsFilter filter) {
        return 1;
    }

    @Override
    public Integer visit(AnyFilter filter) {
        return 1;
    }

    @Override
    public Integer visit(AndFilter andFilter) {
        return andFilter.getFilters()
                        .stream()
                        .mapToInt(k -> k.accept(this))
                        .sum();
    }

    @Override
    public Integer visit(OrFilter orFilter) {
        return orFilter.getFilters()
                       .stream()
                       .mapToInt(k -> k.accept(this))
                       .sum();
    }
}
