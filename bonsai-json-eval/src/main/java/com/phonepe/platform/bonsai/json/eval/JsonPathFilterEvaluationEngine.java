package com.phonepe.platform.bonsai.json.eval;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.TypeRef;
import com.phonepe.platform.query.dsl.FilterVisitor;
import com.phonepe.platform.query.dsl.general.*;
import com.phonepe.platform.query.dsl.logical.AndFilter;
import com.phonepe.platform.query.dsl.logical.OrFilter;
import com.phonepe.platform.query.dsl.numeric.*;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
        List<String> values = context.read(filter.getField(), STRING_TYPE_REF);
        List<String> nonNullValues = values == null ? null : values.stream().filter(Objects::nonNull)
                                                                   .collect(Collectors.toList());
        return nonNullValues != null && !nonNullValues.isEmpty()
                && nonNullValues.stream().allMatch(k -> k.contains(filter.getValue()));
    }

    @Override
    public Boolean visit(LessThanFilter filter) {
        List<Number> values = context.read(filter.getField(), NUMBER_TYPE_REF);
        List<Number> nonNullValues = values == null ? null : values.stream().filter(Objects::nonNull)
                                                                   .collect(Collectors.toList());
        return nonNullValues != null && !nonNullValues.isEmpty()
                && nonNullValues.stream().allMatch(k -> k.floatValue() < filter.getValue().floatValue());
    }

    @Override
    public Boolean visit(LessEqualFilter filter) {
        List<Number> values = context.read(filter.getField(), NUMBER_TYPE_REF);
        List<Number> nonNullValues = values == null ? null : values.stream().filter(Objects::nonNull)
                                                                   .collect(Collectors.toList());
        return nonNullValues != null && !nonNullValues.isEmpty()
                && nonNullValues.stream().allMatch(k -> k.floatValue() <= filter.getValue().floatValue());
    }

    @Override
    public Boolean visit(GreaterThanFilter filter) {
        List<Number> values = context.read(filter.getField(), NUMBER_TYPE_REF);
        List<Number> nonNullValues = values == null ? null : values.stream().filter(Objects::nonNull)
                                                                   .collect(Collectors.toList());
        return nonNullValues != null && !nonNullValues.isEmpty()
                && nonNullValues.stream().allMatch(k -> k.floatValue() > filter.getValue().floatValue());
    }

    @Override
    public Boolean visit(BetweenFilter filter) {
        List<Number> values = context.read(filter.getField(), NUMBER_TYPE_REF);
        List<Number> nonNullValues = values == null ? null : values.stream().filter(Objects::nonNull)
                                                                   .collect(Collectors.toList());
        return nonNullValues != null && !nonNullValues.isEmpty()
                && nonNullValues.stream().allMatch(k -> k.floatValue() > filter.getFrom().floatValue()
                && k.floatValue() < filter.getTo().floatValue());
    }

    @Override
    public Boolean visit(GreaterEqualFilter filter) {
        List<Number> values = context.read(filter.getField(), NUMBER_TYPE_REF);
        List<Number> nonNullValues = values == null ? null : values.stream().filter(Objects::nonNull)
                                                                   .collect(Collectors.toList());
        return nonNullValues != null && !nonNullValues.isEmpty()
                && nonNullValues.stream().allMatch(k -> k.floatValue() >= filter.getValue().floatValue());
    }

    @Override
    public Boolean visit(NotInFilter filter) {
        List<Object> values = context.read(filter.getField(), OBJECT_TYPE_REF);
        List<Object> nonNullValues = values == null ? null : values.stream().filter(Objects::nonNull)
                                                                   .collect(Collectors.toList());
        Set<Object> notIn = new HashSet<>(filter.getValues());
        return nonNullValues != null && !nonNullValues.isEmpty() && nonNullValues.stream()
                                                                                 .noneMatch(notIn::contains);
    }

    @Override
    public Boolean visit(NotEqualsFilter filter) {
        List<Object> values = context.read(filter.getField(), OBJECT_TYPE_REF);
        List<Object> nonNullValues = values == null ? null : values.stream().filter(Objects::nonNull)
                                                                   .collect(Collectors.toList());
        return nonNullValues != null && !nonNullValues.isEmpty()
                && nonNullValues.stream().noneMatch(k -> k.equals(filter.getValue()));
    }

    @Override
    public Boolean visit(MissingFilter filter) {
        List<Object> values = context.read(filter.getField(), OBJECT_TYPE_REF);
        return values == null || values.isEmpty() || values.stream().allMatch(Objects::isNull);
    }

    @Override
    public Boolean visit(InFilter filter) {
        List<Object> values = context.read(filter.getField(), OBJECT_TYPE_REF);
        List<Object> nonNullValues = values == null ? null : values.stream().filter(Objects::nonNull)
                                                                   .collect(Collectors.toList());
        Set<Object> notIn = new HashSet<>(filter.getValues());
        return nonNullValues != null && !nonNullValues.isEmpty() && nonNullValues.stream()
                                                                                 .anyMatch(notIn::contains);
    }

    @Override
    public Boolean visit(ExistsFilter filter) {
        List<Object> values = context.read(filter.getField(), OBJECT_TYPE_REF);
        return values != null && !values.isEmpty();
    }

    @Override
    public Boolean visit(EqualsFilter filter) {
        List<Object> values = context.read(filter.getField(), OBJECT_TYPE_REF);
        List<Object> nonNullValues = values == null ? null : values.stream().filter(Objects::nonNull)
                                                                   .collect(Collectors.toList());
        return nonNullValues != null && !nonNullValues.isEmpty()
                && nonNullValues.stream().allMatch(k -> k.equals(filter.getValue()));
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
}
