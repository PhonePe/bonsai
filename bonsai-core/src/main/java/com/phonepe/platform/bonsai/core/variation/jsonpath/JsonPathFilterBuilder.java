package com.phonepe.platform.bonsai.core.variation.jsonpath;

import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.phonepe.platform.bonsai.core.variation.FilterEvaluationEngine;
import com.phonepe.platform.query.dsl.FilterVisitor;
import com.phonepe.platform.query.dsl.general.AnyFilter;
import com.phonepe.platform.query.dsl.general.ContainsFilter;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import com.phonepe.platform.query.dsl.general.ExistsFilter;
import com.phonepe.platform.query.dsl.general.InFilter;
import com.phonepe.platform.query.dsl.general.MissingFilter;
import com.phonepe.platform.query.dsl.general.NotEqualsFilter;
import com.phonepe.platform.query.dsl.general.NotInFilter;
import com.phonepe.platform.query.dsl.logical.AndFilter;
import com.phonepe.platform.query.dsl.logical.OrFilter;
import com.phonepe.platform.query.dsl.numeric.BetweenFilter;
import com.phonepe.platform.query.dsl.numeric.GreaterEqualFilter;
import com.phonepe.platform.query.dsl.numeric.GreaterThanFilter;
import com.phonepe.platform.query.dsl.numeric.LessEqualFilter;
import com.phonepe.platform.query.dsl.numeric.LessThanFilter;

/**
 * A jayway jsonpath equivalent of the {@link FilterEvaluationEngine} (which builders the filter, that may be applied)
 *
 * @author tushar.naik
 * @version 1.0  20/06/18 - 7:32 PM
 */
public class JsonPathFilterBuilder implements FilterVisitor<Filter> {

    @Override
    public Filter visit(ContainsFilter filter) {
        return Filter.filter(Criteria.where(filter.getField()).contains(filter.getValue()));
    }

    @Override
    public Filter visit(LessThanFilter filter) {
        return Filter.filter(Criteria.where(filter.getField()).lt(filter.getValue()));
    }

    @Override
    public Filter visit(LessEqualFilter filter) {
        return Filter.filter(Criteria.where(filter.getField()).lte(filter.getValue()));
    }

    @Override
    public Filter visit(GreaterThanFilter filter) {
        return Filter.filter(Criteria.where(filter.getField()).gt(filter.getValue()));
    }

    @Override
    public Filter visit(BetweenFilter filter) {
        return Filter.filter(Criteria.where(filter.getField()).gt(filter.getFrom())
                                     .and(filter.getField()).lt(filter.getTo()));
    }

    @Override
    public Filter visit(GreaterEqualFilter filter) {
        return Filter.filter(Criteria.where(filter.getField()).gte(filter.getValue()));
    }

    @Override
    public Filter visit(NotInFilter filter) {
        return Filter.filter(Criteria.where(filter.getField()).nin(filter.getValues()));
    }

    @Override
    public Filter visit(NotEqualsFilter filter) {
        return Filter.filter(Criteria.where(filter.getField()).ne(filter.getValue()));
    }

    @Override
    public Filter visit(InFilter filter) {
        return Filter.filter(Criteria.where(filter.getField()).in(filter.getValues()));
    }

    @Override
    public Filter visit(MissingFilter filter) {
        return Filter.filter(Criteria.where(filter.getField()).empty(true));
    }

    @Override
    public Filter visit(ExistsFilter filter) {
        return Filter.filter(Criteria.where(filter.getField()).empty(false));
    }

    @Override
    public Filter visit(EqualsFilter filter) {
        return Filter.filter(Criteria.where(filter.getField()).eq(filter.getValue()));
    }

    @Override
    public Filter visit(AnyFilter filter) {
        return Filter.filter(ctx -> true);
    }

    @Override
    public Filter visit(AndFilter andFilter) {
        return andFilter.getFilters().stream().map(k -> k.accept(this)).reduce(Filter::or).orElse(null);
    }

    @Override
    public Filter visit(OrFilter orFilter) {
        return orFilter.getFilters().stream().map(k -> k.accept(this)).reduce(Filter::or).orElse(null);
    }
}
