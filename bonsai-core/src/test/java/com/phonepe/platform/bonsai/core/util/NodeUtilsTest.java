package com.phonepe.platform.bonsai.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
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
import com.phonepe.platform.bonsai.models.value.ByteValue;
import com.phonepe.platform.bonsai.models.value.JsonValue;
import com.phonepe.platform.bonsai.models.value.NumberValue;
import com.phonepe.platform.bonsai.models.value.StringValue;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class NodeUtilsTest {


    @Test
    public void testDefaultValueReturnedOnInvalidKeyNode() throws IOException {
        assertTrue(
                NodeUtils.asBoolean(
                        KeyNode.builder()
                                .node(ValueNode.builder().value(new StringValue("false")).build())
                                .build(),
                        true
                                   ));
        assertTrue(
                NodeUtils.asBoolean(
                        KeyNode.builder()
                                .node(ValueNode.builder().value(new StringValue("falsea")).build())
                                .build(),
                        true
                                   ));
        assertTrue(
                NodeUtils.asBoolean(KeyNode.builder().node(ValueNode.builder().build()).build(),
                                    true
                                   ));
        assertTrue(
                NodeUtils.asBoolean(
                        KeyNode.builder().node(ValueNode.builder().value(new NumberValue(3)).build()).build(),
                        true
                                   ));
        assertTrue(
                NodeUtils.asBoolean(
                        KeyNode.builder()
                                .node(ValueNode.builder().value(new JsonValue(new ObjectMapper().readTree("{}")))
                                              .build()).build(),
                        true
                                   ));
        assertTrue(
                NodeUtils.asBoolean(
                        KeyNode.builder().node(ValueNode.builder().value(new ByteValue("{}".getBytes())).build())
                                .build(),
                        true
                                   ));
        assertTrue(
                NodeUtils.asBoolean(KeyNode.builder().node(null).build(),
                                    true
                                   ));
        assertTrue(NodeUtils.asBoolean((KeyNode) null, true));
    }

    @Test
    public void testNonDefaultValueReturnedOnValidKeyNode() {
        assertTrue(
                NodeUtils.asBoolean(
                        KeyNode.builder().node(ValueNode.builder().value(new BooleanValue(true)).build()).build(),
                        false
                                   ));
        assertFalse(
                NodeUtils.asBoolean(
                        KeyNode.builder().node(ValueNode.builder().value(new BooleanValue(false)).build()).build(),
                        true
                                   ));
    }

    @Test
    public void testDefaultValueReturnedOnInvalidPzNode() {
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
    public void testNonDefaultValueReturnedOnValidPzFlatNode() {
        assertTrue(
                NodeUtils.asBoolean(
                        new ValueFlatNode(new BooleanValue(true)),
                        false
                                   ));
        assertFalse(
                NodeUtils.asBoolean(
                        new ValueFlatNode(new BooleanValue(false)),
                        true
                                   ));
        assertFalse(
                NodeUtils.asBoolean(
                        new ListFlatNode(),
                        false
                                   ));
        assertFalse(
                NodeUtils.asBoolean(
                        new MapFlatNode(),
                        false
                                   ));
    }

    @Test
    public void testBooleanValueReturnedOnContainerKeyNode() {

        ImmutableList<KeyNode> trueNodes = ImmutableList.of(
                KeyNode.builder().node(ValueNode.builder().value(new BooleanValue(true)).build()).build(),
                KeyNode.builder().node(ValueNode.builder().value(new BooleanValue(true)).build()).build(),
                KeyNode.builder().node(ValueNode.builder().value(new BooleanValue(true)).build()).build());
        assertTrue(
                NodeUtils.asBoolean(KeyNode.builder().node(ListNode.builder().nodes(trueNodes).build()).build(),
                                    false
                                   ));

        ImmutableMap<String, KeyNode> trueNodesMap = ImmutableMap.of(
                "1", KeyNode.builder().node(ValueNode.builder().value(new BooleanValue(true)).build()).build(),
                "2", KeyNode.builder().node(ValueNode.builder().value(new BooleanValue(true)).build()).build(),
                "3", KeyNode.builder().node(ValueNode.builder().value(new BooleanValue(true)).build()).build());
        assertTrue(
                NodeUtils.asBoolean(KeyNode.builder().node(MapNode.builder().nodeMap(trueNodesMap).build()).build(),
                                    false
                                   ));
    }

    @Test
    public void testStringValueReturnedOnValidKeyNode() {
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
                             KeyNode.builder().node(ValueNode.builder().value(new BooleanValue(true)).build()).build(),
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
    public void testStringValueReturnedOnValidPzNode() {
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
    public void testNumericValueReturnedOnValidKeyNode() {
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
                             KeyNode.builder().node(ValueNode.builder().value(new BooleanValue(true)).build()).build(),
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
    public void testNumericValueReturnedOnValidPzNode() {
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
    public void testJsonNodeValueReturnedOnValidKeyNode() {
        assertNotNull(
                NodeUtils.asJsonNode(
                        KeyNode.builder()
                                .node(ValueNode.builder().value(new JsonValue(NullNode.getInstance())).build())
                                .build(),
                        null));
        assertNull(
                NodeUtils.asJsonNode(
                        KeyNode.builder()
                                .node(ValueNode.builder().value(new StringValue("123")).build())
                                .build(),
                        null));
        assertNull(
                NodeUtils.asJsonNode(
                        KeyNode.builder()
                                .node(ValueNode.builder().value(null).build())
                                .build(),
                        null));
        assertNull(
                NodeUtils.asJsonNode(
                        KeyNode.builder().node(ValueNode.builder().value(new BooleanValue(true)).build()).build(),
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
    }

    @Test
    public void testJsonNodeValueReturnedOnValidPzNode() {
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
    }

    @Test
    public void testListOfBooleanReturnedOnInvalidKeyNodes() {
        assertTrue(
                NodeUtils.asListOfBoolean(
                        KeyNode.builder().build(),
                        Collections.singletonList(true)
                                         ).get(0));
        assertTrue(
                NodeUtils.asListOfBoolean(
                        KeyNode.builder().node(new MapNode()).build(),
                        Collections.singletonList(true)
                                         ).get(0));
        assertTrue(
                NodeUtils.asListOfBoolean(
                        KeyNode.builder().node(new ValueNode()).build(),
                        Collections.singletonList(true)
                                         ).get(0));
    }

    @Test
    public void testListOfBooleanReturnedOnValidKeyNodes() {
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

}