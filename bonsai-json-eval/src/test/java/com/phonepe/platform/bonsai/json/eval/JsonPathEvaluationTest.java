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

package com.phonepe.platform.bonsai.json.eval;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.phonepe.platform.query.dsl.Filter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonPathEvaluationTest {

    private final ObjectExtractor objectExtractor = new ObjectExtractor();

    @Test
    void testJsonPathEval() throws IOException {
        JsonPathSetup.setup();
        Map object = objectExtractor.getObject("sample.json", Map.class);
        JsonPathFilterEvaluationEngine<JsonEvalContext> eval
                = new JsonPathFilterEvaluationEngine<>("temp", () -> JsonPath.parse(object),
                genericFilterContext -> true);
        List<Filter> filters = objectExtractor.getObject("filterList1.json", new TypeReference<List<Filter>>() {
        });
        long count = filters.stream()
                .filter(filter -> filter.accept(eval))
                .count();
        Assertions.assertEquals(8, count);
    }

    @Test
    void testJsonPathEvalWithTrace() throws IOException {
        JsonPathSetup.setup();
        Map object = objectExtractor.getObject("sample.json", Map.class);
        JsonPathFilterEvaluationEngine<JsonEvalContext> eval
                = new TraceWrappedJsonPathFilterEvaluationEngine<>("temp", () -> JsonPath.parse(object),
                genericFilterContext -> true);
        List<Filter> filters = objectExtractor.getObject("filterList1.json", new TypeReference<List<Filter>>() {
        });
        long count = filters.stream()
                .filter(filter -> filter.accept(eval))
                .count();
        Assertions.assertEquals(8, count);
    }
}
