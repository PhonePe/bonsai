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

package com.phonepe.commons.bonsai.core.vital.provided;

import com.phonepe.commons.bonsai.conditions.ConditionEngine;
import com.phonepe.commons.bonsai.core.vital.Context;
import com.phonepe.commons.bonsai.json.eval.GenericFilterContext;
import com.phonepe.commons.bonsai.json.eval.JsonPathFilterEvaluationEngine;
import com.phonepe.commons.bonsai.json.eval.TraceWrappedJsonPathFilterEvaluationEngine;
import com.phonepe.commons.bonsai.models.blocks.Edge;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;

/**
 * This is responsible for matching the {@link Edge}s filters against the {@link Context}
 * We are using the {@link JsonPathFilterEvaluationEngine} to evaluate if all {@link com.phonepe.commons.query.dsl.Filter}s
 * present in the {@link Edge} are true
 * If so, this {@link Edge} will return true, ie, the Context satisfies the {@link Edge}s criteria
 */
@Slf4j
public class VariationSelectorEngine<C extends Context> extends ConditionEngine<C, Edge, String> {

    private final Predicate<GenericFilterContext<C, String>> genericFilterHandler;

    public VariationSelectorEngine() {
        this(genericFilterContext -> true);
    }

    public VariationSelectorEngine(Predicate<GenericFilterContext<C, String>> genericFilterHandler) {
        this.genericFilterHandler = genericFilterHandler;
    }

    @Override
    public Boolean match(C context, Edge edge) {
        /* in case no document context is passed, we will not match the edge's filters */
        if (context.getDocumentContext() == null) {
            if (log.isDebugEnabled()) {
                log.debug("[bonsai][match][{}] no document context", edge.getEdgeIdentifier().getId());
            }
            return false;
        }
        return edge.getFilters()
                .stream()
                .allMatch(k -> {
                    final JsonPathFilterEvaluationEngine<C, String> filterVisitor = log.isTraceEnabled()
                            ?
                            new TraceWrappedJsonPathFilterEvaluationEngine<>(edge.getEdgeIdentifier().getId(), context,
                                    genericFilterHandler)
                            : new JsonPathFilterEvaluationEngine<>(edge.getEdgeIdentifier().getId(), context,
                            genericFilterHandler, null);
                    return k.accept(filterVisitor);
                });
    }

    @Override
    public Boolean match(C context, Edge edge, String associatedKey) {
        /* in case no document context is passed, we will not match the edge's filters */
        if (context.getDocumentContext() == null) {
            if (log.isDebugEnabled()) {
                log.debug("[bonsai][match][{}] no document context", edge.getEdgeIdentifier().getId());
            }
            return false;
        }
        return edge.getFilters()
                .stream()
                .allMatch(k -> {
                    final JsonPathFilterEvaluationEngine<C, String> filterVisitor = log.isTraceEnabled()
                                                                                    ?
                                                                                    new TraceWrappedJsonPathFilterEvaluationEngine<>(edge.getEdgeIdentifier().getId(), context,
                                                                                                                                     genericFilterHandler, associatedKey)
                                                                                    : new JsonPathFilterEvaluationEngine<>(edge.getEdgeIdentifier().getId(), context,
                                                                                                                           genericFilterHandler, associatedKey);
                    return k.accept(filterVisitor);
                });
    }
}
