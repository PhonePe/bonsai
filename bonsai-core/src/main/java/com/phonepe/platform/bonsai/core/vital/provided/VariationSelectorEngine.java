package com.phonepe.platform.bonsai.core.vital.provided;

import com.phonepe.folios.condition.engine.ConditionEngine;
import com.phonepe.platform.bonsai.core.vital.Context;
import com.phonepe.platform.bonsai.json.eval.GenericFilterContext;
import com.phonepe.platform.bonsai.json.eval.JsonPathFilterEvaluationEngine;
import com.phonepe.platform.bonsai.json.eval.TraceWrappedJsonPathFilterEvaluationEngine;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;

/**
 * This is responsible for matching the {@link Edge}s filters against the {@link Context}
 * We are using the {@link JsonPathFilterEvaluationEngine} to evaluate if all {@link com.phonepe.platform.query.dsl.Filter}s
 * present in the {@link Edge} are true
 * If so, this {@link Edge} will return true, ie, the Context satisfies the {@link Edge}s criteria
 */
@Slf4j
public class VariationSelectorEngine<C extends Context> extends ConditionEngine<C, Edge> {

    private final Predicate<GenericFilterContext<C>> genericFilterHandler;

    public VariationSelectorEngine() {
        this(genericFilterContext -> true);
    }

    public VariationSelectorEngine(Predicate<GenericFilterContext<C>> genericFilterHandler) {
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
                    final JsonPathFilterEvaluationEngine<C> filterVisitor = log.isTraceEnabled()
                            ? new TraceWrappedJsonPathFilterEvaluationEngine<>(edge.getEdgeIdentifier().getId(), context, genericFilterHandler)
                            : new JsonPathFilterEvaluationEngine<>(edge.getEdgeIdentifier().getId(), context, genericFilterHandler);
                    return k.accept(filterVisitor);
                });
    }
}
