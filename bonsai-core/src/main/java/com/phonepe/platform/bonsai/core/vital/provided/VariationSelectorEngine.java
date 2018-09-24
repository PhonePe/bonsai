package com.phonepe.platform.bonsai.core.vital.provided;

import com.phonepe.folios.condition.engine.ConditionEngine;
import com.phonepe.platform.bonsai.core.variation.FilterEvaluationEngine;
import com.phonepe.platform.bonsai.core.vital.Context;
import com.phonepe.platform.bonsai.core.vital.blocks.Edge;

/**
 * This is responsible for matching the {@link Edge}s filters (filters) against the {@link Context}
 * We are using the {@link FilterEvaluationEngine} to evaluate if all {@link com.phonepe.platform.bonsai.core.variation.filter.Filter}s
 * present in the {@link Edge} are true
 * If so, this {@link Edge} will return true, ie, the Context satisfies the {@link Edge}s criteria
 *
 * @author tushar.naik
 * @version 1.0  13/07/18 - 11:43 AM
 */
public class VariationSelectorEngine extends ConditionEngine<Context, Edge> {

    @Override
    public Boolean match(Context context, Edge edge) {
        /* in case no document context is passed, we will not match the edge's filters */
        if (context.getDocumentContext() == null) {
            return false;
        }
        return edge.getFilters()
                   .stream()
                   .allMatch(k -> k.accept(new FilterEvaluationEngine(context.getDocumentContext())));
    }
}
