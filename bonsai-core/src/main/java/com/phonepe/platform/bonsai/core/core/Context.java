package com.phonepe.platform.bonsai.core.core;

import com.jayway.jsonpath.DocumentContext;
import com.phonepe.platform.bonsai.models.KeyNode;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  27/07/18 - 12:42 AM
 */
@Data
@Builder
public class Context {
    private DocumentContext documentContext;
    private Map<String, KeyNode> preferences;
}
