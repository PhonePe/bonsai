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

package com.phonepe.commons.bonsai.json.eval;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.phonepe.commons.query.dsl.Filter;
import com.phonepe.commons.query.dsl.general.GenericFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class JsonPathEvaluationTest {

    private final ObjectExtractor objectExtractor = new ObjectExtractor();

    @Test
    void testJsonPathEval() throws IOException {
        JsonPathSetup.setup();
        Map object = objectExtractor.getObject("sample.json", Map.class);
        JsonPathFilterEvaluationEngine<JsonEvalContext, String> eval
                = new JsonPathFilterEvaluationEngine<>("temp", () -> JsonPath.parse(object),
                genericFilterContext -> true, null);
        List<Filter> filters = objectExtractor.getObject("filterList1.json", new TypeReference<>() {
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
        JsonPathFilterEvaluationEngine<JsonEvalContext, String> eval
                = new TraceWrappedJsonPathFilterEvaluationEngine<>("temp", () -> JsonPath.parse(object),
                genericFilterContext -> true);
        List<Filter> filters = objectExtractor.getObject("filterList1.json", new TypeReference<>() {
        });
        long count = filters.stream()
                .filter(filter -> filter.accept(eval))
                .count();
        Assertions.assertEquals(8, count);
    }

    @Test
    void testJsonPathEval_WithEntityMetadata() throws IOException {
        JsonPathSetup.setup();
        Map<String, Object> object = objectExtractor.getObject("sample.json", Map.class);
        GenericFilter genericFilter = new GenericFilter(); // The logic is in the handler.
        String expectedKey = "user-segment-A";

        // 1. Create a handler that checks the entityMetadata.
        Predicate<GenericFilterContext<JsonEvalContext, String>> handler =
                ctx -> expectedKey.equals(ctx.getEntityMetadata());

        // 2. Test the positive case: engine is created with the correct key.
        JsonPathFilterEvaluationEngine<JsonEvalContext, String> evalWithCorrectKey =
                new JsonPathFilterEvaluationEngine<>(
                        "test-entity",
                        () -> JsonPath.parse(object),
                        handler,
                        expectedKey
                );

        boolean result1 = genericFilter.accept(evalWithCorrectKey);
        Assertions.assertTrue(result1, "Filter should pass when entityMetadata matches.");

        // 3. Test the negative case: engine is created with an incorrect key.
        JsonPathFilterEvaluationEngine<JsonEvalContext, String> evalWithWrongKey =
                new JsonPathFilterEvaluationEngine<>(
                        "test-entity",
                        () -> JsonPath.parse(object),
                        handler,
                        "wrong-key"
                );

        boolean result2 = genericFilter.accept(evalWithWrongKey);
        Assertions.assertFalse(result2, "Filter should fail when entityMetadata does not match.");
    }
}
