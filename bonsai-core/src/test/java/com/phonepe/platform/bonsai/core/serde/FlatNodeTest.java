package com.phonepe.platform.bonsai.core.serde;

import com.phonepe.platform.bonsai.core.ObjectExtractor;
import com.phonepe.platform.bonsai.models.model.FlatNode;
import com.phonepe.platform.bonsai.models.model.ValueFlatNode;
import com.phonepe.platform.bonsai.models.value.BooleanValue;
import com.phonepe.platform.bonsai.models.value.JsonValue;
import com.phonepe.platform.bonsai.models.value.NumberValue;
import com.phonepe.platform.bonsai.models.value.StringValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class FlatNodeTest {
    private ObjectExtractor objectExtractor = new ObjectExtractor();

    @Test
    void testJsonValueFlatNode() throws IOException {
        FlatNode flatNode = objectExtractor.getObject("serde/jsonValue.json", FlatNode.class);
        Assertions.assertEquals(FlatNode.FlatNodeType.VALUE, flatNode.getType());
        Assertions.assertEquals("json", ((JsonValue) ((ValueFlatNode) flatNode).getValue()).getValue().get("some").asText());
    }

    @Test
    void testNumberValueFlatNode() throws IOException {
        FlatNode flatNode = objectExtractor.getObject("serde/numberValue.json", FlatNode.class);
        Assertions.assertEquals(FlatNode.FlatNodeType.VALUE, flatNode.getType());
        Assertions.assertEquals(2, ((NumberValue) ((ValueFlatNode) flatNode).getValue()).getValue());
    }

    @Test
    void testStringValueFlatNode() throws IOException {
        FlatNode flatNode = objectExtractor.getObject("serde/stringValue.json", FlatNode.class);
        Assertions.assertEquals(FlatNode.FlatNodeType.VALUE, flatNode.getType());
        Assertions.assertEquals("hello", ((StringValue) ((ValueFlatNode) flatNode).getValue()).getValue());
    }

    @Test
    void testBooleanValueFlatNode() throws IOException {
        FlatNode flatNode = objectExtractor.getObject("serde/booleanValue.json", FlatNode.class);
        Assertions.assertEquals(FlatNode.FlatNodeType.VALUE, flatNode.getType());
        Assertions.assertEquals(true, ((BooleanValue) ((ValueFlatNode) flatNode).getValue()).isValue());
    }
}