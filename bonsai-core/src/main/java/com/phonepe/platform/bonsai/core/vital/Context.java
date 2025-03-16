/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
