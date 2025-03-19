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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.TypeRef;
import com.phonepe.commons.query.dsl.Filter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.naming.OperationNotSupportedException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.DoubleStream;

@Data
@NoArgsConstructor
@Slf4j
public class PathExpression {

    private static final TypeRef<List<Number>> NUMBER_TYPE_REF = new TypeRef<>() {
    };
    private static final TypeRef<List<Object>> OBJECT_TYPE_REF = new TypeRef<>() {
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
                !filters.stream()
                        .allMatch(k -> k.accept(new JsonPathFilterEvaluationEngine<>(key, () -> context,
                                genericFilterContext -> true)))) {
            return null;
        }
        if (value != null) {
            return new Pair<>(key, value);
        }

        try {
            if (operation == null) {
                List<Object> values = context.read(path, OBJECT_TYPE_REF);
                List<Object> nonNullValues =
                        values == null ? null : values.stream().filter(Objects::nonNull).toList();
                if (nonNullValues == null || nonNullValues.isEmpty()) {
                    return null;
                }
                return new Pair<>(key, reValue(multivalued ? nonNullValues : nonNullValues.get(0)));
            } else {
                return numberOperation(context);
            }
        } catch (Exception e) {
            log.error("[bonsai] Error while evaluating expression: " + this, e);
            return null;
        }
    }

    private Pair<String, Object> numberOperation(DocumentContext context) throws OperationNotSupportedException {
        List<Number> values = context.read(path, NUMBER_TYPE_REF);
        if (values == null || values.isEmpty() || values.get(0) == null) {
            return null;
        }
        return switch (operation) {
            case SUM -> new Pair<>(key, reValue(getDoubleStream(values).sum()));
            case AVERAGE -> new Pair<>(key, reValue(getDoubleStream(values).average().orElse(0)));
            case MAX -> new Pair<>(key, reValue(getDoubleStream(values).max().orElse(0)));
            case MIN -> new Pair<>(key, reValue(getDoubleStream(values).min().orElse(0)));
            case LENGTH -> new Pair<>(key, reValue(getDoubleStream(values).count()));
            case PAD_TIMESTAMP -> new Pair<>(key, Utils.leftPad(String.valueOf(values.get(0)), 20, '0'));
            case CONVERT_TO_DATE -> new Pair<>(key, new Date(values.get(0).longValue()));
        };
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

    private double reValue(double oldValue) {
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

    public enum Operation {
        SUM,
        AVERAGE,
        MAX,
        MIN,
        LENGTH,
        PAD_TIMESTAMP,
        CONVERT_TO_DATE
    }

    public static class Adjustment {

        @JsonProperty
        Type type;

        @JsonProperty
        Number value;

        public double reValue(Number initialValue) {
            return switch (type) {
                case ADD -> initialValue.doubleValue() + value.doubleValue();
                case DIVIDE -> initialValue.doubleValue() / value.doubleValue();
                case SUBTRACT -> initialValue.doubleValue() - value.doubleValue();
                case MULTIPLY -> initialValue.doubleValue() * value.doubleValue();
                case SQRT -> Math.sqrt(initialValue.doubleValue());
                case CEIL -> Math.ceil(initialValue.doubleValue());
                case FLOOR -> Math.floor(initialValue.doubleValue());
                case POW -> Math.pow(initialValue.doubleValue(), value.doubleValue());
                default -> throw new UnsupportedOperationException("Adjustment not supported: " + this.toString());
            };
        }

        enum Type {
            ADD,
            DIVIDE,
            SUBTRACT,
            MULTIPLY,
            SQRT,
            CEIL,
            FLOOR,
            POW
        }
    }
}
