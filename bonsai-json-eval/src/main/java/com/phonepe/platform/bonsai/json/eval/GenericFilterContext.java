package com.phonepe.platform.bonsai.json.eval;

import com.phonepe.platform.query.dsl.general.GenericFilter;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenericFilterContext<C extends JsonEvalContext> {
    private GenericFilter genericFilter;
    private C context;
}
