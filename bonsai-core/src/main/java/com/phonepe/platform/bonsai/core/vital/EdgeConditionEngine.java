package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.folios.condition.engine.ConditionEngine;
import com.phonepe.platform.bonsai.core.query.EvaluateJsonFilter;

/**
 * This is responsible for matching the {@link Edge}s conditions (filters) against the {@link Context}
 * We are using the {@link EvaluateJsonFilter} to evaluate if all {@link com.phonepe.platform.bonsai.core.query.filter.Filter}s
 * present in the {@link Edge} are true
 * If so, this {@link Edge} will return true, ie, the Context satisfies the {@link Edge}s criteria
 *
 * @author tushar.naik
 * @version 1.0  13/07/18 - 11:43 AM
 */
public class EdgeConditionEngine extends ConditionEngine<Context, Edge> {

    @Override
    public Boolean match(Context context, Edge edge) {
        /* in case no document context is passed, we will not match the edge's conditions */
        if (context.getDocumentContext() == null) {
            return false;
        }
        return edge.getConditions()
                   .stream()
                   .allMatch(k -> k.accept(new EvaluateJsonFilter(context.getDocumentContext())));
    }
}
