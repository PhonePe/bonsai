package com.phonepe.platform.bonsai.json.eval;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.TypeRef;
import com.phonepe.platform.query.dsl.Filter;
import com.phonepe.platform.query.dsl.FilterVisitor;
import com.phonepe.platform.query.dsl.general.*;
import com.phonepe.platform.query.dsl.logical.AndFilter;
import com.phonepe.platform.query.dsl.logical.OrFilter;
import com.phonepe.platform.query.dsl.numeric.*;
import com.phonepe.platform.query.dsl.string.StringEndsWithFilter;
import com.phonepe.platform.query.dsl.string.StringRegexMatchFilter;
import com.phonepe.platform.query.dsl.string.StringStartsWithFilter;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This is a Json path based filter evaluator
 * A filter predicate visitor that will apply the filter, and tell whether it is true or false
 *
 * @author tushar.naik
 * @version 1.0  29/09/17 - 1:08 PM
 */
@AllArgsConstructor
public class JsonPathFilterEvaluationEngine implements FilterVisitor<Boolean> {

    private static final TypeRef<List<Number>> NUMBER_TYPE_REF = new TypeRef<List<Number>>() {
    };
    private static final TypeRef<List<String>> STRING_TYPE_REF = new TypeRef<List<String>>() {
    };
    private static final TypeRef<List<Object>> OBJECT_TYPE_REF = new TypeRef<List<Object>>() {
    };

    private DocumentContext context;

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
        List<Object> values = context.read(filter.getField(), OBJECT_TYPE_REF);
        return values == null || values.isEmpty() || values.stream().allMatch(Objects::isNull);
    }

    @Override
    public Boolean visit(InFilter filter) {
        List<Object> nonNullValues = nonNullValues(filter, OBJECT_TYPE_REF);
        Set<Object> notIn = new HashSet<>(filter.getValues());
        return isNotEmpty(nonNullValues) && nonNullValues.stream().anyMatch(notIn::contains);
    }

    @Override
    public Boolean visit(ExistsFilter filter) {
        List<Object> values = context.read(filter.getField(), OBJECT_TYPE_REF);
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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////  Helper Functions  /////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////



    private <T> Boolean applyAllMatchFilter(Filter filter, TypeRef<List<T>> typeRef, Predicate<T> predicate) {
        List<T> nonNullValues = nonNullValues(filter, typeRef);
        return isNotEmpty(nonNullValues) && nonNullValues.stream().allMatch(predicate);
    }

    private <T> List<T> nonNullValues(Filter filter, TypeRef<List<T>> typeRef) {
        List<T> values = context.read(filter.getField(), typeRef);
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
