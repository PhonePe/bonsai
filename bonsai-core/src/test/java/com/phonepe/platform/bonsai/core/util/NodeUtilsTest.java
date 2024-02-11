package com.phonepe.platform.bonsai.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.ListNode;
import com.phonepe.platform.bonsai.models.MapNode;
import com.phonepe.platform.bonsai.models.ValueNode;
import com.phonepe.platform.bonsai.models.model.FlatNode;
import com.phonepe.platform.bonsai.models.model.ListFlatNode;
import com.phonepe.platform.bonsai.models.model.MapFlatNode;
import com.phonepe.platform.bonsai.models.model.ValueFlatNode;
import com.phonepe.platform.bonsai.models.value.BooleanValue;
import com.phonepe.platform.bonsai.models.value.JsonValue;
import com.phonepe.platform.bonsai.models.value.NumberValue;
import com.phonepe.platform.bonsai.models.value.StringValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NodeUtilsTest {


    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void testDefaultValueReturnedOnInvalidKeyNode() throws IOException {
        assertTrue(NodeUtils.asBoolean(KeyNode.of(ValueNode.stringValue("false")), true));
        assertTrue(NodeUtils.asBoolean(KeyNode.of(ValueNode.stringValue("falsea")), true));
        assertTrue(NodeUtils.asBoolean(KeyNode.of(ValueNode.builder().build()), true));
        assertTrue(NodeUtils.asBoolean(KeyNode.of(ValueNode.numberValue(3)), true));
        assertTrue(NodeUtils.asBoolean(KeyNode.of(ValueNode.jsonValue(new ObjectMapper().readTree("{}"))), true));
        assertTrue(NodeUtils.asBoolean(KeyNode.of(ValueNode.byteValue("{}".getBytes())), true));
        assertTrue(NodeUtils.asBoolean(KeyNode.empty(""), true));
        assertTrue(NodeUtils.asBoolean((KeyNode) null, true));
    }

    @Test
    void testNonDefaultValueReturnedOnValidKeyNode() {
        assertTrue(NodeUtils.asBoolean(KeyNode.of(ValueNode.booleanValue(true)), false));
        assertFalse(NodeUtils.asBoolean(KeyNode.of(ValueNode.booleanValue(false)), true));
    }

    @Test
    void testDefaultValueReturnedOnInvalidPzNode() {
        assertTrue(
                NodeUtils.asBoolean(new ValueFlatNode(new StringValue("false")), true));
        assertTrue(
                NodeUtils.asBoolean(
                        new ValueFlatNode(new StringValue("falsea")),
                        true
                                   ));
        assertTrue(
                NodeUtils.asBoolean(new ValueFlatNode(), true));
        assertTrue(
                NodeUtils.asBoolean(
                        new ValueFlatNode(new NumberValue(3)),
                        true
                                   ));
        assertTrue(NodeUtils.asBoolean((FlatNode) null, true));
    }

    @Test
    void testNonDefaultValueReturnedOnValidPzFlatNode() {
        assertTrue(NodeUtils.asBoolean(new ValueFlatNode(new BooleanValue(true)), false));
        assertFalse(NodeUtils.asBoolean(new ValueFlatNode(new BooleanValue(false)), true));
        assertFalse(NodeUtils.asBoolean(new ListFlatNode(), false));
        assertFalse(NodeUtils.asBoolean(new MapFlatNode(), false));
    }

    @Test
    void testBooleanValueReturnedOnContainerKeyNode() {

        ImmutableList<KeyNode> trueNodes = ImmutableList.of(
                KeyNode.of(ValueNode.booleanValue(true)),
                KeyNode.of(ValueNode.booleanValue(true)),
                KeyNode.of(ValueNode.booleanValue(true)));
        assertTrue(
                NodeUtils.asBoolean(KeyNode.builder().node(ListNode.builder().nodes(trueNodes).build()).build(),
                                    false
                                   ));

        ImmutableMap<String, KeyNode> trueNodesMap = ImmutableMap.of(
                "1", KeyNode.of(ValueNode.booleanValue(true)),
                "2", KeyNode.of(ValueNode.booleanValue(true)),
                "3", KeyNode.of(ValueNode.booleanValue(true)));
        assertTrue(
                NodeUtils.asBoolean(KeyNode.builder().node(MapNode.builder().nodeMap(trueNodesMap).build()).build(),
                                    false
                                   ));

        assertFalse(NodeUtils.asBoolean(KeyNode.builder().node(new ListNode()).build(), false));
    }

    @Test
    void testStringValueReturnedOnValidKeyNode() {
        assertEquals("true",
                     NodeUtils.asString(
                             KeyNode.builder()
                                     .node(ValueNode.builder().value(new StringValue("true")).build())
                                     .build(),
                             "false"));
        assertEquals("false",
                     NodeUtils.asString(
                             (KeyNode) null,
                             "false"));
        assertEquals("false",
                     NodeUtils.asString(
                             KeyNode.builder().node(ValueNode.builder().value(null).build()).build(),
                             "false"));
        assertEquals("true",
                     NodeUtils.asString(
                             KeyNode.of(ValueNode.booleanValue(true)),
                             "false"));
        assertEquals("123",
                     NodeUtils.asString(
                             KeyNode.builder().node(ValueNode.builder().value(new NumberValue(123)).build()).build(),
                             "false"));
        assertEquals("false",
                     NodeUtils.asString(
                             KeyNode.builder().node(MapNode.builder().build()).build(),
                             "false"));
        assertEquals("false",
                     NodeUtils.asString(
                             KeyNode.builder().node(ListNode.builder().build()).build(),
                             "false"));
    }

    @Test
    void testStringValueReturnedOnValidPzNode() {
        assertEquals("true",
                     NodeUtils.asString(new ValueFlatNode(new StringValue("true")),
                                        "false"));
        assertEquals("false",
                     NodeUtils.asString(
                             (FlatNode) null,
                             "false"));
        assertEquals("false",
                     NodeUtils.asString(
                             KeyNode.builder().node(ValueNode.builder().value(null).build()).build(),
                             "false"));
        assertEquals("true",
                     NodeUtils.asString(new ValueFlatNode(new BooleanValue(true)),
                                        "false"));
        assertEquals("123",
                     NodeUtils.asString(new ValueFlatNode(new NumberValue(123)),
                                        "false"));
        assertEquals("123",
                     NodeUtils.asString(ValueFlatNode.objectValue("123"),
                                        "false"));
        assertEquals("123",
                     NodeUtils.asString(ValueFlatNode.jsonValue(new TextNode("123")),
                                        "false"));
        assertEquals("123",
                     NodeUtils.asString(ValueFlatNode.byteValue("123".getBytes()),
                                        "false"));
        assertEquals("false",
                     NodeUtils.asString(new ValueFlatNode(),
                                        "false"));
        assertEquals("false",
                     NodeUtils.asString(new ListFlatNode(),
                                        "false"));
        assertEquals("false",
                     NodeUtils.asString(new MapFlatNode(),
                                        "false"));
    }

    @Test
    void testNumericValueReturnedOnValidKeyNode() {
        assertEquals(4, NodeUtils.asNumber((KeyNode) null, 4));
        assertEquals(4,
                     NodeUtils.asNumber(
                             KeyNode.builder()
                                     .node(ValueNode.builder().value(new StringValue("true")).build())
                                     .build(), 4));
        assertEquals(123.0,
                     NodeUtils.asNumber(
                             KeyNode.builder()
                                     .node(ValueNode.builder().value(new StringValue("123")).build())
                                     .build(), 4));
        assertEquals(1,
                     NodeUtils.asNumber(
                             KeyNode.of(ValueNode.booleanValue(true)),
                             123));
        assertEquals(123,
                     NodeUtils.asNumber(
                             KeyNode.builder().node(ValueNode.builder().value(new NumberValue(123)).build()).build(),
                             456));
        assertEquals(123,
                     NodeUtils.asNumber(
                             KeyNode.builder().node(MapNode.builder().build()).build(),
                             123));
        assertEquals(123,
                     NodeUtils.asNumber(
                             KeyNode.builder().node(ListNode.builder().build()).build(),
                             123));
    }

    @Test
    void testNumericValueReturnedOnValidPzNode() {
        assertEquals(4,
                     NodeUtils.asNumber((FlatNode) null, 4));
        assertEquals(4,
                     NodeUtils.asNumber(new ValueFlatNode(new StringValue("true")),
                                        4));
        assertEquals(123.0,
                     NodeUtils.asNumber(new ValueFlatNode(new StringValue("123")),
                                        4));
        assertEquals(1,
                     NodeUtils.asNumber(new ValueFlatNode(new BooleanValue(true)),
                                        123));
        assertEquals(123,
                     NodeUtils.asNumber(new ValueFlatNode(new NumberValue(123)),
                                        456));
        assertEquals(123,
                     NodeUtils.asNumber(new MapFlatNode(),
                                        123));
        assertEquals(123,
                     NodeUtils.asNumber(new ListFlatNode(),
                                        123));
    }

    @Test
    void testJsonNodeValueReturnedOnValidKeyNode() {
        assertNotNull(
                NodeUtils.asJsonNode(
                        KeyNode.of(ValueNode.builder().value(new JsonValue(NullNode.getInstance())).build()),
                        null));
        assertNull(
                NodeUtils.asJsonNode(
                        KeyNode.of(ValueNode.builder().value(new StringValue("123")).build()),
                        null));
        assertNull(
                NodeUtils.asJsonNode(
                        KeyNode.of(ValueNode.builder().value(null).build()),
                        null));
        assertNull(
                NodeUtils.asJsonNode(
                        KeyNode.of(ValueNode.booleanValue(true)),
                        null));
        assertNull(
                NodeUtils.asJsonNode(
                        KeyNode.builder().node(ValueNode.builder().value(new NumberValue(123)).build()).build(),
                        null));
        assertNull(
                NodeUtils.asJsonNode(
                        KeyNode.builder().node(MapNode.builder().build()).build(),
                        null));
        assertNull(
                NodeUtils.asJsonNode(
                        KeyNode.builder().node(ListNode.builder().build()).build(),
                        null));
        assertNull(
                NodeUtils.asJsonNode(
                        (KeyNode) null,
                        null));
    }

    @Test
    void testJsonNodeValueReturnedOnValidPzNode() {
        assertNotNull(
                NodeUtils.asJsonNode(new ValueFlatNode(new JsonValue(NullNode.getInstance())), null));
        assertNull(
                NodeUtils.asJsonNode(new ValueFlatNode(new StringValue("123")), null));
        assertNull(
                NodeUtils.asJsonNode(new ValueFlatNode(null), null));
        assertNull(
                NodeUtils.asJsonNode(new ValueFlatNode(new BooleanValue(true)), null));
        assertNull(
                NodeUtils.asJsonNode(new ValueFlatNode(new NumberValue(123)), null));
        assertNull(
                NodeUtils.asJsonNode(new MapFlatNode(), null));
        assertNull(
                NodeUtils.asJsonNode(new ListFlatNode(), null));
        assertNull(
                NodeUtils.asJsonNode((FlatNode) null, null));
    }

    @Test
    void testListOfBooleanReturnedOnInvalidKeyNodes() {
        assertTrue(
                NodeUtils.asListOfBoolean(
                        KeyNode.builder().build(),
                        Collections.singletonList(true)).get(0));
        assertTrue(
                NodeUtils.asListOfBoolean(
                        KeyNode.builder().node(new MapNode()).build(),
                        Collections.singletonList(true)).get(0));
        assertTrue(
                NodeUtils.asListOfBoolean(
                        KeyNode.builder().node(new ValueNode()).build(),
                        Collections.singletonList(true)).get(0));
    }

    @Test
    void testListOfNumberReturnedOnInvalidKeyNodes() {
        assertEquals(1,
                     NodeUtils.asListOfNumber(
                             KeyNode.builder().build(),
                             Collections.singletonList(1)).get(0));
        assertEquals(1,
                     NodeUtils.asListOfNumber(
                             KeyNode.builder().node(new ListNode()).build(),
                             Collections.singletonList(1)).get(0));
        assertEquals(1,
                     NodeUtils.asListOfNumber(
                             KeyNode.builder().node(new MapNode()).build(),
                             Collections.singletonList(1)).get(0));
        assertEquals(1,
                     NodeUtils.asListOfNumber(
                             KeyNode.builder().node(new ValueNode()).build(),
                             Collections.singletonList(1)).get(0));
    }

    @Test
    void testListOfBooleanReturnedOnValidKeyNodes() {
        assertTrue(
                NodeUtils.asListOfBoolean(
                        KeyNode.builder().node(ListNode.builder()
                                                       .node(KeyNode.builder().node(
                                                               ValueNode.builder()
                                                                       .value(new BooleanValue(true))
                                                                       .build()).build()).build())
                                .build(),
                        Collections.singletonList(false)
                                         ).get(0));
        List<Boolean> booleans = NodeUtils.asListOfBoolean(
                KeyNode.builder()
                        .node(ListNode.builder()
                                      .node(KeyNode.builder().node(
                                              ValueNode.builder().value(new BooleanValue(true)).build()).build())
                                      .node(KeyNode.builder().node(
                                              ValueNode.builder().value(new BooleanValue(false)).build()).build())
                                      .node(KeyNode.builder().node(
                                              ValueNode.builder().value(new BooleanValue(false)).build()).build())
                                      .build())
                        .build(),
                Collections.singletonList(false)
                                                          );
        assertEquals(3, booleans.size());
        assertTrue(booleans.get(0));
        assertFalse(booleans.get(1));
        assertFalse(booleans.get(2));
    }

    @Test
    void testAsObject() throws IOException {
        assertEquals(1, NodeUtils.asObject(KeyNode.of(ValueNode.stringValue("{\"a\":1}")),
                                           TestObject.class, null, OBJECT_MAPPER).getA());
        assertEquals(1,
                     NodeUtils.asObject(KeyNode.of(
                                                ValueNode.byteValue(OBJECT_MAPPER.writeValueAsBytes(new TestObject(1,
                                                                                                                   "test")))),
                                        TestObject.class, null, OBJECT_MAPPER).getA());
        assertEquals(1, NodeUtils.asObject(KeyNode.of(ValueNode.objectValue(new TestObject(1, "test"))),
                                           TestObject.class, null, OBJECT_MAPPER).getA());
        assertEquals(1, NodeUtils.asObject(KeyNode.of(ValueNode.jsonValue(OBJECT_MAPPER.readTree("{\"a\":1}"))),
                                           TestObject.class, null, OBJECT_MAPPER).getA());
        assertEquals(1, NodeUtils.asObject(KeyNode.of(ValueNode.numberValue(3)),
                                           TestObject.class, new TestObject(1, "test"), OBJECT_MAPPER).getA());
        assertEquals(1, NodeUtils.asObject(KeyNode.of(ValueNode.booleanValue(true)),
                                           TestObject.class, new TestObject(1, "test"), OBJECT_MAPPER).getA());
        assertEquals(1, NodeUtils.asObject(KeyNode.of(ListNode.builder().build()),
                                           TestObject.class, new TestObject(1, "test"), OBJECT_MAPPER).getA());
        assertEquals(1, NodeUtils.asObject(KeyNode.of(MapNode.builder().build()),
                                           TestObject.class, new TestObject(1, "test"), OBJECT_MAPPER).getA());
        assertEquals(1, NodeUtils.asObject(KeyNode.empty(""),
                                           TestObject.class, new TestObject(1, "test"), OBJECT_MAPPER).getA());
        assertEquals(1, NodeUtils.asObject((KeyNode) null,
                                           TestObject.class, new TestObject(1, "test"), OBJECT_MAPPER).getA());

        assertEquals(1, NodeUtils.asObject(ValueFlatNode.stringValue("{\"a\":1}"),
                                           TestObject.class, null, OBJECT_MAPPER).getA());
        assertEquals(1,
                     NodeUtils.asObject(
                             ValueFlatNode.byteValue(OBJECT_MAPPER.writeValueAsBytes(new TestObject(1, "test"))),
                             TestObject.class, null, OBJECT_MAPPER).getA());
        assertEquals(1, NodeUtils.asObject(ValueFlatNode.objectValue(new TestObject(1, "test")),
                                           TestObject.class, null, OBJECT_MAPPER).getA());
        assertEquals(1, NodeUtils.asObject(ValueFlatNode.jsonValue(OBJECT_MAPPER.readTree("{\"a\":1}")),
                                           TestObject.class, null, OBJECT_MAPPER).getA());
        assertEquals(1, NodeUtils.asObject(ValueFlatNode.numberValue(3),
                                           TestObject.class, new TestObject(1, "test"), OBJECT_MAPPER).getA());
        assertEquals(1, NodeUtils.asObject(ValueFlatNode.booleanValue(true),
                                           TestObject.class, new TestObject(1, "test"), OBJECT_MAPPER).getA());
        assertEquals(1, NodeUtils.asObject(new ListFlatNode(),
                                           TestObject.class, new TestObject(1, "test"), OBJECT_MAPPER).getA());
        assertEquals(1, NodeUtils.asObject(new MapFlatNode(),
                                           TestObject.class, new TestObject(1, "test"), OBJECT_MAPPER).getA());
        assertEquals(1, NodeUtils.asObject(new ValueFlatNode(),
                                           TestObject.class, new TestObject(1, "test"), OBJECT_MAPPER).getA());
        assertEquals(1, NodeUtils.asObject((FlatNode) null,
                                           TestObject.class, new TestObject(1, "test"), OBJECT_MAPPER).getA());
    }

    @Test
    void testAsMapOfBoolean() {
        assertEquals(ImmutableMap.of("t", false), NodeUtils.asMapOfBoolean(
                KeyNode.of(new MapNode("asdf", 1,
                                       ImmutableMap.of("t", KeyNode.of(ValueNode.booleanValue(false))))),
                ImmutableMap.of("u", true)));
    }

    @Test
    void testAsMapOfString() {
        assertEquals(ImmutableMap.of("first", "map"), NodeUtils.asMapOfString(
                KeyNode.of(new MapNode("asdf", 1,
                                       ImmutableMap.of("first", KeyNode.of(ValueNode.stringValue("map"))))),
                ImmutableMap.of("second", "map")));
    }

    @Test
    void testAsMapOfNumber() {
        assertEquals(ImmutableMap.of("first", 1), NodeUtils.asMapOfNumber(
                KeyNode.of(new MapNode("asdf", 1,
                                       ImmutableMap.of("first", KeyNode.of(ValueNode.numberValue(1))))),
                ImmutableMap.of("second", 2)));
    }

    @Test
    void testAsMap() {
        assertEquals(ImmutableMap.of(), NodeUtils.asMap(KeyNode.of(ValueNode.objectValue(new TestObject(1, "test"))),
                                                        ImmutableMap.of(),
                                                        (keyNode, o) -> null));
        assertEquals(ImmutableMap.of("t", "default"),
                     NodeUtils.asMap(KeyNode.of(MapNode.builder()
                                                        .nodeMap(ImmutableMap.of("t",
                                                                                 KeyNode.of(ValueNode.objectValue(
                                                                                         new TestObject(1, "test")))))
                                                        .build()),
                                     ImmutableMap.of(),
                                     (keyNode, o) -> "default"));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TestObject {
        private int a;
        private String b;
    }

}