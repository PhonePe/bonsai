package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.JsonPath;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.Mapper;
import com.phonepe.platform.bonsai.core.ObjectExtractor;
import com.phonepe.platform.bonsai.core.TreeGenerationHelper;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.ListNode;
import com.phonepe.platform.bonsai.models.MapNode;
import com.phonepe.platform.bonsai.models.NodeVisitors;
import com.phonepe.platform.bonsai.models.ValueNode;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.blocks.delta.DeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KeyMappingDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KnotDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import com.phonepe.platform.bonsai.models.data.MapKnotData;
import com.phonepe.platform.bonsai.models.data.MultiKnotData;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.bonsai.models.value.StringValue;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import com.phonepe.platform.query.dsl.general.NotEqualsFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author tushar.naik
 * @version 1.0  23/08/18 - 12:57 PM
 */
public class BonsaiTreeTest {

    private Bonsai<Context> bonsai;

    @Before
    public void setUp() {
        bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(
                        BonsaiProperties
                                .builder()
                                .mutualExclusivitySettingTurnedOn(true)
                                .maxAllowedConditionsPerEdge(10)
                                .maxAllowedVariationsPerKnot(10)
                                .build())
                .build();
    }

    @After
    public void destroy() {
        bonsai = null;
    }

    @Test
    public void given_bonsaiTree_when_evaluatingTree_then_returnKeyNode() throws IOException {
        Map userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);
        Knot level1 = bonsai.createMapping("test", ValuedKnotData.stringValue("1"));
        Knot level21 = bonsai.createKnot(ValuedKnotData.stringValue("21"));
        Knot level22 = bonsai.createKnot(ValuedKnotData.stringValue("22"));
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
                                       .documentContext(JsonPath.parse(userContext1))
                                       .build());
        Assert.assertNotNull("KeyNode should not be null.", keyNode);
        Assert.assertEquals("The value of [keyNode.Key] should be : test.", "test", keyNode.getKey());
    }

    @Test
    public void given_bonsaiTree_when_evaluatingTreeHeavilyWithGenderFilter_then_returnKeyNode() throws IOException, BonsaiError {
        Map userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);
        Map userContext2 = new ObjectExtractor().getObject("userData2.json", Map.class);

        Knot hpKnot = bonsai.createKnot(MultiKnotData.builder()
                                                     .key("widget_1")
                                                     .key("widget_2")
                                                     .key("widget_3")
                                                     .build());

        bonsai.createMapping("home_page_1", hpKnot.getId());

        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                                                                  .key("icon_3")
                                                                  .key("icon_1")
                                                                  .key("icon_4")
                                                                  .build());

        Knot widgetKnot1 = bonsai.createKnot(MultiKnotData.builder()
                                                          .key("icon_1")
                                                          .key("icon_4")
                                                          .key("icon_2")
                                                          .key("icon_3")
                                                          .build());
        bonsai.createMapping("widget_1", widgetKnot1.getId());
        Assert.assertNotNull(femaleConditionKnot);

        Assert.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                                               .filter(new EqualsFilter("$.gender", "female"))
                                                                               .knotId(femaleConditionKnot.getId())
                                                                               .build()));

        KeyNode user1HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                                                                                .documentContext(JsonPath.parse(userContext1))
                                                                                .build());
        System.out.println(Mapper.MAPPER.writeValueAsString(user1HomePageEvaluation));

        Assert.assertEquals("home_page_1", user1HomePageEvaluation.getKey());
        Assert.assertEquals(user1HomePageEvaluation.getNode().getId(), hpKnot.getId());
        Assert.assertTrue(NodeVisitors.isList(user1HomePageEvaluation.getNode()));

        Assert.assertEquals(3, ((ListNode) user1HomePageEvaluation.getNode()).getNodes().size());
        Assert.assertTrue(NodeVisitors.isList(((ListNode) user1HomePageEvaluation.getNode()).getNodes()
                                                                                            .get(0)
                                                                                            .getNode()));

        Assert.assertEquals(4, ((ListNode) (((ListNode) user1HomePageEvaluation.getNode()).getNodes()
                                                                                          .get(0)
                                                                                          .getNode())).getNodes()
                                                                                                      .size());

        /* evaluate with context 2 */
        KeyNode user2HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                                                                                .documentContext(JsonPath.parse(userContext2))
                                                                                .build());


        Assert.assertEquals("home_page_1", user2HomePageEvaluation.getKey());
        Assert.assertEquals(hpKnot.getId(), user2HomePageEvaluation.getNode().getId());
        Assert.assertTrue(NodeVisitors.isList(user2HomePageEvaluation.getNode()));

        Assert.assertEquals(3, ((ListNode) user2HomePageEvaluation.getNode()).getNodes().size());
        Assert.assertTrue(NodeVisitors.isList(((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                                                                                            .get(0)
                                                                                            .getNode()));

        Assert.assertEquals(3, ((ListNode) (((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                                                                                          .get(0).getNode())).getNodes()
                                                                                                             .size());

        Assert.assertEquals((((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                                                                           .get(0)
                                                                           .getNode()).getId(), femaleConditionKnot.getId());

        System.out.println(Mapper.MAPPER.writeValueAsString(user2HomePageEvaluation));
    }

    @Test
    public void given_bonsaiTree_when_evaluatingTreeHeavilyWithLocationFilter_then_returnKeyNode() throws IOException, BonsaiError {
        Map userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);
        Map userContext2 = new ObjectExtractor().getObject("userData2.json", Map.class);

        Knot hpKnot = bonsai.createKnot(MultiKnotData.builder()
                                                     .key("widget_1")
                                                     .key("widget_2")
                                                     .key("widget_3")
                                                     .build());

        bonsai.createMapping("home_page_1", hpKnot.getId());

        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                                                                  .key("icon_3")
                                                                  .key("icon_1")
                                                                  .key("icon_4")
                                                                  .build());

        Knot widgetKnot1 = bonsai.createKnot(MultiKnotData.builder()
                                                          .key("icon_1")
                                                          .key("icon_4")
                                                          .key("icon_2")
                                                          .key("icon_3")
                                                          .build());
        bonsai.createMapping("widget_1", widgetKnot1.getId());
        Assert.assertNotNull(femaleConditionKnot);

        Assert.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                                               .filter(new EqualsFilter("$.location.tier", "tier2"))
                                                                               .knotId(femaleConditionKnot.getId())
                                                                               .build()));

        KeyNode user1HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                                                                                .documentContext(JsonPath.parse(userContext1))
                                                                                .build());
        System.out.println(Mapper.MAPPER.writeValueAsString(user1HomePageEvaluation));

        Assert.assertEquals("home_page_1", user1HomePageEvaluation.getKey());
        Assert.assertEquals(hpKnot.getId(), user1HomePageEvaluation.getNode().getId());
        Assert.assertTrue(NodeVisitors.isList(user1HomePageEvaluation.getNode()));

        Assert.assertEquals(3, ((ListNode) user1HomePageEvaluation.getNode()).getNodes().size());
        Assert.assertTrue(NodeVisitors.isList(((ListNode) user1HomePageEvaluation.getNode()).getNodes()
                                                                                            .get(0)
                                                                                            .getNode()));

        Assert.assertEquals(4, ((ListNode) (((ListNode) user1HomePageEvaluation.getNode()).getNodes()
                                                                                          .get(0)
                                                                                          .getNode())).getNodes()
                                                                                                      .size());

        /* evaluate with context 2 */
        KeyNode user2HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                                                                                .documentContext(JsonPath.parse(userContext2))
                                                                                .build());


        Assert.assertEquals("home_page_1", user2HomePageEvaluation.getKey());
        Assert.assertEquals(user2HomePageEvaluation.getNode().getId(), hpKnot.getId());
        Assert.assertTrue(NodeVisitors.isList(user2HomePageEvaluation.getNode()));

        Assert.assertEquals(3, ((ListNode) user2HomePageEvaluation.getNode()).getNodes().size());
        Assert.assertTrue(NodeVisitors.isList(((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                                                                                            .get(0)
                                                                                            .getNode()));

        Assert.assertEquals(3, ((ListNode) (((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                                                                                          .get(0)
                                                                                          .getNode())).getNodes()
                                                                                                      .size());

        Assert.assertEquals((((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                                                                           .get(0)
                                                                           .getNode()).getId(), femaleConditionKnot.getId());

        System.out.println(Mapper.MAPPER.writeValueAsString(user2HomePageEvaluation));
    }

    @Test
    public void given_bonsaiTree_when_evaluatingTreeHeavilyWithMapKnot_then_returnKeyNode() throws IOException, BonsaiError {
        Map userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);

        Knot homePageKnot = bonsai.createKnot(MapKnotData.builder()
                                                         .mapKeys(ImmutableMap.of("w1", "widget_1",
                                                                                  "w2", "widget_2",
                                                                                  "w3", "widget_3",
                                                                                  "w4", "widget_4"))
                                                         .build());
        Assert.assertNull(bonsai.createMapping("home_page_1", homePageKnot.getId()));
        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                                                                  .key("icon_3")
                                                                  .key("icon_1")
                                                                  .key("icon_4")
                                                                  .build());

        Knot widgetKnot1 = bonsai.createKnot(MultiKnotData.builder()
                                                          .key("icon_1")
                                                          .key("icon_4")
                                                          .key("icon_2")
                                                          .key("icon_3")
                                                          .build());

        bonsai.createMapping("widget_1", widgetKnot1.getId());
        Knot icon3 = bonsai.createKnot(ValuedKnotData.stringValue(("This is some coool icon")));
        /* there is no older mapping, hence it will return null */
        Knot icon_3 = bonsai.createMapping("icon_3", icon3.getId());
        Assert.assertNull(icon_3);

        /* now mappings exist, hence it will return the older knot */
        Knot icon_3_1 = bonsai.createMapping("icon_3", icon3.getId());
        Assert.assertNotNull(icon_3_1);
        Assert.assertEquals(icon3, icon_3_1);

        Assert.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                                               .filter(new EqualsFilter("$.gender", "female"))
                                                                               .knotId(femaleConditionKnot.getId())
                                                                               .build()));

        KeyNode user1HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                                                                                .documentContext(JsonPath.parse(userContext1))
                                                                                .build());
        System.out.println(Mapper.MAPPER.writeValueAsString(user1HomePageEvaluation));

        Assert.assertEquals("home_page_1", user1HomePageEvaluation.getKey());
        Assert.assertEquals(homePageKnot.getId(), user1HomePageEvaluation.getNode().getId());
        Assert.assertTrue(NodeVisitors.isMap(user1HomePageEvaluation.getNode()));

        Assert.assertEquals(4, ((MapNode) user1HomePageEvaluation.getNode()).getNodeMap().size());
        Assert.assertTrue(NodeVisitors.isList(((MapNode) user1HomePageEvaluation.getNode()).getNodeMap()
                                                                                           .get("w1")
                                                                                           .getNode()));

        Assert.assertEquals(4, ((ListNode) (((MapNode) user1HomePageEvaluation.getNode()).getNodeMap()
                                                                                         .get("w1")
                                                                                         .getNode())).getNodes()
                                                                                                     .size());
    }

    @Test
    public void given_bonsaiTree_when_evaluatingTree_then_returnPreferredKeyNode() {
        Knot l1 = bonsai.createKnot(ValuedKnotData.stringValue("L-1"));
        bonsai.createMapping("baseKey", l1.getId());

        Knot l21 = bonsai.createKnot(ValuedKnotData.stringValue("L-2-1"));

        bonsai.addVariation(l1.getId(), Variation.builder()
                                                 .filter(new EqualsFilter("$.gender", "female"))
                                                 .knotId(l21.getId())
                                                 .build());

        KeyNode nonPreferencialEval = bonsai.evaluate("baseKey", Context.builder()
                                                                        .documentContext(JsonPath.parse(ImmutableMap.of("gender", "female")))
                                                                        .build());
        Assert.assertEquals(l21.getId(), nonPreferencialEval.getNode().getId());
        Assert.assertEquals("L-2-1", ((StringValue) ((ValueNode) nonPreferencialEval.getNode()).getValue()).getValue());
        Knot preferredKnot = Knot.builder()
                                 .id("P1kaID")
                                 .knotData(ValuedKnotData.stringValue("P-1"))
                                 .build();
        KeyNode preferentialEval = bonsai.evaluate
                ("baseKey",
                 Context.builder()
                        .documentContext(JsonPath.parse(ImmutableMap.of("gender", "female")))
                        .preferences(ImmutableMap.of("baseKey", preferredKnot))
                        .build());
        Assert.assertEquals(preferredKnot.getId(), preferentialEval.getNode().getId());
        Assert.assertEquals(((StringValue) ((ValuedKnotData) preferredKnot.getKnotData()).getValue()).getValue(),
                            ((StringValue) ((ValueNode) preferentialEval.getNode()).getValue()).getValue());
    }

    @Test
    public void given_bonsaiTree_when_evaluatingTree_then_returnRecursivePreferredKeyNode() {
        Knot l1 = bonsai.createKnot(MultiKnotData.builder().key("w1").key("w2").key("w3").build());
        bonsai.createMapping("l1", l1.getId());

        Knot l21 = bonsai.createKnot(MultiKnotData.builder().key("w3").key("w2").key("w1").build());
        Knot l22 = bonsai.createKnot(MultiKnotData.builder().key("w1").key("w3").build());

        bonsai.createMapping("l1", l1.getId());

        bonsai.createMapping("w1", ValuedKnotData.stringValue("widget1"));
        bonsai.createMapping("w2", ValuedKnotData.stringValue("widget2"));
        bonsai.createMapping("w3", ValuedKnotData.stringValue("widget3"));

        bonsai.addVariation(l1.getId(), Variation.builder()
                                                 .filter(new EqualsFilter("$.gender", "female"))
                                                 .knotId(l21.getId())
                                                 .build());

        bonsai.addVariation(l1.getId(), Variation.builder()
                                                 .filter(new EqualsFilter("$.gender", "male"))
                                                 .knotId(l22.getId())
                                                 .build());

        KeyNode nonPreferencialEval = bonsai.evaluate("l1", Context.builder()
                                                                   .documentContext(JsonPath.parse(ImmutableMap.of("gender", "female")))
                                                                   .build());
        Assert.assertEquals(l21.getId(), nonPreferencialEval.getNode().getId());
        Knot preferredKnot = Knot.builder()
                                 .id("P1kaID")
                                 .knotData(MultiKnotData.builder().key("w3").key("w1").build())
                                 .build();
        KeyNode preferentialEval = bonsai.evaluate
                ("l1",
                 Context.builder()
                        .documentContext(JsonPath.parse(ImmutableMap.of("gender", "female")))
                        .preferences(ImmutableMap.of("l1", preferredKnot))
                        .build());
        Assert.assertEquals(preferredKnot.getId(), preferentialEval.getNode().getId());
        Assert.assertEquals("widget3", ((StringValue) ((ValueNode) ((ListNode) preferentialEval.getNode())
                .getNodes().get(0).getNode()).getValue()).getValue());
        Assert.assertEquals("widget1", ((StringValue) ((ValueNode) ((ListNode) preferentialEval.getNode())
                .getNodes().get(1).getNode()).getValue()).getValue());

        /* since we are using KnotMergingConflictResolver, which will merge the remaining keys for MultiKnot */
        Assert.assertEquals("widget2", ((StringValue) ((ValueNode) ((ListNode) preferentialEval.getNode())
                .getNodes().get(2).getNode()).getValue()).getValue());


        /* now evaluate with a new value for widget1 */
        preferentialEval = bonsai.evaluate
                ("l1",
                 Context.builder()
                        .documentContext(JsonPath.parse(ImmutableMap.of("gender", "female")))
                        .preferences(ImmutableMap.of("l1", preferredKnot,
                                                     "w1", Knot.builder()
                                                               .id("w1kaID")
                                                               .knotData(ValuedKnotData.stringValue("newStringValue"))
                                                               .build()))
                        .build());
        Assert.assertEquals(preferredKnot.getId(), preferentialEval.getNode().getId());
        Assert.assertEquals("widget3", ((StringValue) ((ValueNode) ((ListNode) preferentialEval.getNode())
                .getNodes().get(0).getNode()).getValue()).getValue());

        /* the value should be whatever we have set in preferences */
        Assert.assertEquals("newStringValue", ((StringValue) ((ValueNode) ((ListNode) preferentialEval.getNode())
                .getNodes().get(1).getNode()).getValue()).getValue());

        Assert.assertEquals("widget2", ((StringValue) ((ValueNode) ((ListNode) preferentialEval.getNode())
                .getNodes().get(2).getNode()).getValue()).getValue());

    }

    @Test
    public void given_bonsaiTree_when_gettingCompleteTree_then_returnTreeKnot() {
        final Knot knotOne = bonsai.createKnot(ValuedKnotData.stringValue("KnotOne"));
        final Knot knotTwo = bonsai.createKnot(ValuedKnotData.stringValue("KnotTwo"));
        bonsai.createMapping("key", knotOne.getId());
        bonsai.addVariation(knotOne.getId(), Variation.builder()
                .filter(new EqualsFilter("$.gender", "female"))
                .knotId(knotTwo.getId())
                .build());

        final TreeKnot treeKnot = bonsai.getCompleteTree("key");

        assertNotNull("TreeKnot should not be null.", treeKnot);
        assertNotNull("TreeKnot Id should not be null.", treeKnot.getId());
        assertNotNull("TreeKnot data should exist.", treeKnot.getKnotData());
        assertEquals("There is only one edge connected to root TreeKnot.", 1, treeKnot.getTreeEdges().size());
        assertNotEquals("Version of TreeKnot should not be zero.", 0, treeKnot.getVersion());
        final TreeEdge treeEdge = treeKnot.getTreeEdges().get(0);
        assertNotNull("TreeEdge should not be null.", treeEdge);
        assertNotNull("TreeEdge should have non-null identifier.", treeEdge.getEdgeIdentifier());
        assertEquals("There is only one filter connected to root TreeEdge.", 1, treeEdge.getFilters().size());
        assertNotEquals("Version of TreeEdge should not be zero.", 0, treeEdge.getVersion());
        final TreeKnot treeKnotInternal = treeEdge.getTreeKnot();
        assertNotNull("Internal TreeKnot should not be null.", treeKnotInternal);
        assertNotNull("Internal TreeKnot Id should not be null.", treeKnotInternal.getId());
        assertNotNull("Internal TreeKnot data should exist.", treeKnotInternal.getKnotData());
        assertNull("Internal TreeKnot should have zero TreeEdges.", treeKnotInternal.getTreeEdges());
        assertNotEquals("Version of Internal TreeKnot should not be zero.", 0, treeKnotInternal.getVersion());
    }

    @Test
    public void given_bonsaiTree_when_gettingCompleteTreeWithPendingUpdates_then_returnTreeKnot() {
        final List<DeltaOperation> deltaOperationList = new ArrayList<>();
        deltaOperationList.add(new KeyMappingDeltaOperation("key", "knotOne"));
        deltaOperationList.add(new KnotDeltaOperation(new Knot("knotOne", 0, null, ValuedKnotData.stringValue("Knot One Value"))));

        final TreeKnot treeKnot = bonsai.getCompleteTreeWithDeltaOperations("key", deltaOperationList);
        final String knotViaKey = bonsai.getMapping("key");
        final Knot knot = bonsai.getKnot("knotOne");

        assertNotNull("TreeKnot should not be null.", treeKnot);
        assertEquals("The Id of TreeKnot should be : knotOne.", "knotOne", treeKnot.getId());
        assertEquals("The version of temporary TreeKnot should be zero.", 0, treeKnot.getVersion());
        assertEquals("There are zero edges connected to TreeKnot.", 0, treeKnot.getTreeEdges().size());
        assertNotNull("TreeKnot data should exist.", treeKnot.getKnotData());
        assertEquals("VALUED type KnotData should present.", "VALUED", treeKnot.getKnotData().getKnotDataType().toString());
        assertNull("InMemoryKeyTreeStore should not contain knotId for keyId : key", knotViaKey);
        assertNull("InMemoryKnotStore should not contain Knot for knotId : knotOne", knot);
    }

    @Test
    public void given_bonsaiTree_when_applyingPendingUpdatesOnCompleteTree_then_returnTreeKnot() {
        final List<DeltaOperation> deltaOperationList = new ArrayList<>();
        deltaOperationList.add(new KeyMappingDeltaOperation("key", "knotOne"));
        deltaOperationList.add(new KnotDeltaOperation(new Knot("knotOne", 0, null, ValuedKnotData.stringValue("Knot One Value"))));

        final TreeKnot treeKnot = bonsai.applyDeltaOperations("key", deltaOperationList);
        final String knotViaKey = bonsai.getMapping("key");
        final Knot knot = bonsai.getKnot("knotOne");

        assertNotNull("TreeKnot should not be null.", treeKnot);
        assertEquals("The Id of TreeKnot should be : knotOne.", "knotOne", treeKnot.getId());
        assertEquals("The version of temporary TreeKnot should be zero.", 0, treeKnot.getVersion());
        assertEquals("There are zero edges connected to TreeKnot.", 0, treeKnot.getTreeEdges().size());
        assertNotNull("TreeKnot data should exist.", treeKnot.getKnotData());
        assertEquals("VALUED type KnotData should present.", "VALUED", treeKnot.getKnotData().getKnotDataType().toString());
        assertEquals("InMemoryKeyTreeStore should not contain knotId for keyId : key", "knotOne", knotViaKey);
        assertNotNull("Knot should not be null.", knot);
        assertEquals("The Id of Knot should be : knotOne.", "knotOne", knot.getId());
        assertNotEquals("The version of Knot should not be zero.", 0, knot.getVersion()); // This is main check.
        assertNull("There are zero edges connected to Knot.", knot.getEdges());
        assertNotNull("Knot data should exist.", knot.getKnotData());
        assertEquals("VALUED type KnotData should present.", "VALUED", knot.getKnotData().getKnotDataType().toString());
    }

    @Test
    public void given_bonsaiTree_when_deleteKnot_then_returnNull() {
        final TreeKnot treeKnot = bonsai.deleteKnot("knotId", false);
        Assert.assertNull("TreeKnot should be null.", treeKnot);
    }

    @Test
    public void given_bonsaiTree_when_evaluatingWithOnlyKeyToKnotMapping_then_returnEmptyNotNullKeyNode() {
        final Knot knot = bonsai.createKnot(ValuedKnotData.stringValue("Knot Data."));
        bonsai.createMapping("key", knot.getId());
        bonsai.deleteKnot(knot.getId(), false);

        final KeyNode keyNode = bonsai.evaluate("key",
                Context.builder().documentContext(JsonPath.parse(ImmutableMap.of("E", 9333))).build());

        assertNotNull("KeyNode should not be null.", keyNode);
        assertEquals("KeyNode's key value should be : key", "key", keyNode.getKey());
        assertNull("KeyNode's node should be null.", keyNode.getNode());
        assertEquals("The size of KeyNode edgePath should be null.", 0, keyNode.getEdgePath().size());
    }

    @Test(expected = BonsaiError.class)
    public void given_bonsaiTree_when_addingNonExistingVariation_then_throwBonsaiError() {
        Knot l1 = bonsai.createKnot(MultiKnotData.builder().key("w1").key("w2").build());
        bonsai.createMapping("l1", l1.getId());
        bonsai.addVariation(l1.getId(), Variation.builder()
                .filter(new EqualsFilter("$.gender", "female"))
                .knotId("variationKnot")
                .build());
    }

    @Test(expected = BonsaiError.class)
    public void given_bonsaiTree_when_updatingEdgeFilters_then_throwBonsaiError() {
        Knot l1 = bonsai.createKnot(MultiKnotData.builder().key("w1").key("w2").build());
        bonsai.updateEdgeFilters(l1.getId(), "edgeId", new ArrayList<>());
    }

    @Test(expected = BonsaiError.class)
    public void given_bonsaiTree_when_addingEdgeFilters_then_throwBonsaiError() {
        bonsai.addEdgeFilters("edgeId", new ArrayList<>());
    }

    @Test(expected = BonsaiError.class)
    public void given_bonsaiTreeWithDissimilarKnotData_when_evaluatingTree_then_throwBonsaiError() {
        Knot l1 = bonsai.createKnot(MultiKnotData.builder().key("w1").key("w2").build());
        bonsai.createMapping("l1", l1.getId());
        Knot l21 = bonsai.createKnot(MultiKnotData.builder().key("l21w3").key("l21w4").build());
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
                        .documentContext(JsonPath.parse(ImmutableMap.of("gender", "female")))
                        .preferences(ImmutableMap.of("l1", preferredKnot))
                        .build());
    }

    @Test(expected = BonsaiError.class)
    public void given_bonsaiTree_when_evaluatingTreeWithOverShootingEdgesPerKnot_then_throwBonsaiError() {
        Knot knot = bonsai.createKnot(ValuedKnotData.stringValue("Data"));
        bonsai.createMapping("mera_data", knot.getId());
        TreeGenerationHelper.generateEdges(knot, bonsai, 10000);
        KeyNode evaluate = bonsai.evaluate("mera_data", Context.builder()
                .documentContext(JsonPath.parse(ImmutableMap.of("E", 9333)))
                .build());
        Assert.assertTrue(evaluate.getNode() instanceof ValueNode);
        Assert.assertEquals("Data9333", ((StringValue) ((ValueNode) evaluate.getNode()).getValue()).getValue());
        System.out.println(evaluate);
    }

    @Test(expected = BonsaiError.class)
    public void given_bonsaiTree_when_createMappingWithWrongKnotData_then_throwBonsaiError() {
        bonsai.createMapping("mera_data", new MapKnotData());
    }

    @Test
    public void given_bonsaiTree_when_evaluatingTreeWithMaxEdgesPerKnot_then_returnEvaluatedKnot() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties
                        .builder()
                        .mutualExclusivitySettingTurnedOn(true)
                        .maxAllowedVariationsPerKnot(10)
                        .build())
                .build();

        Knot knot = bonsai.createKnot(ValuedKnotData.stringValue("Data"));
        bonsai.createMapping("mera_data", knot.getId());
        TreeGenerationHelper.generateEdges(knot, bonsai, 10);
        KeyNode evaluate = bonsai.evaluate("mera_data", Context.builder()
                .documentContext(JsonPath.parse(ImmutableMap.of("E", 9333)))
                .build());
        Assert.assertTrue(evaluate.getNode() instanceof ValueNode);
        Assert.assertEquals("Data", ((StringValue) ((ValueNode) evaluate.getNode()).getValue()).getValue().toString());
        System.out.println(evaluate);
    }

    @Test(expected = BonsaiError.class)
    public void given_bonsaiTree_when_evaluatingTreeWithOneMaxthenMaxEdgesPerKnot_then_throwBonsaiError() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties
                        .builder()
                        .mutualExclusivitySettingTurnedOn(true)
                        .maxAllowedVariationsPerKnot(10)
                        .build())
                .build();

        Knot knot = bonsai.createKnot(ValuedKnotData.stringValue("Data"));
        bonsai.createMapping("mera_data", knot.getId());
        TreeGenerationHelper.generateEdges(knot, bonsai, 11);
        KeyNode evaluate = bonsai.evaluate("mera_data", Context.builder()
                .documentContext(JsonPath.parse(ImmutableMap.of("E", 9333)))
                .build());
        Assert.assertTrue(evaluate.getNode() instanceof ValueNode);
        Assert.assertEquals("Data9333", ((StringValue) ((ValueNode) evaluate.getNode()).getValue()).getValue()
                .toString());
        System.out.println(evaluate);
    }
}