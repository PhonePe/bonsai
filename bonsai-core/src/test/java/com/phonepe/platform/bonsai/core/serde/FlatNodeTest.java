package com.phonepe.platform.bonsai.core.serde;

import com.phonepe.platform.bonsai.core.ObjectExtractor;
import com.phonepe.platform.bonsai.models.model.FlatNode;
import com.phonepe.platform.bonsai.models.model.ValueFlatNode;
import com.phonepe.platform.bonsai.models.value.BooleanValue;
import com.phonepe.platform.bonsai.models.value.JsonValue;
import com.phonepe.platform.bonsai.models.value.NumberValue;
import com.phonepe.platform.bonsai.models.value.StringValue;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author tushar.naik
 * @version 1.0  2019-06-17 - 17:43
 */
public class FlatNodeTest {
    private ObjectExtractor objectExtractor = new ObjectExtractor();

    @Test
    public void testJsonValueFlatNode() throws IOException {
        FlatNode flatNode = objectExtractor.getObject("serde/jsonValue.json", FlatNode.class);
        Assert.assertEquals(FlatNode.FlatNodeType.VALUE, flatNode.getType());
        Assert.assertEquals("json", ((JsonValue) ((ValueFlatNode) flatNode).getValue()).getValue().get("some").asText());
    }

    @Test
    public void testNumberValueFlatNode() throws IOException {
        FlatNode flatNode = objectExtractor.getObject("serde/numberValue.json", FlatNode.class);
        Assert.assertEquals(FlatNode.FlatNodeType.VALUE, flatNode.getType());
        Assert.assertEquals(2, ((NumberValue) ((ValueFlatNode) flatNode).getValue()).getValue());
    }

    @Test
    public void testStringValueFlatNode() throws IOException {
        FlatNode flatNode = objectExtractor.getObject("serde/stringValue.json", FlatNode.class);
        Assert.assertEquals(FlatNode.FlatNodeType.VALUE, flatNode.getType());
        Assert.assertEquals("hello", ((StringValue) ((ValueFlatNode) flatNode).getValue()).getValue());
    }

    @Test
    public void testBooleanValueFlatNode() throws IOException {
        FlatNode flatNode = objectExtractor.getObject("serde/booleanValue.json", FlatNode.class);
        Assert.assertEquals(FlatNode.FlatNodeType.VALUE, flatNode.getType());
        Assert.assertEquals(true, ((BooleanValue) ((ValueFlatNode) flatNode).getValue()).isValue());
    }
}