/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.phonepe.commons.bonsai.json.eval;

import com.jayway.jsonpath.TypeRef;
import com.phonepe.commons.query.dsl.Filter;
import com.phonepe.commons.query.dsl.FilterVisitor;
import com.phonepe.commons.query.dsl.general.AnyFilter;
import com.phonepe.commons.query.dsl.general.ContainsFilter;
import com.phonepe.commons.query.dsl.general.EqualsFilter;
import com.phonepe.commons.query.dsl.general.ExistsFilter;
import com.phonepe.commons.query.dsl.general.GenericFilter;
import com.phonepe.commons.query.dsl.general.InFilter;
import com.phonepe.commons.query.dsl.general.MissingFilter;
import com.phonepe.commons.query.dsl.general.NotEqualsFilter;
import com.phonepe.commons.query.dsl.general.NotInFilter;
import com.phonepe.commons.query.dsl.logical.AndFilter;
import com.phonepe.commons.query.dsl.logical.NotFilter;
import com.phonepe.commons.query.dsl.logical.OrFilter;
import com.phonepe.commons.query.dsl.numeric.BetweenFilter;
import com.phonepe.commons.query.dsl.numeric.GreaterEqualFilter;
import com.phonepe.commons.query.dsl.numeric.GreaterThanFilter;
import com.phonepe.commons.query.dsl.numeric.LessEqualFilter;
import com.phonepe.commons.query.dsl.numeric.LessThanFilter;
import com.phonepe.commons.query.dsl.numeric.NumericBinaryFilter;
import com.phonepe.commons.query.dsl.string.StringEndsWithFilter;
import com.phonepe.commons.query.dsl.string.StringRegexMatchFilter;
import com.phonepe.commons.query.dsl.string.StringStartsWithFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * This is a Json path based filter evaluator
 * A filter predicate visitor that will apply the filter, and tell whether it is true or false
 */
@Slf4j
@AllArgsConstructor
public class JsonPathFilterEvaluationEngine<C extends JsonEvalContext, F> implements FilterVisitor<Boolean> {

    public static final String BONSAI_FILTER_VALUES_DOCUMENT_LOG_STR = "[bonsai][{}] filter:{} values:{} document:{}";
    private static final TypeRef<List<Number>> NUMBER_TYPE_REF = new TypeRef<>() {
    };
    private static final TypeRef<List<String>> STRING_TYPE_REF = new TypeRef<>() {
    };
    private static final TypeRef<List<Object>> OBJECT_TYPE_REF = new TypeRef<>() {
    };
    protected final String entityId;

    protected final C context;

    private final Predicate<GenericFilterContext<C, F>> genericFilterHandler;

    private final F associatedEntity;

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
        List<Object> values = readContextValues(filter, OBJECT_TYPE_REF);
        Set<Object> valueSet = filter.getValues();
        return !valueInValueSet(values, valueSet);
    }

    @Override
    public Boolean visit(NotEqualsFilter filter) {
        return applyNoneMatch(filter, OBJECT_TYPE_REF, k -> k.equals(filter.getValue()));
    }

    @Override
    public Boolean visit(MissingFilter filter) {
        List<Object> values = context.documentContext().read(filter.getField(), OBJECT_TYPE_REF);
        if (log.isTraceEnabled()) {
            log.trace(BONSAI_FILTER_VALUES_DOCUMENT_LOG_STR, context.id(), filter.getField(), values,
                      context.documentContext().json());
        }
        return values == null || values.isEmpty() || values.stream().allMatch(Objects::isNull);
    }

    @Override
    public Boolean visit(InFilter filter) {
        List<Object> values = readContextValues(filter, OBJECT_TYPE_REF);
        Set<Object> valueSet = filter.getValues();
        return valueInValueSet(values, valueSet);
    }

    private Boolean valueInValueSet(List<Object> values, Set<Object> valueSet) {
        if (isEmpty(values)) {
            return false;
        }
        for (Object value : values) {
            if (value != null && valueSet.contains(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean visit(ExistsFilter filter) {
        List<Object> values = context.documentContext().read(filter.getField(), OBJECT_TYPE_REF);
        if (log.isTraceEnabled()) {
            log.trace(BONSAI_FILTER_VALUES_DOCUMENT_LOG_STR, context.id(), filter.getField(), values,
                      context.documentContext().json());
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
        /* todo create a small Pattern compiled cache, to avo   id compile every time */
        return applyAllMatchFilter(filter, STRING_TYPE_REF, k -> k.matches(filter.getValue()));
    }

    @Override
    public Boolean visit(GenericFilter filter) {
        final GenericFilterContext<C, F> genericFilterContext = new GenericFilterContext<>(filter, context, associatedEntity);
        return genericFilterHandler.test(genericFilterContext);
    }

    /// ////////////////////////////////////////////////////////////////////////////////////////////////////
    /// ///////////////////////////////////////  Helper Functions  /////////////////////////////////////////
    /// ////////////////////////////////////////////////////////////////////////////////////////////////////


    private <T> Boolean applyAllMatchFilter(Filter filter, TypeRef<List<T>> typeRef, Predicate<T> predicate) {
        List<T> values = readContextValues(filter, typeRef);
        if (isEmpty(values)) {
            return false;
        }
        boolean hasNonNullValue = false;
        for (T value : values) {
            if (value != null) {
                hasNonNullValue = true;
                if (!predicate.test(value)) {
                    return false;
                }
            }
        }
        return hasNonNullValue;
    }

    private <T> List<T> readContextValues(Filter filter, TypeRef<List<T>> typeRef) {
        List<T> values = context.documentContext().read(filter.getField(), typeRef);
        if (log.isTraceEnabled()) {
            log.trace(BONSAI_FILTER_VALUES_DOCUMENT_LOG_STR, context.id(), filter.getField(), values,
                      context.documentContext().json());
        }
        return values == null ? Collections.emptyList() : values;
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

    private <T> Boolean applyNoneMatch(Filter filter,
                                       @SuppressWarnings("SameParameterValue") TypeRef<List<T>> typeRef,
                                       Predicate<T> predicate) {
        List<T> nonNullValues = readContextValues(filter, typeRef);
        return isNotEmpty(nonNullValues) && nonNullValues.stream().filter(Objects::nonNull).noneMatch(predicate);
    }

    private <T> boolean isNotEmpty(List<T> nonNullValues) {
        return nonNullValues != null && !nonNullValues.isEmpty();
    }

    private <T> boolean isEmpty(List<T> nonNullValues) {
        return nonNullValues == null || nonNullValues.isEmpty();
    }
}
