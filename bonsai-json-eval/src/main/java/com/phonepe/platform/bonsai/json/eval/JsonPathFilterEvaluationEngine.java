package com.phonepe.platform.bonsai.json.eval;

import com.jayway.jsonpath.TypeRef;
import com.phonepe.platform.query.dsl.Filter;
import com.phonepe.platform.query.dsl.FilterVisitor;
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
import com.phonepe.platform.query.dsl.numeric.NumericBinaryFilter;
import com.phonepe.platform.query.dsl.string.StringEndsWithFilter;
import com.phonepe.platform.query.dsl.string.StringRegexMatchFilter;
import com.phonepe.platform.query.dsl.string.StringStartsWithFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This is a Json path based filter evaluator
 * A filter predicate visitor that will apply the filter, and tell whether it is true or false
 *
 * @author tushar.naik
 * @version 1.0  29/09/17 - 1:08 PM
 */
@Slf4j
@AllArgsConstructor
public class JsonPathFilterEvaluationEngine<C extends JsonEvalContext> implements FilterVisitor<Boolean> {

    private static final TypeRef<List<Number>> NUMBER_TYPE_REF = new TypeRef<List<Number>>() {
    };
    private static final TypeRef<List<String>> STRING_TYPE_REF = new TypeRef<List<String>>() {
    };
    private static final TypeRef<List<Object>> OBJECT_TYPE_REF = new TypeRef<List<Object>>() {
    };

    protected final String entityId;

    private final C context;

    private final Predicate<GenericFilterContext<C>> genericFilterHandler;

    @Override
    public Boolean visit(ContainsFilter filter) {
        if (filter.isIterable()) {
            return applyAllMatchFilter(filter, OBJECT_TYPE_REF, o -> {
                Set<String> items = new HashSet<>(((List<String>) o));
                return items.contains(filter.getValue());
            });
        }
        return applyAllMatchFilter(filter, STRING_TYPE_REF, k -> k.contains(filter.getValue()));
    }

    @Override
    public Boolean visit(LessThanFilter filter) {
        return applyAllMatchFilter(filter, NUMBER_TYPE_REF, lessThan(filter));
    }

    @Override
    public Boolean visit(LessEqualFilter filter) {
        return applyAllMatchFilter(filter, NUMBER_TYPE_REF, lessThanEquals(filter));
    }

    @Override
    public Boolean visit(GreaterThanFilter filter) {
        return applyAllMatchFilter(filter, NUMBER_TYPE_REF, greaterThan(filter));
    }

    @Override
    public Boolean visit(BetweenFilter filter) {
        return applyAllMatchFilter(filter, NUMBER_TYPE_REF, between(filter));
    }

    @Override
    public Boolean visit(GreaterEqualFilter filter) {
        return applyAllMatchFilter(filter, NUMBER_TYPE_REF, greaterThanEquals(filter));
    }

    @Override
    public Boolean visit(NotInFilter filter) {
        Predicate<Object> predicate = contains(filter);
        return applyNoneMatch(filter, OBJECT_TYPE_REF, predicate);
    }

    @Override
    public Boolean visit(NotEqualsFilter filter) {
        return applyNoneMatch(filter, OBJECT_TYPE_REF, k -> k.equals(filter.getValue()));
    }

    @Override
    public Boolean visit(MissingFilter filter) {
        List<Object> values = context.documentContext().read(filter.getField(), OBJECT_TYPE_REF);
        if (log.isTraceEnabled()) {
            log.trace("[bonsai] filter:{} values:{} document:{}", filter.getField(), values, context.documentContext().toString());
        }
        return values == null || values.isEmpty() || values.stream().allMatch(Objects::isNull);
    }

    @Override
    public Boolean visit(InFilter filter) {
        List<Object> nonNullValues = nonNullValues(filter, OBJECT_TYPE_REF);
        Set<Object> valueSet = new HashSet<>(filter.getValues());
        return isNotEmpty(nonNullValues) && nonNullValues.stream().anyMatch(valueSet::contains);
    }

    @Override
    public Boolean visit(ExistsFilter filter) {
        List<Object> values = context.documentContext().read(filter.getField(), OBJECT_TYPE_REF);
        if (log.isTraceEnabled()) {
            log.trace("[bonsai] filter:{} values:{} document:{}", filter.getField(), values, context.documentContext().toString());
        }
        return isNotEmpty(values);
    }

    @Override
    public Boolean visit(EqualsFilter filter) {
        return applyAllMatchFilter(filter, OBJECT_TYPE_REF, equalsFilter(filter));
    }

    @Override
    public Boolean visit(AnyFilter filter) {
        return true;
    }

    @Override
    public Boolean visit(AndFilter andFilter) {
        return andFilter.getFilters().stream().allMatch(k -> k.accept(this));
    }

    @Override
    public Boolean visit(OrFilter orFilter) {
        return orFilter.getFilters().stream().anyMatch(k -> k.accept(this));
    }

    @Override
    public Boolean visit(NotFilter notFilter) {
        return !notFilter.getFilter().accept(this);
    }

    @Override
    public Boolean visit(StringStartsWithFilter filter) {
        return applyAllMatchFilter(filter, STRING_TYPE_REF, k -> k.startsWith(filter.getValue()));
    }

    @Override
    public Boolean visit(StringEndsWithFilter filter) {
        return applyAllMatchFilter(filter, STRING_TYPE_REF, k -> k.endsWith(filter.getValue()));
    }

    @Override
    public Boolean visit(StringRegexMatchFilter filter) {
        /* todo create a small Pattern compiled cache, to avoid compile every time */
        return applyAllMatchFilter(filter, STRING_TYPE_REF, k -> k.matches(filter.getValue()));
    }

    @Override
    public Boolean visit(GenericFilter filter) {
        final GenericFilterContext<C> genericFilterContext = new GenericFilterContext<>(filter, context);
        return genericFilterHandler.test(genericFilterContext);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////  Helper Functions  /////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////


    private <T> Boolean applyAllMatchFilter(Filter filter, TypeRef<List<T>> typeRef, Predicate<T> predicate) {
        List<T> nonNullValues = nonNullValues(filter, typeRef);
        return isNotEmpty(nonNullValues) && nonNullValues.stream().allMatch(predicate);
    }

    private <T> List<T> nonNullValues(Filter filter, TypeRef<List<T>> typeRef) {
        List<T> values = context.documentContext().read(filter.getField(), typeRef);
        if (log.isTraceEnabled()) {
            log.trace("[bonsai] filter:{} values:{} document:{}", filter.getField(), values, context.documentContext().toString());
        }
        return values == null ? Collections.emptyList() : values.stream().filter(Objects::nonNull)
                                                                .collect(Collectors.toList());
    }

    private Predicate<Number> lessThan(NumericBinaryFilter filter) {
        return k -> k.floatValue() < filter.getValue().floatValue();
    }

    private Predicate<Number> lessThanEquals(NumericBinaryFilter filter) {
        return k -> k.floatValue() <= filter.getValue().floatValue();
    }

    private Predicate<Number> greaterThan(NumericBinaryFilter filter) {
        return k -> k.floatValue() > filter.getValue().floatValue();
    }

    private Predicate<Number> between(BetweenFilter filter) {
        return k -> k.floatValue() > filter.getFrom().floatValue()
                && k.floatValue() < filter.getTo().floatValue();
    }

    private Predicate<Number> greaterThanEquals(NumericBinaryFilter filter) {
        return k -> k.floatValue() >= filter.getValue().floatValue();
    }

    private Predicate<Object> equalsFilter(EqualsFilter filter) {
        return k -> k.equals(filter.getValue());
    }

    private Predicate<Object> contains(NotInFilter filter) {
        Set<Object> notIn = new HashSet<>(filter.getValues());
        return notIn::contains;
    }

    private <T> Boolean applyNoneMatch(Filter filter,
                                       @SuppressWarnings("SameParameterValue") TypeRef<List<T>> typeRef,
                                       Predicate<T> predicate) {
        List<T> nonNullValues = nonNullValues(filter, typeRef);
        return isNotEmpty(nonNullValues) && nonNullValues.stream().noneMatch(predicate);
    }

    private <T> boolean isNotEmpty(List<T> nonNullValues) {
        return nonNullValues != null && !nonNullValues.isEmpty();
    }

}
