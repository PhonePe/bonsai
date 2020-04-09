package com.phonepe.platform.bonsai.core.vital;

import com.jayway.jsonpath.DocumentContext;
import com.phonepe.platform.bonsai.json.eval.JsonEvalContext;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * A simple Context for evaluation
 * @author tushar.naik
 * @version 1.0  27/07/18 - 12:42 AM
 */
@Data
@Builder
@AllArgsConstructor
public class Context implements JsonEvalContext {
    private DocumentContext documentContext;
    private Map<String, Knot> preferences;

    @Override
    public DocumentContext documentContext() {
        return documentContext;
    }
}
