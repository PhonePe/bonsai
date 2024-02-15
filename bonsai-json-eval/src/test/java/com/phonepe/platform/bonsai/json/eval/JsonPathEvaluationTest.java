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
