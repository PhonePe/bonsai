package com.phonepe.platform.bonsai.json.eval;

import com.jayway.jsonpath.DocumentContext;
import com.phonepe.platform.query.dsl.general.GenericFilter;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author utsab.b on 30/10/19.
 */
@Data
@AllArgsConstructor
public class GenericFilterContext {
    private GenericFilter genericFilter;
    private DocumentContext context;
}
