package com.phonepe.platform.bonsai.json.eval;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jayway.jsonpath.JsonPath;
import com.phonepe.platform.query.dsl.Filter;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  23/09/19 - 7:39 PM
 */
public class JsonPathEvaluationTest {

    private ObjectExtractor objectExtractor= new ObjectExtractor();

    @Test
    public void testJsonPathEval() throws IOException {
        JsonPathSetup.setup();
        Map object = objectExtractor.getObject("sample.json", Map.class);
        JsonPathFilterEvaluationEngine eval = new JsonPathFilterEvaluationEngine(JsonPath.parse(object));
        List<Filter> filters = objectExtractor.getObject("filterList1.json", new TypeReference<List<Filter>>() {
        });
        long count = filters.stream()
                            .filter(filter -> filter.accept(eval))
                            .count();
        Assert.assertEquals( 7, count);
    }
}
