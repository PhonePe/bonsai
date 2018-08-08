package com.phonepe.platform.bonsai.core.core;

import com.phonepe.folios.condition.engine.ConditionEngine;
import com.phonepe.platform.bonsai.core.query.EvaluateJsonFilter;

/**
 * @author tushar.naik
 * @version 1.0  13/07/18 - 11:43 AM
 */
public class EdgeConditionEngine extends ConditionEngine<Context, Edge> {

    @Override
    public Boolean match(Context context, Edge edge) {
        return edge.getConditions()
                   .stream()
                   .allMatch(k -> k.accept(new EvaluateJsonFilter(context.getDocumentContext())));
    }
}
