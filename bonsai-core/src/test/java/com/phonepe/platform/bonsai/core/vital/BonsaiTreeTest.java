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

import com.google.common.collect.ImmutableMap;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.ObjectExtractor;
import com.phonepe.platform.bonsai.core.Parsers;
import com.phonepe.platform.bonsai.core.TreeGenerationHelper;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.exception.BonsaiErrorCode;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.ListNode;
import com.phonepe.platform.bonsai.models.MapNode;
import com.phonepe.platform.bonsai.models.NodeVisitors;
import com.phonepe.platform.bonsai.models.TreeKnotState;
import com.phonepe.platform.bonsai.models.ValueNode;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.blocks.delta.DeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.EdgeDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KeyMappingDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KnotDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import com.phonepe.platform.bonsai.models.data.KnotData;
import com.phonepe.platform.bonsai.models.data.MapKnotData;
import com.phonepe.platform.bonsai.models.data.MultiKnotData;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.bonsai.models.structures.OrderedList;
import com.phonepe.platform.bonsai.models.value.StringValue;
import com.phonepe.platform.query.dsl.Filter;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import com.phonepe.platform.query.dsl.general.NotEqualsFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BonsaiTreeTest {

    private static final BonsaiProperties DEFAULT_PROPERTIES =
            BonsaiProperties.builder().mutualExclusivitySettingTurnedOn(true)
                    .maxAllowedConditionsPerEdge(10).maxAllowedVariationsPerKnot(10).build();

    private Bonsai<Context> bonsai;

    private static Bonsai<Context> createNewInMemoryBonsai(BonsaiProperties properties) {
        return BonsaiBuilder.builder().withBonsaiProperties(properties).build();
    }

    @BeforeEach
    public void setUp() {
        bonsai = createNewInMemoryBonsai(DEFAULT_PROPERTIES);
    }

    @AfterEach
    public void destroy() {
        bonsai = null;
    }

    @Test
    void given_bonsaiTree_when_evaluatingTree_then_returnKeyNode() throws IOException {
        Map userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);
        Knot level1 = bonsai.createMapping("test", ValuedKnotData.stringValue("1"), null);
        Knot level21 = bonsai.createKnot(ValuedKnotData.stringValue("21"), null);
        Knot level22 = bonsai.createKnot(ValuedKnotData.stringValue("22"), null);
        bonsai.addVariation(level1.getId(), Variation.builder()
                .filter(EqualsFilter.builder().field("$.location.cityy")
                        .value(true).build())
                .knotId(level21.getId())
                .build());
        bonsai.addVariation(level1.getId(), Variation.builder()
                .filter(NotEqualsFilter.builder().field("$.location.cityy")
                        .value(false).build())
                .knotId(level22.getId())
                .build());
        final KeyNode keyNode = bonsai.evaluate("test", Context.builder()
                .documentContext(Parsers.parse(userContext1))
                .build());
        Assertions.assertNotNull(keyNode, "KeyNode should not be null.");
        Assertions.assertEquals("test", keyNode.getKey(), "The value of [keyNode.Key] should be : test.");
    }

    @Test
    void given_bonsaiTree_when_evaluatingTreeHeavilyWithGenderFilter_then_returnKeyNode() throws IOException,
            BonsaiError {
        final var userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);
        final var userContext2 = new ObjectExtractor().getObject("userData2.json", Map.class);

        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"), null);
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"), null);
        bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"), null);
        bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"), null);
        final var widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
                .key("icon_1")
                .key("icon_4")
                .key("icon_2")
                .key("icon_3")
                .build(), null);
        bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"), null);
        bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"), null);
        final var hpKnot = bonsai.createKnot(MultiKnotData.builder()
                .key("widget_1")
                .key("widget_2")
                .key("widget_3")
                .build(), null);

        bonsai.createMapping("home_page_1", hpKnot.getId());

        final var femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                .key("icon_3")
                .key("icon_1")
                .key("icon_4")
                .build(), null);
        Assertions.assertNotNull(femaleConditionKnot);

        Assertions.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                .filter(new EqualsFilter("$.gender", "female"))
                .knotId(femaleConditionKnot.getId())
                .build()));

        KeyNode user1HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                .documentContext(Parsers.parse(userContext1))
                .build());
        System.out.println(Parsers.MAPPER.writeValueAsString(user1HomePageEvaluation));

        Assertions.assertEquals("home_page_1", user1HomePageEvaluation.getKey());
        Assertions.assertEquals(user1HomePageEvaluation.getNode().getId(), hpKnot.getId());
        Assertions.assertTrue(NodeVisitors.isList(user1HomePageEvaluation.getNode()));

        Assertions.assertEquals(3, ((ListNode) user1HomePageEvaluation.getNode()).getNodes().size());
        Assertions.assertTrue(NodeVisitors.isList(((ListNode) user1HomePageEvaluation.getNode()).getNodes()
                .get(0)
                .getNode()));

        Assertions.assertEquals(4, ((ListNode) (((ListNode) user1HomePageEvaluation.getNode()).getNodes()
                .get(0)
                .getNode())).getNodes()
                .size());

        /* evaluate with context 2 */
        KeyNode user2HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                .documentContext(Parsers.parse(userContext2))
                .build());


        Assertions.assertEquals("home_page_1", user2HomePageEvaluation.getKey());
        Assertions.assertEquals(hpKnot.getId(), user2HomePageEvaluation.getNode().getId());
        Assertions.assertTrue(NodeVisitors.isList(user2HomePageEvaluation.getNode()));

        Assertions.assertEquals(3, ((ListNode) user2HomePageEvaluation.getNode()).getNodes().size());
        Assertions.assertTrue(NodeVisitors.isList(((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                .get(0)
                .getNode()));

        Assertions.assertEquals(3, ((ListNode) (((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                .get(0).getNode())).getNodes()
                .size());

        Assertions.assertEquals((((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                .get(0)
                .getNode()).getId(), femaleConditionKnot.getId());

        System.out.println(Parsers.MAPPER.writeValueAsString(user2HomePageEvaluation));
    }

    @Test
    void given_bonsaiTree_when_evaluatingTreeHeavilyWithLocationFilter_then_returnKeyNode() throws IOException,
            BonsaiError {
        Map userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);
        Map userContext2 = new ObjectExtractor().getObject("userData2.json", Map.class);

        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"), null);
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"), null);
        bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"), null);
        bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"), null);
        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
                .key("icon_1")
                .key("icon_4")
                .key("icon_2")
                .key("icon_3")
                .build(), null);
        bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"), null);
        bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"), null);
        Knot hpKnot = bonsai.createKnot(MultiKnotData.builder()
                .key("widget_1")
                .key("widget_2")
                .key("widget_3")
                .build(), null);

        bonsai.createMapping("home_page_1", hpKnot.getId());

        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                .key("icon_3")
                .key("icon_1")
                .key("icon_4")
                .build(), null);
        Assertions.assertNotNull(femaleConditionKnot);

        Assertions.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                .filter(new EqualsFilter("$.location.tier", "tier2"))
                .knotId(femaleConditionKnot.getId())
                .build()));

        KeyNode user1HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                .documentContext(Parsers.parse(userContext1))
                .build());
        System.out.println(Parsers.MAPPER.writeValueAsString(user1HomePageEvaluation));

        Assertions.assertEquals("home_page_1", user1HomePageEvaluation.getKey());
        Assertions.assertEquals(hpKnot.getId(), user1HomePageEvaluation.getNode().getId());
        Assertions.assertTrue(NodeVisitors.isList(user1HomePageEvaluation.getNode()));

        Assertions.assertEquals(3, ((ListNode) user1HomePageEvaluation.getNode()).getNodes().size());
        Assertions.assertTrue(NodeVisitors.isList(((ListNode) user1HomePageEvaluation.getNode()).getNodes()
                .get(0)
                .getNode()));

        Assertions.assertEquals(4, ((ListNode) (((ListNode) user1HomePageEvaluation.getNode()).getNodes()
                .get(0)
                .getNode())).getNodes()
                .size());

        /* evaluate with context 2 */
        KeyNode user2HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                .documentContext(Parsers.parse(userContext2))
                .build());


        Assertions.assertEquals("home_page_1", user2HomePageEvaluation.getKey());
        Assertions.assertEquals(user2HomePageEvaluation.getNode().getId(), hpKnot.getId());
        Assertions.assertTrue(NodeVisitors.isList(user2HomePageEvaluation.getNode()));

        Assertions.assertEquals(3, ((ListNode) user2HomePageEvaluation.getNode()).getNodes().size());
        Assertions.assertTrue(NodeVisitors.isList(((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                .get(0)
                .getNode()));

        Assertions.assertEquals(3, ((ListNode) (((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                .get(0)
                .getNode())).getNodes()
                .size());

        Assertions.assertEquals((((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                .get(0)
                .getNode()).getId(), femaleConditionKnot.getId());

        System.out.println(Parsers.MAPPER.writeValueAsString(user2HomePageEvaluation));
    }

    @Test
    void given_bonsaiTree_when_evaluatingTreeHeavilyWithMapKnot_then_returnKeyNode() throws IOException, BonsaiError {
        Map userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);

        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"), null);
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"), null);
        Knot icon3 = bonsai.createKnot(ValuedKnotData.stringValue(("This is some coool icon")), null);
        /* there is no older mapping, hence it will return null */
        Knot icon_3 = bonsai.createMapping("icon_3", icon3.getId());
        Assertions.assertNull(icon_3);

        /* now mappings exist, hence it will return the older knot */
        Knot icon_3_1 = bonsai.createMapping("icon_3", icon3.getId());
        Assertions.assertNotNull(icon_3_1);
        Assertions.assertEquals(icon3, icon_3_1);
        bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"), null);
        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
                .key("icon_1")
                .key("icon_4")
                .key("icon_2")
                .key("icon_3")
                .build(), null);
        bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"), null);
        bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"), null);
        bonsai.createMapping("widget_4", ValuedKnotData.stringValue("widget_4"), null);
        Knot homePageKnot = bonsai.createKnot(MapKnotData.builder()
                .mapKeys(ImmutableMap.of("w1", "widget_1",
                        "w2", "widget_2",
                        "w3", "widget_3",
                        "w4", "widget_4"))
                .build(), null);
        Assertions.assertNull(bonsai.createMapping("home_page_1", homePageKnot.getId()));
        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                .key("icon_3")
                .key("icon_1")
                .key("icon_4")
                .build(), null);
        Assertions.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                .filter(new EqualsFilter("$.gender", "female"))
                .knotId(femaleConditionKnot.getId())
                .build()));

        KeyNode user1HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                .documentContext(Parsers.parse(userContext1))
                .build());
        System.out.println(Parsers.MAPPER.writeValueAsString(user1HomePageEvaluation));

        Assertions.assertEquals("home_page_1", user1HomePageEvaluation.getKey());
        Assertions.assertEquals(homePageKnot.getId(), user1HomePageEvaluation.getNode().getId());
        Assertions.assertTrue(NodeVisitors.isMap(user1HomePageEvaluation.getNode()));

        Assertions.assertEquals(4, ((MapNode) user1HomePageEvaluation.getNode()).getNodeMap().size());
        Assertions.assertTrue(NodeVisitors.isList(((MapNode) user1HomePageEvaluation.getNode()).getNodeMap()
                .get("w1")
                .getNode()));

        Assertions.assertEquals(4, ((ListNode) (((MapNode) user1HomePageEvaluation.getNode()).getNodeMap()
                .get("w1")
                .getNode())).getNodes()
                .size());
    }

    @Test
    void given_bonsaiTree_when_evaluatingTree_then_returnPreferredKeyNode() {
        Knot l1 = bonsai.createKnot(ValuedKnotData.stringValue("L-1"), null);
        bonsai.createMapping("baseKey", l1.getId());

        Knot l21 = bonsai.createKnot(ValuedKnotData.stringValue("L-2-1"), null);

        bonsai.addVariation(l1.getId(), Variation.builder()
                .filter(new EqualsFilter("$.gender", "female"))
                .knotId(l21.getId())
                .build());

        KeyNode nonPreferencialEval = bonsai.evaluate("baseKey", Context.builder()
                .documentContext(Parsers.parse(ImmutableMap.of("gender", "female")))
                .build());
        Assertions.assertEquals(l21.getId(), nonPreferencialEval.getNode().getId());
        Assertions.assertEquals("L-2-1",
                ((StringValue) ((ValueNode) nonPreferencialEval.getNode()).getValue()).getValue());
        Knot preferredKnot = Knot.builder()
                .id("P1kaID")
                .knotData(ValuedKnotData.stringValue("P-1"))
                .build();
        KeyNode preferentialEval = bonsai.evaluate
                ("baseKey",
                        Context.builder()
                                .documentContext(Parsers.parse(ImmutableMap.of("gender", "female")))
                                .preferences(ImmutableMap.of("baseKey", preferredKnot))
                                .build());
        Assertions.assertEquals(preferredKnot.getId(), preferentialEval.getNode().getId());
        Assertions.assertEquals(((StringValue) ((ValuedKnotData) preferredKnot.getKnotData()).getValue()).getValue(),
                ((StringValue) ((ValueNode) preferentialEval.getNode()).getValue()).getValue());
    }

    @Test
    void given_bonsaiTree_when_evaluatingTree_then_returnRecursivePreferredKeyNode() {
        bonsai.createMapping("w1", ValuedKnotData.stringValue("widget1"), null);
        bonsai.createMapping("w2", ValuedKnotData.stringValue("widget2"), null);
        bonsai.createMapping("w3", ValuedKnotData.stringValue("widget3"), null);

        Knot l1 = bonsai.createKnot(MultiKnotData.builder().key("w1").key("w2").key("w3").build(), null);
        bonsai.createMapping("l1", l1.getId());

        Knot l21 = bonsai.createKnot(MultiKnotData.builder().key("w3").key("w2").key("w1").build(), null);
        Knot l22 = bonsai.createKnot(MultiKnotData.builder().key("w1").key("w3").build(), null);

        bonsai.createMapping("l1", l1.getId());

        bonsai.addVariation(l1.getId(), Variation.builder()
                .filter(new EqualsFilter("$.gender", "female"))
                .knotId(l21.getId())
                .build());

        bonsai.addVariation(l1.getId(), Variation.builder()
                .filter(new EqualsFilter("$.gender", "male"))
                .knotId(l22.getId())
                .build());

        KeyNode nonPreferencialEval = bonsai.evaluate("l1", Context.builder()
                .documentContext(Parsers.parse(ImmutableMap.of("gender", "female")))
                .build());
        Assertions.assertEquals(l21.getId(), nonPreferencialEval.getNode().getId());
        Knot preferredKnot = Knot.builder()
                .id("P1kaID")
                .knotData(MultiKnotData.builder().key("w3").key("w1").build())
                .build();
        KeyNode preferentialEval = bonsai.evaluate
                ("l1",
                        Context.builder()
                                .documentContext(Parsers.parse(ImmutableMap.of("gender", "female")))
                                .preferences(ImmutableMap.of("l1", preferredKnot))
                                .build());
        Assertions.assertEquals(preferredKnot.getId(), preferentialEval.getNode().getId());
        Assertions.assertEquals("widget3", ((StringValue) ((ValueNode) ((ListNode) preferentialEval.getNode())
                .getNodes().get(0).getNode()).getValue()).getValue());
        Assertions.assertEquals("widget1", ((StringValue) ((ValueNode) ((ListNode) preferentialEval.getNode())
                .getNodes().get(1).getNode()).getValue()).getValue());

        /* now evaluate with a new value for widget1 */
        preferentialEval = bonsai.evaluate
                ("l1",
                        Context.builder()
                                .documentContext(Parsers.parse(ImmutableMap.of("gender", "female")))
                                .preferences(ImmutableMap.of("l1", preferredKnot,
                                        "w1", Knot.builder()
                                                .id("w1kaID")
                                                .knotData(ValuedKnotData.stringValue("newStringValue"))
                                                .build()))
                                .build());
        Assertions.assertEquals(preferredKnot.getId(), preferentialEval.getNode().getId());
        Assertions.assertEquals("widget3", ((StringValue) ((ValueNode) ((ListNode) preferentialEval.getNode())
                .getNodes().get(0).getNode()).getValue()).getValue());

        /* the value should be whatever we have set in preferences */
        Assertions.assertEquals("newStringValue", ((StringValue) ((ValueNode) ((ListNode) preferentialEval.getNode())
                .getNodes().get(1).getNode()).getValue()).getValue());
    }

    @Test
    void given_bonsaiTree_when_gettingCompleteTree_then_returnTreeKnot() {
        final Knot knotOne = bonsai.createKnot(ValuedKnotData.stringValue("KnotOne"), null);
        final Knot knotTwo = bonsai.createKnot(ValuedKnotData.stringValue("KnotTwo"), null);
        bonsai.createMapping("key", knotOne.getId());
        bonsai.addVariation(knotOne.getId(), Variation.builder()
                .filter(new EqualsFilter("$.gender", "female"))
                .knotId(knotTwo.getId())
                .build());

        final TreeKnot treeKnot = bonsai.getCompleteTree("key");

        assertNotNull(treeKnot, "TreeKnot should not be null.");
        assertNotNull(treeKnot.getId(), "TreeKnot Id should not be null.");
        assertNotNull(treeKnot.getKnotData(), "TreeKnot data should exist.");
        assertEquals(1, treeKnot.getTreeEdges().size(), "There is only one edge connected to root TreeKnot.");
        assertNotEquals(0, treeKnot.getVersion(), "Version of TreeKnot should not be zero.");
        final TreeEdge treeEdge = treeKnot.getTreeEdges().get(0);
        assertNotNull(treeEdge, "TreeEdge should not be null.");
        assertNotNull(treeEdge.getEdgeIdentifier(), "TreeEdge should have non-null identifier.");
        assertEquals(1, treeEdge.getFilters().size(), "There is only one filter connected to root TreeEdge.");
        assertNotEquals(0, treeEdge.getVersion(), "Version of TreeEdge should not be zero.");
        final TreeKnot treeKnotInternal = treeEdge.getTreeKnot();
        assertNotNull(treeKnotInternal, "Internal TreeKnot should not be null.");
        assertNotNull(treeKnotInternal.getId(), "Internal TreeKnot Id should not be null.");
        assertNotNull(treeKnotInternal.getKnotData(), "Internal TreeKnot data should exist.");
        assertNull(treeKnotInternal.getTreeEdges(), "Internal TreeKnot should have zero TreeEdges.");
        assertNotEquals(0, treeKnotInternal.getVersion(), "Version of Internal TreeKnot should not be zero.");
    }

    @Test
    void given_bonsaiTree_when_gettingCompleteTreeWithPendingUpdates_then_returnTreeKnot() {
        final List<DeltaOperation> deltaOperationList = new ArrayList<>();
        deltaOperationList.add(new KeyMappingDeltaOperation("key", "knotOne"));
        deltaOperationList.add(new KnotDeltaOperation(
                new Knot("knotOne", 0, null, ValuedKnotData.stringValue("Knot One Value"), new HashMap<>())));

        final TreeKnot treeKnot = bonsai.getCompleteTreeWithDeltaOperations("key", deltaOperationList).getTreeKnot();
        final String knotViaKey = bonsai.getMapping("key");
        final Knot knot = bonsai.getKnot("knotOne");

        assertNotNull(treeKnot, "TreeKnot should not be null.");
        assertEquals("knotOne", treeKnot.getId(), "The Id of TreeKnot should be : knotOne.");
        assertEquals(0, treeKnot.getVersion(), "The version of temporary TreeKnot should be zero.");
        assertEquals(0, treeKnot.getTreeEdges().size(), "There are zero edges connected to TreeKnot.");
        assertNotNull(treeKnot.getKnotData(), "TreeKnot data should exist.");
        assertEquals("VALUED", treeKnot.getKnotData().getKnotDataType().toString(),
                "VALUED type KnotData should present.");
        assertNull(knotViaKey, "InMemoryKeyTreeStore should not contain knotId for keyId : key");
        assertNull(knot, "InMemoryKnotStore should not contain Knot for knotId : knotOne");
    }

    @Test
    void given_bonsaiTree_when_applyingPendingUpdatesOnCompleteTree_then_returnTreeKnot() {
        final List<DeltaOperation> deltaOperationList = new ArrayList<>();
        deltaOperationList.add(new KeyMappingDeltaOperation("key", "knotOne"));
        deltaOperationList.add(new KnotDeltaOperation(
                new Knot("knotOne", 0, null, ValuedKnotData.stringValue("Knot One Value"), new HashMap<>())));

        final TreeKnot treeKnot = bonsai.applyDeltaOperations("key", deltaOperationList).getTreeKnot();
        final String knotViaKey = bonsai.getMapping("key");
        final Knot knot = bonsai.getKnot(knotViaKey);

        assertNotNull(treeKnot, "TreeKnot should not be null.");
        assertEquals("knotOne", treeKnot.getId(), "The Id of TreeKnot should be : knotOne.");
        assertEquals(0, treeKnot.getVersion(), "The version of temporary TreeKnot should be zero.");
        assertEquals(0, treeKnot.getTreeEdges().size(), "There are zero edges connected to TreeKnot.");
        assertNotNull(treeKnot.getKnotData(), "TreeKnot data should exist.");
        assertEquals("VALUED", treeKnot.getKnotData().getKnotDataType().toString(),
                "VALUED type KnotData should present.");
        assertNotNull(knot, "Knot should not be null.");
        assertNotEquals(0, knot.getVersion(), "The version of Knot should not be zero."); // This is main check.
        assertNull(knot.getEdges(), "There are zero edges connected to Knot.");
        assertNotNull(knot.getKnotData(), "Knot data should exist.");
        assertEquals("VALUED", knot.getKnotData().getKnotDataType().toString(), "VALUED type KnotData should present.");


    }

    /**
     * Adding the documentation to explain what we are trying to achieve here.
     * <p>
     * 1. Add a new tree with KEY = "key"
     * __________
     * |  VALUED  |
     * | Value::0 |
     * ----------
     * Now, validate it.
     * <p>
     * <p>
     * 2. Add two variation to its
     * <p>
     * __________
     * |  VALUED  |
     * | Value::0 |
     * ----------
     * userId = U1     /            \ userId = U2
     * number = 1    /             \ number = 2
     * ----------        ----------
     * |  VALUED  |      |  VALUED  |
     * | Value::1 |      | Value::2 |
     * ----------        ----------
     * Now, validate it.
     * <p>
     * <p>
     * 3. Remove "Value::1" node and add third node.
     * <p>
     * __________
     * |  VALUED  |
     * | Value::0 |
     * ----------
     * userId != U5    /         \ userId = U2
     * number = 3    /          \ number = 2
     * ----------      ----------
     * |  VALUED  |    |  VALUED  |
     * | Value::3 |    | Value::2 |
     * ----------      ----------
     * Now, validate its along with the new number '3' on the newest knot.
     */
    @Test
    void Given_BonsaiTreeAndDeltaOperationList_When_ApplyingDeltaOperationsOnTree_Then_SaveAndReturnFinalTreeKnot() {
        // First Insertion
        List<DeltaOperation> deltaOperationList = new ArrayList<>();
        final KeyMappingDeltaOperation keyMappingDeltaOperation = KeyMappingDeltaOperation.builder()
                .keyId("key")
                .knotId("rootKnotId")
                .build();
        final Knot rootKnot = Knot.builder()
                .id("rootKnotId")
                .version(0)
                .knotData(ValuedKnotData.stringValue("Value :: 0"))
                .build();
        KnotDeltaOperation rootKnotDeltaOperation = new KnotDeltaOperation(rootKnot);
        deltaOperationList.add(keyMappingDeltaOperation);
        deltaOperationList.add(rootKnotDeltaOperation);

        TreeKnot fetchTreeKnot = bonsai.applyDeltaOperations("key", deltaOperationList).getTreeKnot();

        assertNotNull(fetchTreeKnot);
        assertNotNull(fetchTreeKnot.getId());
        assertEquals(0, fetchTreeKnot.getVersion());
        assertNotNull(fetchTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, fetchTreeKnot.getKnotData().getKnotDataType());
        assertEquals(0, fetchTreeKnot.getTreeEdges().size());

        fetchTreeKnot = bonsai.getCompleteTree("key");

        assertNotNull(fetchTreeKnot);
        assertNotNull(fetchTreeKnot.getId());
        assertNotEquals(0, fetchTreeKnot.getVersion());
        assertNotNull(fetchTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, fetchTreeKnot.getKnotData().getKnotDataType());
        assertNull(fetchTreeKnot.getTreeEdges());

        // Second Insertion
        final Knot firstKnot = Knot.builder()
                .id("firstKnotId")
                .version(0)
                .knotData(ValuedKnotData.stringValue("Value :: 1"))
                .build();
        final KnotDeltaOperation firstKnotDeltaOperation = new KnotDeltaOperation(firstKnot);
        final Edge firstEdge = Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("firstEdgeId", 1, 1))
                .version(0)
                .knotId("firstKnotId")
                .filter(new EqualsFilter("userId", "U1"))
                .build();
        final EdgeDeltaOperation firstEdgeDeltaOperation = new EdgeDeltaOperation(firstEdge);
        final Knot secondKnot = Knot.builder()
                .id("secondKnotId")
                .version(0)
                .knotData(ValuedKnotData.stringValue("Value :: 2"))
                .build();
        final KnotDeltaOperation secondKnotDeltaOperation = new KnotDeltaOperation(secondKnot);
        final Edge secondEdge = Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("secondEdgeId", 2, 2))
                .version(0)
                .knotId("secondKnotId")
                .filter(new EqualsFilter("userId", "U2"))
                .build();
        final EdgeDeltaOperation secondEdgeDeltaOperation = new EdgeDeltaOperation(secondEdge);
        final OrderedList<EdgeIdentifier> edgeIdentifierOrderedList = new OrderedList<>();
        edgeIdentifierOrderedList.add(new EdgeIdentifier("firstEdgeId", 0, 1));
        edgeIdentifierOrderedList.add(new EdgeIdentifier("secondEdgeId", 0, 2));
        final Knot rootKnotModifiedOne = Knot.builder()
                .id(fetchTreeKnot.getId())
                .version(0)
                .knotData(ValuedKnotData.stringValue("Value :: 0"))
                .edges(edgeIdentifierOrderedList)
                .build();
        final KnotDeltaOperation rootKnotDeltaOperationModifiedOne = new KnotDeltaOperation(rootKnotModifiedOne);

        deltaOperationList.clear();
        deltaOperationList.add(rootKnotDeltaOperationModifiedOne);
        deltaOperationList.add(secondEdgeDeltaOperation);
        deltaOperationList.add(secondKnotDeltaOperation);
        deltaOperationList.add(firstEdgeDeltaOperation);
        deltaOperationList.add(firstKnotDeltaOperation);

        fetchTreeKnot = bonsai.applyDeltaOperations("key", deltaOperationList).getTreeKnot();

        assertNotNull(fetchTreeKnot);
        assertNotNull(fetchTreeKnot.getId());
        assertEquals(0, fetchTreeKnot.getVersion());
        assertNotNull(fetchTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, fetchTreeKnot.getKnotData().getKnotDataType());
        assertEquals(2, fetchTreeKnot.getTreeEdges().size());
        TreeEdge firstTreeEdge = fetchTreeKnot.getTreeEdges().get(0);
        TreeEdge secondTreeEdge = fetchTreeKnot.getTreeEdges().get(1);
        assertNotNull(firstTreeEdge);
        assertNotNull(firstTreeEdge.getEdgeIdentifier().getId());
        assertEquals(1, firstTreeEdge.getEdgeIdentifier().getNumber());
        assertEquals(0, firstTreeEdge.getVersion());
        assertEquals("userId", firstTreeEdge.getFilters().get(0).getField());
        assertNotNull(secondTreeEdge);
        assertNotNull(secondTreeEdge.getEdgeIdentifier().getId());
        assertEquals(2, secondTreeEdge.getEdgeIdentifier().getNumber());
        assertEquals(0, secondTreeEdge.getVersion());
        assertEquals("userId", secondTreeEdge.getFilters().get(0).getField());
        TreeKnot firstTreeKnot = firstTreeEdge.getTreeKnot();
        TreeKnot secondTreeKnot = secondTreeEdge.getTreeKnot();
        assertNotNull(firstTreeKnot);
        assertEquals("firstKnotId", firstTreeKnot.getId());
        assertEquals(0, firstTreeKnot.getVersion());
        assertNotNull(firstTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, firstTreeKnot.getKnotData().getKnotDataType());
        assertNotNull(secondTreeKnot);
        assertEquals("secondKnotId", secondTreeKnot.getId());
        assertEquals(0, secondTreeKnot.getVersion());
        assertNotNull(secondTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, secondTreeKnot.getKnotData().getKnotDataType());

        fetchTreeKnot = bonsai.getCompleteTree("key");

        assertNotNull(fetchTreeKnot);
        assertNotNull(fetchTreeKnot.getId());
        assertNotEquals(0, fetchTreeKnot.getVersion());
        assertNotNull(fetchTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, fetchTreeKnot.getKnotData().getKnotDataType());
        assertEquals(2, fetchTreeKnot.getTreeEdges().size());
        firstTreeEdge = fetchTreeKnot.getTreeEdges().get(0);
        String firstEdgeId = fetchTreeKnot.getTreeEdges().get(0).getEdgeIdentifier().getId();
        secondTreeEdge = fetchTreeKnot.getTreeEdges().get(1);
        String secondEdgeId = fetchTreeKnot.getTreeEdges().get(1).getEdgeIdentifier().getId();
        assertNotNull(firstTreeEdge);
        assertNotNull(firstTreeEdge.getEdgeIdentifier().getId());
        assertEquals(1, firstTreeEdge.getEdgeIdentifier().getNumber());
        assertNotEquals(0, firstTreeEdge.getVersion());
        assertEquals("userId", firstTreeEdge.getFilters().get(0).getField());
        assertNotNull(secondTreeEdge);
        assertNotNull(secondTreeEdge.getEdgeIdentifier().getId());
        assertEquals(2, secondTreeEdge.getEdgeIdentifier().getNumber());
        assertNotEquals(0, secondTreeEdge.getVersion());
        assertEquals("userId", secondTreeEdge.getFilters().get(0).getField());
        firstTreeKnot = firstTreeEdge.getTreeKnot();
        String firstKnotId = firstTreeEdge.getTreeKnot().getId();
        secondTreeKnot = secondTreeEdge.getTreeKnot();
        String secondKnotId = secondTreeEdge.getTreeKnot().getId();
        assertNotNull(firstTreeKnot);
        assertNotNull(firstTreeKnot.getId());
        assertNotEquals(0, firstTreeKnot.getVersion());
        assertNotNull(firstTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, firstTreeKnot.getKnotData().getKnotDataType());
        assertNotNull(secondTreeKnot);
        assertNotNull(secondTreeKnot.getId());
        assertNotEquals(0, secondTreeKnot.getVersion());
        assertNotNull(secondTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, secondTreeKnot.getKnotData().getKnotDataType());

        // Third Insertion
        final Knot thirdKnot = Knot.builder()
                .id("thirdKnotId")
                .version(0)
                .knotData(ValuedKnotData.stringValue("Value :: 3"))
                .build();
        final KnotDeltaOperation thirdKnotDeltaOperation = new KnotDeltaOperation(thirdKnot);
        final Edge thirdEdge = Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("thirdEdgeId", 0, 2))
                .version(0)
                .knotId("thirdKnotId")
                .filter(new NotEqualsFilter("userId", "U5"))
                .build();
        final EdgeDeltaOperation thirdEdgeDeltaOperation = new EdgeDeltaOperation(thirdEdge);
        final OrderedList<EdgeIdentifier> edgeIdentifiers = new OrderedList<>();
        edgeIdentifiers.add(new EdgeIdentifier(secondEdgeId, 0, 2));
        edgeIdentifiers.add(new EdgeIdentifier("thirdEdgeId", 0, 2));
        final Knot rootKnotModifiedTwo = Knot.builder()
                .id(fetchTreeKnot.getId())
                .version(0)
                .knotData(ValuedKnotData.stringValue("Value :: 0"))
                .edges(edgeIdentifiers)
                .build();
        final KnotDeltaOperation rootKnotDeltaOperationModifiedTwo = new KnotDeltaOperation(rootKnotModifiedTwo);

        deltaOperationList.clear();
        deltaOperationList.add(rootKnotDeltaOperationModifiedTwo);
        deltaOperationList.add(thirdEdgeDeltaOperation);
        deltaOperationList.add(thirdKnotDeltaOperation);

        fetchTreeKnot = bonsai.applyDeltaOperations("key", deltaOperationList).getTreeKnot();

        assertNotNull(fetchTreeKnot);
        assertNotNull(fetchTreeKnot.getId());
        assertEquals(0, fetchTreeKnot.getVersion());
        assertNotNull(fetchTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, fetchTreeKnot.getKnotData().getKnotDataType());
        assertEquals(2, fetchTreeKnot.getTreeEdges().size());
        firstTreeEdge = fetchTreeKnot.getTreeEdges().get(0);
        firstEdgeId = fetchTreeKnot.getTreeEdges().get(0).getEdgeIdentifier().getId();
        secondTreeEdge = fetchTreeKnot.getTreeEdges().get(1);
        secondEdgeId = fetchTreeKnot.getTreeEdges().get(1).getEdgeIdentifier().getId();
        assertNotNull(firstTreeEdge);
        assertNotNull(firstTreeEdge.getEdgeIdentifier().getId());
        assertEquals(2, firstTreeEdge.getEdgeIdentifier().getNumber());
        assertNotEquals(0, firstTreeEdge.getVersion());
        assertEquals("userId", firstTreeEdge.getFilters().get(0).getField());
        assertNotNull(secondTreeEdge);
        assertNotNull(secondTreeEdge.getEdgeIdentifier().getId());
        assertEquals(4, secondTreeEdge.getEdgeIdentifier().getNumber());
        assertEquals(0, secondTreeEdge.getVersion());
        assertEquals("userId", secondTreeEdge.getFilters().get(0).getField());
        firstTreeKnot = firstTreeEdge.getTreeKnot();
        firstKnotId = firstTreeEdge.getTreeKnot().getId();
        secondTreeKnot = secondTreeEdge.getTreeKnot();
        secondKnotId = secondTreeEdge.getTreeKnot().getId();
        assertNotNull(firstTreeKnot);
        assertNotNull(firstTreeKnot.getId());
        assertNotEquals(0, firstTreeKnot.getVersion());
        assertNotNull(firstTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, firstTreeKnot.getKnotData().getKnotDataType());
        assertNotNull(secondTreeKnot);
        assertEquals("thirdKnotId", secondTreeKnot.getId());
        assertEquals(0, secondTreeKnot.getVersion());
        assertNotNull(secondTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, secondTreeKnot.getKnotData().getKnotDataType());

        fetchTreeKnot = bonsai.getCompleteTree("key");

        assertNotNull(fetchTreeKnot);
        assertNotNull(fetchTreeKnot.getId());
        assertNotEquals(0, fetchTreeKnot.getVersion());
        assertNotNull(fetchTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, fetchTreeKnot.getKnotData().getKnotDataType());
        assertEquals(2, fetchTreeKnot.getTreeEdges().size());
        firstTreeEdge = fetchTreeKnot.getTreeEdges().get(0);
        secondTreeEdge = fetchTreeKnot.getTreeEdges().get(1);
        assertNotNull(firstTreeEdge);
        assertNotNull(firstTreeEdge.getEdgeIdentifier().getId());
        assertEquals(2, firstTreeEdge.getEdgeIdentifier().getNumber());
        assertNotEquals(0, firstTreeEdge.getVersion());
        assertEquals("userId", firstTreeEdge.getFilters().get(0).getField());
        assertNotNull(secondTreeEdge);
        assertNotNull(secondTreeEdge.getEdgeIdentifier().getId());
        assertEquals(3, secondTreeEdge.getEdgeIdentifier().getNumber());
        assertNotEquals(0, secondTreeEdge.getVersion());
        assertEquals("userId", secondTreeEdge.getFilters().get(0).getField());
        firstTreeKnot = firstTreeEdge.getTreeKnot();
        secondTreeKnot = secondTreeEdge.getTreeKnot();
        secondKnotId = secondTreeEdge.getTreeKnot().getId();
        assertNotNull(firstTreeKnot);
        assertEquals(firstKnotId, firstTreeKnot.getId());
        assertNotEquals(0, firstTreeKnot.getVersion());
        assertNotNull(firstTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, firstTreeKnot.getKnotData().getKnotDataType());
        assertNotNull(secondTreeKnot);
        assertEquals(secondKnotId, secondTreeKnot.getId());
        assertNotEquals(0, secondTreeKnot.getVersion());
        assertNotNull(secondTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, secondTreeKnot.getKnotData().getKnotDataType());
    }

    /**
     * Adding the documentation to explain what we are trying to achieve here.
     * <p>
     * 1. Add a new tree with KEY = "key" with its one variation
     * <p>
     * __________
     * |  VALUED  |
     * | Value::0 |
     * ----------
     * userId = U1     /
     * number = 1    /
     * ----------
     * |  VALUED  |
     * | Value::1 |
     * ----------
     * Now, validate it.
     * <p>
     * <p>
     * 2. Add "Value::2" node and check if any previous versions are not updating.
     * <p>
     * __________
     * |  VALUED  |
     * | Value::0 |
     * ----------
     * userId = U1     /         \ userId = U2
     * number = 1    /          \ number = 2
     * ----------      ----------
     * |  VALUED  |    |  VALUED  |
     * | Value::1 |    | Value::2 |
     * ----------      ----------
     * Now, validate it.
     */
    @Test
    void Given_BonsaiTreeAndDeltaOperationList_When_ApplyingDeltaOperationsOnTree_Then_ReturnFinalTreeKnotAndVerifyEdgeVersioning() {
        List<DeltaOperation> deltaOperationList = new ArrayList<>();
        final Knot firstKnot = Knot.builder()
                .id("firstKnotId")
                .version(0)
                .knotData(ValuedKnotData.stringValue("Value :: 1"))
                .build();
        final KnotDeltaOperation firstKnotDeltaOperation = new KnotDeltaOperation(firstKnot);
        final Edge firstEdge = Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("firstEdgeId", 1, 1))
                .version(0)
                .knotId("firstKnotId")
                .filter(new EqualsFilter("userId", "U1"))
                .build();
        final EdgeDeltaOperation firstEdgeDeltaOperation = new EdgeDeltaOperation(firstEdge);
        final OrderedList<EdgeIdentifier> edgeIdentifierOrderedList = new OrderedList<>();
        edgeIdentifierOrderedList.add(new EdgeIdentifier("firstEdgeId", 0, 1));
        final KeyMappingDeltaOperation keyMappingDeltaOperation = KeyMappingDeltaOperation.builder()
                .keyId("key")
                .knotId("rootKnotId")
                .build();
        final Knot rootKnot = Knot.builder()
                .id("rootKnotId")
                .version(0)
                .knotData(ValuedKnotData.stringValue("Value :: 0"))
                .edges(edgeIdentifierOrderedList)
                .build();
        KnotDeltaOperation rootKnotDeltaOperation = new KnotDeltaOperation(rootKnot);
        deltaOperationList.add(keyMappingDeltaOperation);
        deltaOperationList.add(rootKnotDeltaOperation);
        deltaOperationList.add(firstEdgeDeltaOperation);
        deltaOperationList.add(firstKnotDeltaOperation);

        TreeKnot fetchTreeKnot = bonsai.applyDeltaOperations("key", deltaOperationList).getTreeKnot();

        assertNotNull(fetchTreeKnot);
        assertNotNull(fetchTreeKnot.getId());
        assertEquals(0, fetchTreeKnot.getVersion());
        assertNotNull(fetchTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, fetchTreeKnot.getKnotData().getKnotDataType());
        assertEquals(1, fetchTreeKnot.getTreeEdges().size());
        TreeEdge firstTreeEdge = fetchTreeKnot.getTreeEdges().get(0);
        assertNotNull(firstTreeEdge);
        assertNotNull(firstTreeEdge.getEdgeIdentifier().getId());
        assertEquals(1, firstTreeEdge.getEdgeIdentifier().getNumber());
        assertEquals(0, firstTreeEdge.getVersion());
        assertEquals("userId", firstTreeEdge.getFilters().get(0).getField());
        TreeKnot firstTreeKnot = firstTreeEdge.getTreeKnot();
        assertNotNull(firstTreeKnot);
        assertEquals("firstKnotId", firstTreeKnot.getId());
        assertEquals(0, firstTreeKnot.getVersion());
        assertNotNull(firstTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, firstTreeKnot.getKnotData().getKnotDataType());

        fetchTreeKnot = bonsai.getCompleteTree("key");

        assertNotNull(fetchTreeKnot);
        assertNotNull(fetchTreeKnot.getId());
        assertNotEquals(0, fetchTreeKnot.getVersion());
        assertNotNull(fetchTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, fetchTreeKnot.getKnotData().getKnotDataType());
        assertEquals(1, fetchTreeKnot.getTreeEdges().size());
        firstTreeEdge = fetchTreeKnot.getTreeEdges().get(0);
        final long firstTreeEdgeVersion = firstTreeEdge.getVersion(); // Will be compared later.
        assertNotNull(firstTreeEdge);
        assertNotNull(firstTreeEdge.getEdgeIdentifier().getId());
        assertEquals(1, firstTreeEdge.getEdgeIdentifier().getNumber());
        assertNotEquals(0, firstTreeEdge.getVersion());
        assertEquals("userId", firstTreeEdge.getFilters().get(0).getField());
        firstTreeKnot = firstTreeEdge.getTreeKnot();
        final long firstTreeKnotVersion = firstTreeKnot.getVersion();
        assertNotNull(firstTreeKnot);
        assertNotNull(firstTreeKnot.getId());
        assertNotEquals(0, firstTreeKnot.getVersion());
        assertNotNull(firstTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, firstTreeKnot.getKnotData().getKnotDataType());

        // Second knot inserted.
        final Knot secondKnot = Knot.builder()
                .id("secondKnotId")
                .version(0)
                .knotData(ValuedKnotData.stringValue("Value :: 2"))
                .build();
        final KnotDeltaOperation secondKnotDeltaOperation = new KnotDeltaOperation(secondKnot);
        final Edge secondEdge = Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("secondEdgeId", 2, 2))
                .version(0)
                .knotId("secondKnotId")
                .filter(new EqualsFilter("userId", "U2"))
                .build();
        final EdgeDeltaOperation secondEdgeDeltaOperation = new EdgeDeltaOperation(secondEdge);
        final OrderedList<EdgeIdentifier> edgeIdentifiers = new OrderedList<>();
        edgeIdentifiers.add(new EdgeIdentifier(firstTreeEdge.getEdgeIdentifier().getId(), 0, 1));
        edgeIdentifiers.add(new EdgeIdentifier("secondEdgeId", 0, 1));
        final Knot rootKnotModified = Knot.builder()
                .id(fetchTreeKnot.getId())
                .version(0)
                .knotData(ValuedKnotData.stringValue("Value :: 0"))
                .edges(edgeIdentifiers)
                .build();
        KnotDeltaOperation rootKnotDeltaOperationModified = new KnotDeltaOperation(rootKnotModified);

        deltaOperationList.clear();
        deltaOperationList.add(rootKnotDeltaOperationModified);
        deltaOperationList.add(secondEdgeDeltaOperation);
        deltaOperationList.add(secondKnotDeltaOperation);

        fetchTreeKnot = bonsai.applyDeltaOperations("key", deltaOperationList).getTreeKnot();

        assertNotNull(fetchTreeKnot);
        assertNotNull(fetchTreeKnot.getId());
        assertEquals(0, fetchTreeKnot.getVersion());
        assertNotNull(fetchTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, fetchTreeKnot.getKnotData().getKnotDataType());
        assertEquals(2, fetchTreeKnot.getTreeEdges().size());
        firstTreeEdge = fetchTreeKnot.getTreeEdges().get(0);
        TreeEdge secondTreeEdge = fetchTreeKnot.getTreeEdges().get(1);
        assertNotNull(firstTreeEdge);
        assertNotNull(firstTreeEdge.getEdgeIdentifier().getId());
        assertEquals(1, firstTreeEdge.getEdgeIdentifier().getNumber());
        assertEquals(firstTreeEdgeVersion, firstTreeEdge.getVersion()); // This should be equal to its previous version.
        assertEquals("userId", firstTreeEdge.getFilters().get(0).getField());
        assertNotNull(secondTreeEdge);
        assertNotNull(secondTreeEdge.getEdgeIdentifier().getId());
        assertEquals(3, secondTreeEdge.getEdgeIdentifier().getNumber());
        assertEquals(0, secondTreeEdge.getVersion());
        assertEquals("userId", secondTreeEdge.getFilters().get(0).getField());
        firstTreeKnot = firstTreeEdge.getTreeKnot();
        TreeKnot secondTreeKnot = secondTreeEdge.getTreeKnot();
        assertNotNull(firstTreeKnot);
        assertNotNull(firstTreeKnot.getId());
        assertEquals(firstTreeKnotVersion, firstTreeKnot.getVersion()); // This should be equal to its previous version.
        assertNotNull(firstTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, firstTreeKnot.getKnotData().getKnotDataType());
        assertNotNull(secondTreeKnot);
        assertEquals("secondKnotId", secondTreeKnot.getId());
        assertEquals(0, secondTreeKnot.getVersion());
        assertNotNull(secondTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, secondTreeKnot.getKnotData().getKnotDataType());

        fetchTreeKnot = bonsai.getCompleteTree("key");

        assertNotNull(fetchTreeKnot);
        assertNotNull(fetchTreeKnot.getId());
        assertNotNull(fetchTreeKnot.getVersion());
        assertNotNull(fetchTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, fetchTreeKnot.getKnotData().getKnotDataType());
        assertEquals(2, fetchTreeKnot.getTreeEdges().size());
        firstTreeEdge = fetchTreeKnot.getTreeEdges().get(0);
        secondTreeEdge = fetchTreeKnot.getTreeEdges().get(1);
        assertNotNull(firstTreeEdge);
        assertNotNull(firstTreeEdge.getEdgeIdentifier().getId());
        assertEquals(1, firstTreeEdge.getEdgeIdentifier().getNumber());
        assertEquals(firstTreeEdgeVersion, firstTreeEdge.getVersion()); // This should be equal to its previous version.
        assertEquals("userId", firstTreeEdge.getFilters().get(0).getField());
        assertNotNull(secondTreeEdge);
        assertNotNull(secondTreeEdge.getEdgeIdentifier().getId());
        assertEquals(2, secondTreeEdge.getEdgeIdentifier().getNumber());
        assertNotEquals(0, secondTreeEdge.getVersion());
        assertEquals("userId", secondTreeEdge.getFilters().get(0).getField());
        firstTreeKnot = firstTreeEdge.getTreeKnot();
        secondTreeKnot = secondTreeEdge.getTreeKnot();
        assertNotNull(firstTreeKnot);
        assertNotNull(firstTreeKnot.getId());
        assertEquals(firstTreeKnotVersion, firstTreeKnot.getVersion()); // This should be equal to its previous version.
        assertNotNull(firstTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, firstTreeKnot.getKnotData().getKnotDataType());
        assertNotNull(secondTreeKnot);
        assertNotNull(secondTreeKnot.getId());
        assertNotEquals(0, secondTreeKnot.getVersion());
        assertNotNull(secondTreeKnot.getKnotData());
        assertEquals(KnotData.KnotDataType.VALUED, secondTreeKnot.getKnotData().getKnotDataType());
    }

    /**
     * Adding the documentation to explain what we are trying to achieve here.
     * <p>
     * 1. Add a new tree with KEY = "key" with its two variations.
     * <p>
     * __________
     * |  VALUED  |
     * | Value::0 |
     * ----------
     * userId = U1     /         \ userId = U2
     * number = 1    /          \ number = 2
     * ----------      ----------
     * |  VALUED  |    |  VALUED  |
     * | Value::1 |    | Value::2 |
     * ----------      ----------
     * Now, validate it and capture its revert DeltaOperation List (Let us call it RD1)
     * <p>
     * <p>
     * 2. Add a new variation to under Value::1 knots, delete Value::2 knot and add new variation Value::3 under Value::0 knot.
     * <p>
     * __________
     * |  VALUED  |
     * | Value::0 |
     * ----------
     * userId = U1     /         \ userId = U3
     * number = 1    /          \ number = 3
     * ----------      ----------
     * |  VALUED  |    |  VALUED  |
     * | Value::1 |    | Value::3 |
     * ----------      ----------
     * profileId = P1    |
     * number = 1      |
     * ----------
     * |  VALUED  |
     * | Value::4 |
     * ----------
     * Now, validate it and capture its revert DeltaOperation List (Let us call it RD2)
     * <p>
     * <p>
     * 3. Now apply the RD2 on the top-most snapshot and validate if it is equivalent to the first copy.
     */
    @Test
    void Given_BonsaiTreeAndDeltaOperationList_When_ApplyingDeltaOperationsOnTree_Then_ReturnFinalTreeKnotAndVerifyAndApplyRevertDeltaOperationList() {

        final String knotIdOne = "knotIdOne";
        final KnotData knotDataOne = ValuedKnotData.builder().value(new StringValue("Value::1")).build();
        final Knot knotOne = Knot.builder()
                .id(knotIdOne)
                .version(0)
                .knotData(knotDataOne)
                .edges(null)
                .build();
        final KnotDeltaOperation knotDeltaOperationOne = new KnotDeltaOperation(knotOne);

        final String knotIdTwo = "knotIdTwo";
        final KnotData knotDataTwo = ValuedKnotData.stringValue("Value::2");
        final Knot knotTwo = Knot.builder()
                .id(knotIdTwo)
                .version(0)
                .knotData(knotDataTwo)
                .edges(null)
                .build();
        final KnotDeltaOperation knotDeltaOperationTwo = new KnotDeltaOperation(knotTwo);

        final EdgeIdentifier edgeIdentifierOne = new EdgeIdentifier("edgeOne", 1, 1);
        final Filter filterOne = EqualsFilter.builder()
                .field("userId")
                .value("U1")
                .build();
        final Edge edgeOne = Edge.builder()
                .edgeIdentifier(edgeIdentifierOne)
                .version(0)
                .filter(filterOne)
                .knotId(knotIdOne)
                .build();
        final EdgeDeltaOperation edgeDeltaOperationOne = new EdgeDeltaOperation(edgeOne);

        final EdgeIdentifier edgeIdentifierTwo = new EdgeIdentifier("edgeTwo", 2, 2);
        final Filter filterTwo = EqualsFilter.builder()
                .field("userId")
                .value("U2")
                .build();
        final Edge edgeTwo = Edge.builder()
                .edgeIdentifier(edgeIdentifierTwo)
                .version(0)
                .filter(filterTwo)
                .knotId(knotIdTwo)
                .build();
        final EdgeDeltaOperation edgeDeltaOperationTwo = new EdgeDeltaOperation(edgeTwo);

        final OrderedList<EdgeIdentifier> edgeIdentifierOrderedList = new OrderedList<>();
        edgeIdentifierOrderedList.add(edgeIdentifierOne);
        edgeIdentifierOrderedList.add(edgeIdentifierTwo);
        final String knotIdZero = "knotIdZero";
        final KnotData knotDataZero = ValuedKnotData.stringValue("Value::0");
        final Knot knotZero = Knot.builder()
                .id(knotIdZero)
                .version(0)
                .knotData(knotDataZero)
                .edges(edgeIdentifierOrderedList)
                .build();
        final KnotDeltaOperation knotDeltaOperationZero = new KnotDeltaOperation(knotZero);

        final KeyMappingDeltaOperation keyMappingDeltaOperation = new KeyMappingDeltaOperation("key", knotIdZero);

        final List<DeltaOperation> firstInputDeltaOperationList = new ArrayList<>();
        firstInputDeltaOperationList.add(keyMappingDeltaOperation);
        firstInputDeltaOperationList.add(knotDeltaOperationZero);
        firstInputDeltaOperationList.add(edgeDeltaOperationOne);
        firstInputDeltaOperationList.add(edgeDeltaOperationTwo);
        firstInputDeltaOperationList.add(knotDeltaOperationOne);
        firstInputDeltaOperationList.add(knotDeltaOperationTwo);

        TreeKnotState metaData = bonsai.applyDeltaOperations("key", firstInputDeltaOperationList);
        final TreeKnot firstTreeKnotSnapshot = metaData.getTreeKnot();
        final List<DeltaOperation> firstRevertDeltaOperationList = metaData.getDeltaOperationsToPreviousState();

        assertThat(firstTreeKnotSnapshot, is(notNullValue()));
        assertThat(firstTreeKnotSnapshot.getId(), is(knotIdZero));
        assertThat(firstTreeKnotSnapshot.getKnotData(), is(knotDataZero));
        assertThat(firstTreeKnotSnapshot.getTreeEdges().size(), is(2));
        TreeEdge fetchedTreeEdge = firstTreeKnotSnapshot.getTreeEdges().get(0);
        assertThat(fetchedTreeEdge.getEdgeIdentifier(), is(edgeIdentifierOne));
        assertThat(fetchedTreeEdge.getFilters().size(), is(1));
        assertThat(fetchedTreeEdge.getFilters().get(0), is(filterOne));
        TreeKnot fetchedTreeKnot = fetchedTreeEdge.getTreeKnot();
        assertThat(fetchedTreeKnot.getId(), is(knotIdOne));
        assertThat(fetchedTreeKnot.getKnotData(), is(knotDataOne));
        assertThat(fetchedTreeKnot.getTreeEdges(), is(empty()));
        fetchedTreeEdge = firstTreeKnotSnapshot.getTreeEdges().get(1);
        assertThat(fetchedTreeEdge.getEdgeIdentifier(), is(edgeIdentifierTwo));
        assertThat(fetchedTreeEdge.getFilters().size(), is(1));
        assertThat(fetchedTreeEdge.getFilters().get(0), is(filterTwo));
        fetchedTreeKnot = fetchedTreeEdge.getTreeKnot();
        assertThat(fetchedTreeKnot.getId(), is(knotIdTwo));
        assertThat(fetchedTreeKnot.getKnotData(), is(knotDataTwo));
        assertThat(fetchedTreeKnot.getTreeEdges(), is(empty()));
        assertThat(firstRevertDeltaOperationList.size(),
                is(0)); // This will be empty, because we are trying to create base image.


        // Second insertion.
        TreeKnot updatedIdTreeKnot = bonsai.getCompleteTree("key");
        final String knotIdFour = "knotIdFour";
        final KnotData knotDataFour = ValuedKnotData.builder().value(new StringValue("Value::4")).build();
        final Knot knotFour = Knot.builder()
                .id(knotIdFour)
                .version(0)
                .knotData(knotDataFour)
                .edges(null)
                .build();
        final KnotDeltaOperation knotDeltaOperationFour = new KnotDeltaOperation(knotFour);

        final String knotIdThree = "knotIdThree";
        final KnotData knotDataThree = ValuedKnotData.builder().value(new StringValue("Value::3")).build();
        final Knot knotThree = Knot.builder()
                .id(knotIdThree)
                .version(0)
                .knotData(knotDataThree)
                .edges(null)
                .build();
        final KnotDeltaOperation knotDeltaOperationThree = new KnotDeltaOperation(knotThree);

        final EdgeIdentifier edgeIdentifierFour = new EdgeIdentifier("edgeFour", 1, 1);
        final Filter filterFour = EqualsFilter.builder()
                .field("profileId")
                .value("P1")
                .build();
        final Edge edgeFour = Edge.builder()
                .edgeIdentifier(edgeIdentifierFour)
                .version(0)
                .filter(filterFour)
                .knotId(knotIdFour)
                .build();
        final EdgeDeltaOperation edgeDeltaOperationFour = new EdgeDeltaOperation(edgeFour);

        final EdgeIdentifier edgeIdentifierThree = new EdgeIdentifier("edgeThree", 3, 3);
        final Filter filterThree = EqualsFilter.builder()
                .field("userId")
                .value("U3")
                .build();
        final Edge edgeThree = Edge.builder()
                .edgeIdentifier(edgeIdentifierThree)
                .version(0)
                .filter(filterThree)
                .knotId(knotIdThree)
                .build();
        final EdgeDeltaOperation edgeDeltaOperationThree = new EdgeDeltaOperation(edgeThree);

        final OrderedList<EdgeIdentifier> edgeIdentifiersInsideKnotOne = new OrderedList<>();
        edgeIdentifiersInsideKnotOne.add(edgeIdentifierFour);
        final String knotIdOneUpdated = updatedIdTreeKnot.getTreeEdges().get(0).getTreeKnot().getId();
        final Knot knotOneUpdated = Knot.builder()
                .id(knotIdOneUpdated)
                .version(0)
                .knotData(knotDataOne)
                .edges(edgeIdentifiersInsideKnotOne)
                .build();
        final KnotDeltaOperation knotDeltaOperationOneUpdated = new KnotDeltaOperation(knotOneUpdated);

        final OrderedList<EdgeIdentifier> edgeIdentifiersInsideKnotZero = new OrderedList<>();
        final EdgeIdentifier edgeIdentifierOneUpdate = updatedIdTreeKnot.getTreeEdges().get(0).getEdgeIdentifier();
        edgeIdentifiersInsideKnotZero.add(edgeIdentifierOneUpdate);
        edgeIdentifiersInsideKnotZero.add(edgeIdentifierThree);
        final KnotData knotDataZeroUpdated = ValuedKnotData.stringValue("Value::0");
        final String knotIdZeroUpdated = updatedIdTreeKnot.getId();
        final Knot knotZeroUpdated = Knot.builder()
                .id(knotIdZeroUpdated)
                .version(0)
                .knotData(knotDataZeroUpdated)
                .edges(edgeIdentifiersInsideKnotZero)
                .build();
        final KnotDeltaOperation knotDeltaOperationZeroUpdated = new KnotDeltaOperation(knotZeroUpdated);

        final List<DeltaOperation> secondInputDeltaOperationList = new ArrayList<>();
        secondInputDeltaOperationList.add(knotDeltaOperationZeroUpdated);
        secondInputDeltaOperationList.add(edgeDeltaOperationThree);
        secondInputDeltaOperationList.add(knotDeltaOperationThree);
        secondInputDeltaOperationList.add(knotDeltaOperationOneUpdated);
        secondInputDeltaOperationList.add(edgeDeltaOperationFour);
        secondInputDeltaOperationList.add(knotDeltaOperationFour);

        metaData = bonsai.applyDeltaOperations("key", secondInputDeltaOperationList);
        final TreeKnot secondTreeKnotSnapshot = metaData.getTreeKnot();
        final List<DeltaOperation> secondRevertDeltaOperationList = metaData.getDeltaOperationsToPreviousState();

        // Add assert function here.
        assertThat(secondTreeKnotSnapshot, is(notNullValue()));
        assertThat(secondTreeKnotSnapshot.getId(), is(knotIdZeroUpdated));
        assertThat(secondTreeKnotSnapshot.getKnotData(), is(knotDataZeroUpdated));
        assertThat(secondTreeKnotSnapshot.getTreeEdges().size(), is(2));
        fetchedTreeEdge = secondTreeKnotSnapshot.getTreeEdges().get(0);
        assertThat(fetchedTreeEdge.getEdgeIdentifier(), is(edgeIdentifierOneUpdate));
        assertThat(fetchedTreeEdge.getFilters().size(), is(1));
        assertThat(fetchedTreeEdge.getFilters().get(0), is(filterOne));
        fetchedTreeKnot = fetchedTreeEdge.getTreeKnot();
        assertThat(fetchedTreeKnot.getId(), is(knotIdOneUpdated));
        assertThat(fetchedTreeKnot.getKnotData(), is(knotDataOne));
        assertThat(fetchedTreeKnot.getTreeEdges().size(), is(1));
        fetchedTreeEdge = fetchedTreeKnot.getTreeEdges().get(0);
        assertThat(fetchedTreeEdge.getEdgeIdentifier(), is(edgeIdentifierFour));
        assertThat(fetchedTreeEdge.getFilters().size(), is(1));
        assertThat(fetchedTreeEdge.getFilters().get(0), is(filterFour));
        fetchedTreeKnot = fetchedTreeEdge.getTreeKnot();
        assertThat(fetchedTreeKnot.getId(), is(knotIdFour));
        assertThat(fetchedTreeKnot.getKnotData(), is(knotDataFour));
        assertThat(fetchedTreeKnot.getTreeEdges(), is(empty()));
        fetchedTreeEdge = secondTreeKnotSnapshot.getTreeEdges().get(1);
        assertThat(fetchedTreeEdge.getEdgeIdentifier(), is(edgeIdentifierThree));
        assertThat(fetchedTreeEdge.getFilters().size(), is(1));
        assertThat(fetchedTreeEdge.getFilters().get(0), is(filterThree));
        fetchedTreeKnot = fetchedTreeEdge.getTreeKnot();
        assertThat(fetchedTreeKnot.getId(), is(knotIdThree));
        assertThat(fetchedTreeKnot.getKnotData(), is(knotDataThree));
        assertThat(fetchedTreeKnot.getTreeEdges(), is(empty()));

        // Time to assert secondRevertDeltaOperationList
        assertThat(secondRevertDeltaOperationList.size(), is(4));
        KnotDeltaOperation fetchedKnotDeltaOperation = (KnotDeltaOperation) secondRevertDeltaOperationList.get(0);
        Knot fetchedKnot = fetchedKnotDeltaOperation.getKnot();
        assertThat(fetchedKnot.getId(), is(updatedIdTreeKnot.getId()));
        assertThat(fetchedKnot.getKnotData(), is(updatedIdTreeKnot.getKnotData()));
        EdgeDeltaOperation fetchedEdgeDeltaOperation = (EdgeDeltaOperation) secondRevertDeltaOperationList.get(1);
        Edge fetchedEdge = fetchedEdgeDeltaOperation.getEdge();
        assertThat(fetchedEdge.getEdgeIdentifier(), is(updatedIdTreeKnot.getTreeEdges().get(1).getEdgeIdentifier()));
        assertThat(fetchedEdge.getFilters(), is(edgeTwo.getFilters()));
        fetchedKnotDeltaOperation = (KnotDeltaOperation) secondRevertDeltaOperationList.get(2);
        fetchedKnot = fetchedKnotDeltaOperation.getKnot();
        assertThat(fetchedKnot.getId(), is(updatedIdTreeKnot.getTreeEdges().get(1).getTreeKnot().getId()));
        assertThat(fetchedKnot.getKnotData(), is(knotTwo.getKnotData()));
        assertThat(fetchedKnot.getEdges(), is(empty()));
        fetchedKnotDeltaOperation = (KnotDeltaOperation) secondRevertDeltaOperationList.get(3);
        fetchedKnot = fetchedKnotDeltaOperation.getKnot();
        assertThat(fetchedKnot.getId(), is(knotIdOneUpdated));
        assertThat(fetchedKnot.getKnotData(), is(knotOne.getKnotData()));
        assertThat(fetchedKnot.getEdges(), is(empty()));

        // Apply secondRevertDeltaOperationList on the latest tree image to get the firstTreeKnotSnapshot.
        metaData = bonsai.applyDeltaOperations("key", secondRevertDeltaOperationList);
        final TreeKnot thirdTreeKnotSnapshot = metaData.getTreeKnot();
        final List<DeltaOperation> thirdRevertDeltaOperationList = metaData.getDeltaOperationsToPreviousState();

        assertThat(thirdTreeKnotSnapshot, is(notNullValue()));
        assertThat(thirdTreeKnotSnapshot.getId(), is(knotIdZeroUpdated));
        assertThat(thirdTreeKnotSnapshot.getTreeEdges().size(), is(2));
        fetchedTreeEdge = thirdTreeKnotSnapshot.getTreeEdges().get(0);
        assertThat(fetchedTreeEdge.getFilters().size(), is(1));
        assertThat(fetchedTreeEdge.getFilters().get(0), is(filterOne));
        fetchedTreeKnot = fetchedTreeEdge.getTreeKnot();
        assertThat(fetchedTreeKnot.getId(), is(knotIdOneUpdated));
        assertThat(fetchedTreeKnot.getKnotData(), is(knotDataOne));
        assertThat(fetchedTreeKnot.getTreeEdges(), is(empty()));
        fetchedTreeEdge = thirdTreeKnotSnapshot.getTreeEdges().get(1);
        assertThat(fetchedTreeEdge.getFilters().size(), is(1));
        assertThat(fetchedTreeEdge.getFilters().get(0), is(filterTwo));
        fetchedTreeKnot = fetchedTreeEdge.getTreeKnot();
        assertThat(fetchedTreeKnot.getKnotData(), is(knotDataTwo));
        assertThat(fetchedTreeKnot.getTreeEdges(), is(empty()));

        // Assert on thirdRevertDeltaOperationList
        assertThat(thirdRevertDeltaOperationList.size(), is(6));
        fetchedKnotDeltaOperation = (KnotDeltaOperation) thirdRevertDeltaOperationList.get(0);
        assertThat(fetchedKnotDeltaOperation.getKnot().getKnotData(), is(knotDataZeroUpdated));
        fetchedEdgeDeltaOperation = (EdgeDeltaOperation) thirdRevertDeltaOperationList.get(1);
        assertThat(fetchedEdgeDeltaOperation.getEdge().getFilters().size(), is(1));
        assertThat(fetchedEdgeDeltaOperation.getEdge().getFilters().get(0), is(filterThree));
        fetchedKnotDeltaOperation = (KnotDeltaOperation) thirdRevertDeltaOperationList.get(2);
        assertThat(fetchedKnotDeltaOperation.getKnot().getKnotData(), is(knotDataThree));
        assertThat(fetchedKnotDeltaOperation.getKnot().getEdges(), is(empty()));
        fetchedKnotDeltaOperation = (KnotDeltaOperation) thirdRevertDeltaOperationList.get(3);
        assertThat(fetchedKnotDeltaOperation.getKnot().getId(), is(knotIdOneUpdated));
        assertThat(fetchedKnotDeltaOperation.getKnot().getKnotData(), is(knotDataOne));
        assertThat(fetchedKnotDeltaOperation.getKnot().getEdges().size(), is(1));
        fetchedEdgeDeltaOperation = (EdgeDeltaOperation) thirdRevertDeltaOperationList.get(4);
        assertThat(fetchedEdgeDeltaOperation.getEdge().getFilters().size(), is(1));
        assertThat(fetchedEdgeDeltaOperation.getEdge().getFilters().get(0), is(filterFour));
        fetchedKnotDeltaOperation = (KnotDeltaOperation) thirdRevertDeltaOperationList.get(5);
        assertThat(fetchedKnotDeltaOperation.getKnot().getKnotData(), is(knotDataFour));
        assertThat(fetchedKnotDeltaOperation.getKnot().getEdges(), is(empty()));
    }

    @Test
    void given_bonsaiTree_when_deleteKnot_then_returnNull() {
        final TreeKnot treeKnot = bonsai.deleteKnot("knotId", false);
        Assertions.assertNull(treeKnot, "TreeKnot should be null.");
    }

    @Test
    void given_bonsaiTree_when_evaluatingWithOnlyKeyToKnotMapping_then_returnEmptyNotNullKeyNode() {
        final Knot knot = bonsai.createKnot(ValuedKnotData.stringValue("Knot Data."), null);
        bonsai.createMapping("key", knot.getId());
        bonsai.deleteKnot(knot.getId(), false);

        final KeyNode keyNode = bonsai.evaluate("key",
                Context.builder().documentContext(Parsers.parse(ImmutableMap.of("E", 9333))).build());

        assertNotNull(keyNode, "KeyNode should not be null.");
        assertEquals("key", keyNode.getKey(), "KeyNode's key value should be : key");
        assertNull(keyNode.getNode(), "KeyNode's node should be null.");
        assertEquals(0, keyNode.getEdgePath().size(), "The size of KeyNode edgePath should be null.");
    }

    @Test
    void given_bonsaiTree_when_addingNonExistingVariation_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            Knot l1 = bonsai.createKnot(MultiKnotData.builder().key("w1").key("w2").build(), null);
            bonsai.createMapping("l1", l1.getId());
            bonsai.addVariation(l1.getId(), Variation.builder()
                    .filter(new EqualsFilter("$.gender", "female"))
                    .knotId("variationKnot")
                    .build());
        });
    }

    @Test
    void given_bonsaiTree_when_updatingEdgeFilters_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            Knot l1 = bonsai.createKnot(MultiKnotData.builder().key("w1").key("w2").build(), null);
            bonsai.updateVariation(l1.getId(), "edgeId", Variation.builder().build());
        });
    }

    @Test
    void given_bonsaiTreeWithDissimilarKnotData_when_evaluatingTree_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            Knot l1 = bonsai.createKnot(MultiKnotData.builder().key("w1").key("w2").build(), null);
            bonsai.createMapping("l1", l1.getId());
            Knot l21 = bonsai.createKnot(MultiKnotData.builder().key("l21w3").key("l21w4").build(), null);
            bonsai.addVariation(l1.getId(), Variation.builder()
                    .filter(new EqualsFilter("$.gender", "female"))
                    .knotId(l21.getId())
                    .build());
            Knot preferredKnot = Knot.builder()
                    .id("P1kaID")
                    .knotData(ValuedKnotData.stringValue("P-1"))
                    .build();
            bonsai.evaluate("l1",
                    Context.builder()
                            .documentContext(Parsers.parse(ImmutableMap.of("gender", "female")))
                            .preferences(ImmutableMap.of("l1", preferredKnot))
                            .build());
        });
    }

    @Test
    void given_bonsaiTree_when_evaluatingTreeWithOverShootingEdgesPerKnot_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            Knot knot = bonsai.createKnot(ValuedKnotData.stringValue("Data"), null);
            bonsai.createMapping("mera_data", knot.getId());
            TreeGenerationHelper.generateEdges(knot, bonsai, 10000);
            KeyNode evaluate = bonsai.evaluate("mera_data", Context.builder()
                    .documentContext(Parsers.parse(ImmutableMap.of("E", 9333)))
                    .build());
            Assertions.assertTrue(evaluate.getNode() instanceof ValueNode);
            Assertions.assertEquals("Data9333", ((StringValue) ((ValueNode) evaluate.getNode()).getValue()).getValue());
            System.out.println(evaluate);
        });
    }

    @Test
    void given_bonsaiTree_when_createMappingWithWrongKnotData_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            bonsai.createMapping("mera_data", new MapKnotData(), null);
        });
    }

    @Test
    void given_bonsaiTree_when_evaluatingTreeWithMaxEdgesPerKnot_then_returnEvaluatedKnot() {
        Bonsai<Context> bonsai = createNewInMemoryBonsai(
                BonsaiProperties.builder().mutualExclusivitySettingTurnedOn(true)
                        .maxAllowedVariationsPerKnot(10).build());

        Knot knot = bonsai.createKnot(ValuedKnotData.stringValue("Data"), null);
        bonsai.createMapping("mera_data", knot.getId());
        TreeGenerationHelper.generateEdges(knot, bonsai, 10);
        KeyNode evaluate = bonsai.evaluate("mera_data", Context.builder()
                .documentContext(Parsers.parse(ImmutableMap.of("E", 9333)))
                .build());
        Assertions.assertTrue(evaluate.getNode() instanceof ValueNode);
        Assertions.assertEquals("Data",
                ((StringValue) ((ValueNode) evaluate.getNode()).getValue()).getValue().toString());
        System.out.println(evaluate);
    }

    @Test
    void given_bonsaiTree_when_evaluatingTreeWithOneMaxthenMaxEdgesPerKnot_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            Bonsai<Context> bonsai = createNewInMemoryBonsai(
                    BonsaiProperties.builder().mutualExclusivitySettingTurnedOn(true)
                            .maxAllowedVariationsPerKnot(10).build());

            Knot knot = bonsai.createKnot(ValuedKnotData.stringValue("Data"), null);
            bonsai.createMapping("mera_data", knot.getId());
            TreeGenerationHelper.generateEdges(knot, bonsai, 11);
            KeyNode evaluate = bonsai.evaluate("mera_data", Context.builder()
                    .documentContext(Parsers.parse(ImmutableMap.of("E", 9333)))
                    .build());
            Assertions.assertTrue(evaluate.getNode() instanceof ValueNode);
            Assertions.assertEquals("Data9333", ((StringValue) ((ValueNode) evaluate.getNode()).getValue()).getValue()
                    .toString());
            System.out.println(evaluate);
        });
    }

    @Test
    void givenSelfCyclicMultiKnotDataWhenDetectingCycleOnBonsaiThenThrowException() {
        assertThrows(BonsaiError.class, () -> {
            try {
                bonsai.createMapping("key", MultiKnotData.builder().key("key").build(), null);
            } catch (BonsaiError e) {
                Assertions.assertEquals(BonsaiErrorCode.KNOT_ABSENT, e.getErrorCode());
                throw e;
            }
        });
    }

    @Test
    void givenSelfCyclicMapKnotDataWhenDetectingCycleOnBonsaiThenThrowException() {
        assertThrows(BonsaiError.class, () -> {
            try {
                bonsai.createMapping("key1", MapKnotData.builder().mapKeys(ImmutableMap.of("key", "key1")).build(),
                        null);
            } catch (BonsaiError e) {
                Assertions.assertEquals(BonsaiErrorCode.KNOT_ABSENT, e.getErrorCode());
                throw e;
            }
        });
    }

    @Test
    void givenCyclicInputWhenDetectingCycleOnBonsaiThenThrowException() {
        assertThrows(BonsaiError.class, () -> {
            final Knot knot = bonsai.createMapping("key", ValuedKnotData.stringValue("Value one"), null);

            try {
                final Edge edge = bonsai.addVariation(knot.getId(), Variation.builder()
                        .filter(EqualsFilter.builder().field("field").value("value").build())
                        .knotId(knot.getId()).build());
            } catch (BonsaiError e) {
                Assertions.assertEquals(BonsaiErrorCode.CYCLE_DETECTED, e.getErrorCode());
                throw e;
            }
        });
    }

    @Test
    void givenCyclicInputWithValuedKnotsWhenDetectingCycleOnBonsaiThenThrowException() {
        assertThrows(BonsaiError.class, () -> {
            final Knot knotOne = bonsai.createMapping("key", ValuedKnotData.numberValue(0), null);
            final Knot knotTwo = bonsai.createKnot(ValuedKnotData.numberValue(1), null);
            final Edge edgeOne = bonsai.addVariation(knotOne.getId(), Variation.builder()
                    .filter(EqualsFilter.builder().field("field").value("value").build())
                    .knotId(knotTwo.getId()).build());
            try {
                final Edge edgeTwo = bonsai.addVariation(knotTwo.getId(), Variation.builder()
                        .filter(EqualsFilter.builder().field("field2").value("value2").build())
                        .knotId(knotOne.getId()).build());
            } catch (BonsaiError e) {
                Assertions.assertEquals(BonsaiErrorCode.CYCLE_DETECTED, e.getErrorCode());
                throw e;
            }
        });
    }

    @Test
    void givenCyclicInputInMapKnotWhenDetectingCycleOnBonsaiThenThrowException() {
        assertThrows(BonsaiError.class, () -> {
            try {
                bonsai.createMapping("keyThree", ValuedKnotData.numberValue(1), null);
                final Knot knotTwo = bonsai.createMapping("keyTwo", MapKnotData.builder()
                        .mapKeys(ImmutableMap.of("key", "keyThree"))
                        .build(), null);
                final Knot knotOne = bonsai.createMapping("keyOne", MapKnotData.builder()
                        .mapKeys(ImmutableMap.of("key", "keyTwo"))
                        .build(), null);
                bonsai.updateKnotData(knotTwo.getId(), MapKnotData.builder()
                        .mapKeys(ImmutableMap.of("key", "keyOne"))
                        .build(), new HashMap<>());
            } catch (BonsaiError e) {
                Assertions.assertEquals(BonsaiErrorCode.CYCLE_DETECTED, e.getErrorCode());
                throw e;
            }
        });
    }

    @Test
    void givenTriangularInputVariationWhenDetectingCycleOnBonsaiThenSaveSuccessfully() {
        assertThrows(BonsaiError.class, () -> {
            bonsai.createMapping("key", ValuedKnotData.numberValue(1), null);
            bonsai.createMapping("hello", ValuedKnotData.stringValue("hello value"), null);
            final Knot knotThree =
                    bonsai.createMapping("world", MapKnotData.builder().mapKeys(ImmutableMap.of("key", "key")).build(),
                            null);
            final Knot knotOne = bonsai.createMapping("keyOne", MultiKnotData.builder().key("hello").build(), null);
            final Knot knotTwo = bonsai.createKnot(MultiKnotData.builder().key("world").build(), null);
            bonsai.addVariation(knotOne.getId(), Variation.builder()
                    .filter(EqualsFilter.builder().field("field").value("value").build())
                    .knotId(knotTwo.getId())
                    .build());
            try {
                bonsai.updateKnotData(knotThree.getId(),
                        MapKnotData.builder().mapKeys(ImmutableMap.of("key", "keyOne")).build(), new HashMap<>());
            } catch (BonsaiError e) {
                Assertions.assertEquals(BonsaiErrorCode.CYCLE_DETECTED, e.getErrorCode());
                throw e;
            }
        });
    }

    @Test
    void updatingPropertiesSuccessfully() {
        bonsai.createMapping("key", ValuedKnotData.numberValue(1), null);
        bonsai.createMapping("hello", ValuedKnotData.stringValue("hello value"), null);
        Knot knotOne = bonsai.createMapping("keyOne", MultiKnotData.builder().key("hello").build(), null);

        Assertions.assertEquals(0, knotOne.getProperties().size());

        final Map<String, Object> properties = new HashMap<>();
        final List<String> labels = new ArrayList<>();
        labels.add("TestKnot");
        labels.add("MultiKnot");
        properties.put("Label", labels);

        bonsai.updateKnotData(knotOne.getId(), knotOne.getKnotData(), properties);
        knotOne = bonsai.getKnot(knotOne.getId());

        Assertions.assertTrue(knotOne.getProperties().containsValue(labels));
    }

    @Test
    void modifyingPropertiesSuccessfully() {
        bonsai.createMapping("key", ValuedKnotData.numberValue(1), null);
        bonsai.createMapping("hello", ValuedKnotData.stringValue("hello value"), null);
        Knot knotOne = bonsai.createMapping("keyOne", MultiKnotData.builder().key("hello").build(), null);

        Assertions.assertEquals(0, knotOne.getProperties().size());

        final Map<String, Object> properties = new HashMap<>();
        final List<String> labels = new ArrayList<>();
        labels.add("TestKnot");
        properties.put("Label", labels);

        bonsai.updateKnotData(knotOne.getId(), knotOne.getKnotData(), properties);
        knotOne = bonsai.getKnot(knotOne.getId());

        Assertions.assertTrue(knotOne.getProperties().containsValue(labels));

        final List<String> newLabelList = new ArrayList<>(labels);
        newLabelList.add("New label");
        final Map<String, Object> updatedProperties = new HashMap<>();
        updatedProperties.put("Label", newLabelList);

        bonsai.updateKnotData(knotOne.getId(), knotOne.getKnotData(), updatedProperties);
        knotOne = bonsai.getKnot(knotOne.getId());

        Assertions.assertTrue(knotOne.getProperties().containsValue(newLabelList));
    }

    @Test
    void updatingPropertiesWithNull() {
        bonsai.createMapping("key", ValuedKnotData.numberValue(1), null);
        bonsai.createMapping("hello", ValuedKnotData.stringValue("hello value"), null);
        Knot knotOne = bonsai.createMapping("keyOne", MultiKnotData.builder().key("hello").build(), null);

        Assertions.assertEquals(0, knotOne.getProperties().size());

        final Map<String, Object> properties = new HashMap<>();
        final List<String> labels = new ArrayList<>();
        labels.add("TestKnot");
        properties.put("Label", labels);

        bonsai.updateKnotData(knotOne.getId(), knotOne.getKnotData(), properties);
        knotOne = bonsai.getKnot(knotOne.getId());

        Assertions.assertTrue(knotOne.getProperties().containsValue(labels));

        bonsai.updateKnotData(knotOne.getId(), knotOne.getKnotData(), null);
        knotOne = bonsai.getKnot(knotOne.getId());

        Assertions.assertTrue(knotOne.getProperties().containsValue(labels));
    }

    @Test
    void given_validBonsaiTree_when_getDeltaOperations_then_returnValidDeltaOperations() throws IOException {
        final var userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);

        var key = "example";
        var root = bonsai.createMapping(key, ValuedKnotData.stringValue(("Root knot Value 1")), null);
        var child11 = bonsai.createKnot(ValuedKnotData.stringValue(("Child 11")), null);
        var child12 = bonsai.createKnot(ValuedKnotData.stringValue(("Child 12")), null);

        bonsai.addVariation(root.getId(),
                Variation.builder().filter(new EqualsFilter("$.gender", "male")).knotId(child11.getId()).build());

        bonsai.addVariation(root.getId(),
                Variation.builder().filter(new EqualsFilter("$.gender", "female")).knotId(child12.getId()).build());

        var operations = bonsai.calculateDeltaOperations(key);


        var user1ExampleEvaluation = bonsai.evaluate(key, Context.builder()
                .documentContext(Parsers.parse(userContext1))
                .build());

        var newBonsai = createNewInMemoryBonsai(DEFAULT_PROPERTIES);

        // if you apply these on a new tree, the evaluation should be the same, the resulting tree should also be the same
        newBonsai.applyDeltaOperations(key, operations);
        var user1ExampleEvaluationAgain = bonsai.evaluate(key, Context.builder()
                .documentContext(Parsers.parse(userContext1))
                .build());

        assertEquals(user1ExampleEvaluation, user1ExampleEvaluationAgain);

        AssertionUtils.assertSame(bonsai.getCompleteTree(key), newBonsai.getCompleteTree(key), false);
    }


    @Test
    void given_validBonsaiTreeWithNothing_when_getDeltaOperations_then_returnEmptyOperations() {
        var key = "example";
        List<DeltaOperation> operations = bonsai.calculateDeltaOperations(key);
        assertEquals(0, operations.size());
    }
}