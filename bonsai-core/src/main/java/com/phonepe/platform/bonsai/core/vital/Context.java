package com.phonepe.platform.bonsai.core.vital;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jayway.jsonpath.DocumentContext;
import com.phonepe.platform.bonsai.json.eval.JsonEvalContext;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

/**
 * A simple Context for evaluation
 *
 * @author tushar.naik
 * @version 1.0  27/07/18 - 12:42 AM
 */
@Data
public class Context implements JsonEvalContext {
    @JsonIgnore
    private DocumentContext documentContext;
    private Map<String, Knot> preferences;
    private String id = UUID.randomUUID().toString();

    @Builder
    public Context(DocumentContext documentContext,
                   Map<String, Knot> preferences) {
        this.documentContext = documentContext;
        this.preferences = preferences;
    }

    @Override
    public DocumentContext documentContext() {
        return documentContext;
    }

    @Override
    public String id() {
        return id;
    }
}
