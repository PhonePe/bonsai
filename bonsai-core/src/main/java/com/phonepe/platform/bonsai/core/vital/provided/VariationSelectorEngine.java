package com.phonepe.platform.bonsai.core.vital.provided;

import com.phonepe.folios.condition.engine.ConditionEngine;
import com.phonepe.platform.bonsai.core.vital.Context;
import com.phonepe.platform.bonsai.json.eval.GenericFilterContext;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.json.eval.JsonPathFilterEvaluationEngine;


import java.util.function.Function;

/**
 * This is responsible for matching the {@link Edge}s filters against the {@link Context}
 * We are using the {@link JsonPathFilterEvaluationEngine} to evaluate if all {@link com.phonepe.platform.query.dsl.Filter}s
 * present in the {@link Edge} are true
 * If so, this {@link Edge} will return true, ie, the Context satisfies the {@link Edge}s criteria
 *
 * @author tushar.naik
 * @version 1.0  13/07/18 - 11:43 AM
 */
public class VariationSelectorEngine<C extends Context> extends ConditionEngine<C, Edge> {

    private final Function<GenericFilterContext, Boolean> genericFilterHandler;

    public VariationSelectorEngine() {
        this(genericFilterContext -> true);
    }

    public VariationSelectorEngine(Function<GenericFilterContext, Boolean> genericFilterHandler) {
        this.genericFilterHandler = genericFilterHandler;
    }

    @Override
    public Boolean match(C context, Edge edge) {
        /* in case no document context is passed, we will not match the edge's filters */
        if (context.getDocumentContext() == null) {
            return false;
        }
        return edge.getFilters()
                .stream()
                .allMatch(k -> k.accept(new JsonPathFilterEvaluationEngine(context.getDocumentContext(), genericFilterHandler)));
    }
}
