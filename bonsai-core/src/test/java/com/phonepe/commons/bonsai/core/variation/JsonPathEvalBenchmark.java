package com.phonepe.commons.bonsai.core.variation;

import com.jayway.jsonpath.TypeRef;
import com.phonepe.commons.bonsai.json.eval.Parsers;
import com.phonepe.commons.bonsai.core.vital.Context;
import com.phonepe.commons.query.dsl.Filter;
import com.phonepe.commons.query.dsl.general.NotInFilter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;


@State(Scope.Benchmark)
public class JsonPathEvalBenchmark {
    private static final TypeRef<List<Object>> OBJECT_TYPE_REF = new TypeRef<>() {
    };

    private static final Context CONTEXT = Context.builder()
            .documentContext(Parsers.parse(Map.of("E", Integer.MAX_VALUE)))
            .build();
    private static final NotInFilter NOT_IN_FILTER = NotInFilter.builder()
            .field("$.E")
            .values(Set.of(1, 2, 3)).build();

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }

    private <T> List<T> nonNullValues(Context context, Filter filter, TypeRef<List<T>> typeRef) {
        List<T> values = context.documentContext().read(filter.getField(), typeRef);
        return values == null ? Collections.emptyList() : values.stream().filter(Objects::nonNull).toList();
    }

    private <T> Boolean applyAllMatchFilter(Context context, Filter filter, TypeRef<List<T>> typeRef, Predicate<T> predicate) {
        List<T> nonNullValues = nonNullValues(context, filter, typeRef);
        return isNotEmpty(nonNullValues) && nonNullValues.stream().allMatch(predicate);
    }

    private <T> boolean isNotEmpty(List<T> nonNullValues) {
        return nonNullValues != null && !nonNullValues.isEmpty();
    }

    private <T> List<T> readContextValues(Context context, Filter filter, TypeRef<List<T>> typeRef) {
        List<T> values = context.documentContext().read(filter.getField(), typeRef);
        return values == null ? Collections.emptyList() : values;
    }

    private <T> Boolean applyAllMatchFilter2(Context context, Filter filter, TypeRef<List<T>> typeRef, Predicate<T> predicate) {
        List<T> values = readContextValues(context, filter, typeRef);
        if (!isNotEmpty(values)) {
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

    @Benchmark
    @Fork(value = 1)
    @Warmup(iterations = 3)
    public Boolean olderImpl() {
        return applyAllMatchFilter(CONTEXT,
                                   NOT_IN_FILTER,
                                   OBJECT_TYPE_REF,
                                   Objects::nonNull);
    }

    @Benchmark
    @Fork(value = 1)
    @Warmup(iterations = 3)
    public Boolean newerImpl() {
        return applyAllMatchFilter2(CONTEXT,
                                    NOT_IN_FILTER,
                                    OBJECT_TYPE_REF,
                                    Objects::nonNull);
    }
}