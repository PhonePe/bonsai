package com.phonepe.platform.bonsai.json.eval;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.TypeRef;
import com.phonepe.platform.query.dsl.Filter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.naming.OperationNotSupportedException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * @author tushar.naik
 * @version 1.0  26/05/17 - 6:59 PM
 */
@Data
@NoArgsConstructor
@Slf4j
public class PathExpression {

    private static final TypeRef<List<Number>> NUMBER_TYPE_REF = new TypeRef<List<Number>>() {
    };
    private static final TypeRef<List<Object>> OBJECT_TYPE_REF = new TypeRef<List<Object>>() {
    };

    @JsonProperty
    private String key;

    @JsonProperty
    private String path;

    /* is the value that is being looked at, multivalued or not */
    private boolean multivalued = false;

    /* operations done on multi-valued entities from path */
    @JsonProperty
    private Operation operation;

    /* adjustments done on the value obtained from path */
    @JsonProperty
    private List<Adjustment> adjustments;

    private String type;

    /* filters evaluated if not null */
    private List<Filter> filters;

    /* if value is passed and all filters pass, this will be returned (applicable only when adjustments and path arent present) */
    private Object value;

    public Pair<String, Object> eval(DocumentContext context) {
        if (filters != null && !filters.isEmpty() &&
                !filters.stream().allMatch(k -> k.accept(new JsonPathFilterEvaluationEngine(context)))) {
            return null;
        }
        if (value != null) {
            return new Pair<>(key, value);
        }

        try {
            if (operation == null) {
                List<Object> values = context.read(path, OBJECT_TYPE_REF);
                List<Object> nonNullValues =
                        values == null ? null : values.stream().filter(Objects::nonNull)
                                                      .collect(Collectors.toList());
                if (nonNullValues == null || nonNullValues.isEmpty()) {
                    return null;
                }
                return new Pair<>(key, reValue(multivalued ? nonNullValues : nonNullValues.get(0)));
            } else {
                List<Number> values = context.read(path, NUMBER_TYPE_REF);
                if (values == null || values.isEmpty() || values.get(0) == null) {
                    return null;
                }
                switch (operation) {
                    case sum:
                        return new Pair<>(key, reValue(getDoubleStream(values).sum()));
                    case average:
                        return new Pair<>(key, reValue(getDoubleStream(values).average().orElse(0)));
                    case max:
                        return new Pair<>(key, reValue(getDoubleStream(values).max().orElse(0)));
                    case min:
                        return new Pair<>(key, reValue(getDoubleStream(values).min().orElse(0)));
                    case length:
                        return new Pair<>(key, reValue(getDoubleStream(values).count()));
                    case padTimestamp:
                        return new Pair<>(key, Utils.leftPad(String.valueOf(values.get(0)), 20, '0'));
                    case convertToDate:
                        return new Pair<>(key, new Date(values.get(0).longValue()));
                    default:
                        throw new OperationNotSupportedException("Operation not supported: " + this.toString());
                }
            }
        } catch (Exception e) {
            log.error("Error while evaluating expression: " +  toString(), e);
            return null;
        }
    }

    public static class Adjustment {

        @JsonProperty
        Type type;

        @JsonProperty
        Number value;

        enum Type {
            add,
            divide,
            subtract,
            multiply,
            sqrt,
            ceil,
            floor,
            pow
        }

        public double reValue(Number initialValue) {
            switch (type) {
                case add:
                    return initialValue.doubleValue() + value.doubleValue();
                case divide:
                    return initialValue.doubleValue() / value.doubleValue();
                case subtract:
                    return initialValue.doubleValue() - value.doubleValue();
                case multiply:
                    return initialValue.doubleValue() * value.doubleValue();
                case sqrt:
                    return Math.sqrt(initialValue.doubleValue());
                case ceil:
                    return Math.ceil(initialValue.doubleValue());
                case floor:
                    return Math.floor(initialValue.doubleValue());
                case pow:
                    return Math.pow(initialValue.doubleValue(), value.doubleValue());
                default:
                    throw new RuntimeException("Adjustment not supported: " + this.toString());
            }
        }
    }

    public enum Operation {
        sum,
        average,
        max,
        min,
        length,
        padTimestamp,
        convertToDate
    }

    public Pair<String, Object> evaluate(DocumentContext context) {
        com.jayway.jsonpath.Filter filter = null;
        if (filters != null && !filters.isEmpty()) {
            filter = filters.stream()
                            .map(k -> k.accept(new JsonPathFilterBuilder()))
                            .reduce(com.jayway.jsonpath.Filter::or)
                            .orElse(null);
            if (!filters.stream().allMatch(k -> k.accept(new JsonPathFilterEvaluationEngine(context)))) {
                return null;
            }
        }
        try {
            if (operation == null) {
                List<Object> values;
                if (filter == null) {
                    values = context.read(path, OBJECT_TYPE_REF);
                } else {
                    values = context.read(path, filter);
                }
                List<Object> nonNullValues =
                        values == null ? null : values.stream().filter(Objects::nonNull)
                                                      .collect(Collectors.toList());
                if (nonNullValues == null || nonNullValues.isEmpty()) {
                    return null;
                }
                return new Pair<>(key, reValue(multivalued ? nonNullValues : nonNullValues.get(0)));
            } else {
                List<Number> values = context.read(path, NUMBER_TYPE_REF);
                if (values == null || values.isEmpty() || values.get(0) == null) {
                    return null;
                }
                switch (operation) {
                    case sum:
                        return new Pair<>(key, reValue(getDoubleStream(values).sum()));
                    case average:
                        return new Pair<>(key, reValue(getDoubleStream(values).average().orElse(0)));
                    case max:
                        return new Pair<>(key, reValue(getDoubleStream(values).max().orElse(0)));
                    case min:
                        return new Pair<>(key, reValue(getDoubleStream(values).min().orElse(0)));
                    case length:
                        return new Pair<>(key, reValue(getDoubleStream(values).count()));
                    default:
                        throw new OperationNotSupportedException("Operation not supported: " + this.toString());
                }
            }
        } catch (Exception e) {
            log.error("Error while evaluating expression: " + this, toString(), e);
            return null;
        }
    }

    private DoubleStream getDoubleStream(List<Number> values) {
        return values.stream().mapToDouble(Number::doubleValue);
    }

    private Object reValue(Object oldValue) {
        if (adjustments == null || adjustments.isEmpty()) {
            return oldValue;
        }
        return reValue(((Number) oldValue).doubleValue());
    }

    public double reValue(double oldValue) {
        if (adjustments == null || adjustments.isEmpty()) {
            return oldValue;
        }
        for (Adjustment adjustment : adjustments) {
            oldValue = adjustment.reValue(oldValue);
        }
        return oldValue;
    }

    @Override
    public String toString() {
        return "[" + "key:'" + key + '\'' +
                ", path:'" + path + '\'' +
                ", adjustments:'" + adjustments + '\'' +
                ", operation:" + operation +
                ']';
    }
}
