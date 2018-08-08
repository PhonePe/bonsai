package com.phonepe.platform.bonsai.core.query;

import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.phonepe.platform.bonsai.core.query.filter.FilterVisitor;
import com.phonepe.platform.bonsai.core.query.filter.general.*;
import com.phonepe.platform.bonsai.core.query.filter.logical.AndFilter;
import com.phonepe.platform.bonsai.core.query.filter.logical.OrFilter;
import com.phonepe.platform.bonsai.core.query.filter.numeric.*;

/**
 * A jayway jsonpath equivalent of the {@link EvaluateJsonFilter} (which builders the filter, that may be applied)
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
