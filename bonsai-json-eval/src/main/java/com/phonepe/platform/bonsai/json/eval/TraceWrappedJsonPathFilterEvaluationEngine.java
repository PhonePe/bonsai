package com.phonepe.platform.bonsai.json.eval;

import com.phonepe.platform.query.dsl.Filter;
import com.phonepe.platform.query.dsl.general.AnyFilter;
import com.phonepe.platform.query.dsl.general.ContainsFilter;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import com.phonepe.platform.query.dsl.general.ExistsFilter;
import com.phonepe.platform.query.dsl.general.GenericFilter;
import com.phonepe.platform.query.dsl.general.InFilter;
import com.phonepe.platform.query.dsl.general.MissingFilter;
import com.phonepe.platform.query.dsl.general.NotEqualsFilter;
import com.phonepe.platform.query.dsl.general.NotInFilter;
import com.phonepe.platform.query.dsl.logical.AndFilter;
import com.phonepe.platform.query.dsl.logical.NotFilter;
import com.phonepe.platform.query.dsl.logical.OrFilter;
import com.phonepe.platform.query.dsl.numeric.BetweenFilter;
import com.phonepe.platform.query.dsl.numeric.GreaterEqualFilter;
import com.phonepe.platform.query.dsl.numeric.GreaterThanFilter;
import com.phonepe.platform.query.dsl.numeric.LessEqualFilter;
import com.phonepe.platform.query.dsl.numeric.LessThanFilter;
import com.phonepe.platform.query.dsl.string.StringEndsWithFilter;
import com.phonepe.platform.query.dsl.string.StringRegexMatchFilter;
import com.phonepe.platform.query.dsl.string.StringStartsWithFilter;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;

@Slf4j
public class TraceWrappedJsonPathFilterEvaluationEngine<C extends JsonEvalContext>
        extends JsonPathFilterEvaluationEngine<C> {

    public TraceWrappedJsonPathFilterEvaluationEngine(String entityId, C context,
                                                      Predicate<GenericFilterContext<C>> genericFilterHandler) {
        super(entityId, context, genericFilterHandler);
    }

    @Override
    public Boolean visit(ContainsFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(LessThanFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(LessEqualFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(GreaterThanFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(BetweenFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(GreaterEqualFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(NotInFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(NotEqualsFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(MissingFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(InFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(ExistsFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(EqualsFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(AnyFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(AndFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(OrFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(NotFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(StringStartsWithFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(StringEndsWithFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(StringRegexMatchFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    @Override
    public Boolean visit(GenericFilter filter) {
        boolean result = super.visit(filter);
        trace(filter, result);
        return result;
    }

    private void trace(Filter filter, boolean result) {
        log.trace("[bonsai][{}}][{}] eval result: {}",   filter.getClass().getSimpleName(), entityId, result);
    }
}
