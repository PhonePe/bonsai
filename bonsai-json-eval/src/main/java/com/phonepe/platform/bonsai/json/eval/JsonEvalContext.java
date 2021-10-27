package com.phonepe.platform.bonsai.json.eval;

import com.jayway.jsonpath.DocumentContext;

import java.util.UUID;

@FunctionalInterface
public interface JsonEvalContext {
    DocumentContext documentContext();

    default String id() {
        return UUID.randomUUID().toString();
    }
}
