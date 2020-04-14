package com.phonepe.platform.bonsai.json.eval;

import com.jayway.jsonpath.DocumentContext;

/**
 * @author tushar.naik
 * @version 1.0  09/04/20 - 7:39 PM
 */
@FunctionalInterface
public interface JsonEvalContext {
    DocumentContext documentContext();
}
