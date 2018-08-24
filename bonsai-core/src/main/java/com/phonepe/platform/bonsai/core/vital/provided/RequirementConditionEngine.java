package com.phonepe.platform.bonsai.core.vital.provided;

import com.phonepe.folios.condition.engine.ConditionEngine;
import com.phonepe.platform.bonsai.core.query.EvaluateJsonFilter;
import com.phonepe.platform.bonsai.core.vital.Context;
import com.phonepe.platform.bonsai.core.vital.provided.model.AtomicEdge;

/**
 * This is responsible for matching the {@link AtomicEdge}s conditions (filters) against the {@link Context}
 * We are using the {@link EvaluateJsonFilter} to evaluate if all {@link com.phonepe.platform.bonsai.core.query.filter.Filter}s
 * present in the {@link AtomicEdge} are true
 * If so, this {@link AtomicEdge} will return true, ie, the Context satisfies the {@link AtomicEdge}s criteria
 *
 * @author tushar.naik
 * @version 1.0  13/07/18 - 11:43 AM
 */
public class RequirementConditionEngine extends ConditionEngine<Context, AtomicEdge> {

    @Override
    public Boolean match(Context context, AtomicEdge edge) {
        /* in case no document context is passed, we will not match the edge's conditions */
        if (context.getDocumentContext() == null) {
            return false;
        }
        return edge.getConditions()
                   .stream()
                   .allMatch(k -> k.accept(new EvaluateJsonFilter(context.getDocumentContext())));
    }
}
