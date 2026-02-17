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

import com.jayway.jsonpath.DocumentContext;
import com.phonepe.commons.bonsai.json.eval.PathExpression.Adjustment;
import com.phonepe.commons.bonsai.json.eval.PathExpression.Operation;
import com.phonepe.commons.query.dsl.Filter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.DoubleStream;
import javax.naming.OperationNotSupportedException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
public class PathExpressionEval {

    private BonsaiHopeEngine hopeEngine;

    private PathExpression pathExpression;

    public Pair<String, Object> eval(DocumentContext context) {
        final String key = pathExpression.getKey();
        final List<Filter> filters = pathExpression.getFilters();
        if (filters != null && !filters.isEmpty() &&
                !filters.stream()
                        .allMatch(k -> k.accept(
                                new JsonPathFilterEvaluationEngine<>(key, () -> context, genericFilterContext -> true,
                                        key, hopeEngine)))) {
            return null;
        }
        final Object value = pathExpression.getValue();
        if (value != null) {
            return new Pair<>(key, value);
        }

        final String path = pathExpression.getPath();
        final Operation operation = pathExpression.getOperation();
        try {
            if (operation == null) {
                List<Object> values = context.read(path, PathExpression.OBJECT_TYPE_REF);
                List<Object> nonNullValues =
                        values == null ? null : values.stream().filter(Objects::nonNull).toList();
                if (nonNullValues == null || nonNullValues.isEmpty()) {
                    return null;
                }
                return new Pair<>(key, reValue(pathExpression.isMultivalued() ? nonNullValues : nonNullValues.get(0)));
            } else {
                return numberOperation(context);
            }
        } catch (Exception e) {
            log.error("[bonsai] Error while evaluating expression: " + this, e);
            return null;
        }
    }

    private Object reValue(Object oldValue) {
        final List<Adjustment> adjustments = pathExpression.getAdjustments();
        if (adjustments == null || adjustments.isEmpty()) {
            return oldValue;
        }
        return reValue(((Number) oldValue).doubleValue());
    }

    private double reValue(double oldValue) {
        final List<Adjustment> adjustments = pathExpression.getAdjustments();
        if (adjustments == null || adjustments.isEmpty()) {
            return oldValue;
        }
        for (Adjustment adjustment : adjustments) {
            oldValue = adjustment.reValue(oldValue);
        }
        return oldValue;
    }

    private Pair<String, Object> numberOperation(DocumentContext context) throws OperationNotSupportedException {
        List<Number> values = context.read(pathExpression.getPath(), PathExpression.NUMBER_TYPE_REF);
        if (values == null || values.isEmpty() || values.get(0) == null) {
            return null;
        }
        final String key = pathExpression.getKey();
        return switch (pathExpression.getOperation()) {
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
}
