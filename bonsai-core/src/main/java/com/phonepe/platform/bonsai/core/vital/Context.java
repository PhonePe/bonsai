package com.phonepe.platform.bonsai.core.vital;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jayway.jsonpath.DocumentContext;
import com.phonepe.platform.bonsai.json.eval.JsonEvalContext;
import com.phonepe.platform.bonsai.models.BonsaiConstants;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.slf4j.MDC;

import java.util.Map;

/**
 * A simple Context for evaluation
 */
@Data
@Builder
@AllArgsConstructor
public class Context implements JsonEvalContext {
    @JsonIgnore
    private DocumentContext documentContext;
    private Map<String, Knot> preferences;

    @Override
    public DocumentContext documentContext() {
        return documentContext;
    }

    @Override
    public String id() {
        return MDC.get(BonsaiConstants.EVALUATION_ID);
    }
}
