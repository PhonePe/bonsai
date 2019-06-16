package com.phonepe.platform.bonsai.core.vital;

import com.jayway.jsonpath.DocumentContext;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
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
public class Context {
    private DocumentContext documentContext;
    private Map<String, Knot> preferences;
}
