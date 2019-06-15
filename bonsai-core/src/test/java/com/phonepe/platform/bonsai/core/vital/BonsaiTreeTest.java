package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.JsonPath;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.Mapper;
import com.phonepe.platform.bonsai.core.ObjectExtractor;
import com.phonepe.platform.bonsai.core.TreeGenerationHelper;
import com.phonepe.platform.bonsai.core.data.MapKnotData;
import com.phonepe.platform.bonsai.core.data.MultiKnotData;
import com.phonepe.platform.bonsai.core.data.ValuedKnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.core.vital.blocks.Variation;
import com.phonepe.platform.bonsai.models.*;
import com.phonepe.platform.bonsai.models.value.DataValue;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  23/08/18 - 12:57 PM
 */
public class BonsaiTreeTest {

    private Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                                  .withBonsaiProperties(
                                                          BonsaiProperties
                                                                  .builder()
                                                                  .mutualExclusivitySettingTurnedOn(true)
                                                                  .maxAllowedConditionsPerEdge(10)
                                                                  .maxAllowedVariationsPerKnot(10)
                                                                  .build())
                                                  .build();


    @Test(expected = BonsaiError.class)
    public void testBonsaiEdgeMaxCondition() {
        Knot knot = bonsai.createKnot(ValuedKnotData.builder()
                                                    .value(DataValue.builder().data("Data").build())
                                                    .build());
        bonsai.createMapping("mera_data", knot.getId());
        TreeGenerationHelper.generateEdges(knot, bonsai, 10000);
        KeyNode evaluate = bonsai.evaluate("mera_data", Context.builder()
                                                               .documentContext(JsonPath.parse(ImmutableMap.of("E", 9333)))
                                                               .build());
        Assert.assertTrue(evaluate.getNode() instanceof ValueNode);
        Assert.assertEquals("Data9333", ((DataValue) ((ValueNode) evaluate.getNode()).getValue()).getData().toString());
        System.out.println(evaluate);
    }

    @Test
    public void testBonsaiEdgeMaxCondition2() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties
                                                                            .builder()
                                                                            .mutualExclusivitySettingTurnedOn(true)
                                                                            .maxAllowedVariationsPerKnot(10)
                                                                            .build())
                                              .build();

        Knot knot = bonsai.createKnot(ValuedKnotData.builder()
                                                    .value(DataValue.builder().data("Data").build())
                                                    .build());
        bonsai.createMapping("mera_data", knot.getId());
        TreeGenerationHelper.generateEdges(knot, bonsai, 9);
        KeyNode evaluate = bonsai.evaluate("mera_data", Context.builder()
                                                               .documentContext(JsonPath.parse(ImmutableMap.of("E", 9333)))
                                                               .build());
        Assert.assertTrue(evaluate.getNode() instanceof ValueNode);
        Assert.assertEquals("Data", ((DataValue) ((ValueNode) evaluate.getNode()).getValue()).getData().toString());
        System.out.println(evaluate);
    }

    @Test(expected = BonsaiError.class)
    public void testBonsaiEdgeMaxCondition3() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties
                                                                            .builder()
                                                                            .mutualExclusivitySettingTurnedOn(true)
                                                                            .maxAllowedVariationsPerKnot(10)
                                                                            .build())
                                              .build();

        Knot knot = bonsai.createKnot(ValuedKnotData.builder()
                                                    .value(DataValue.builder().data("Data").build())
                                                    .build());
        bonsai.createMapping("mera_data", knot.getId());
        TreeGenerationHelper.generateEdges(knot, bonsai, 11);
        KeyNode evaluate = bonsai.evaluate("mera_data", Context.builder()
                                                               .documentContext(JsonPath.parse(ImmutableMap.of("E", 9333)))
                                                               .build());
        Assert.assertTrue(evaluate.getNode() instanceof ValueNode);
        Assert.assertEquals("Data9333", ((DataValue) ((ValueNode) evaluate.getNode()).getValue()).getData().toString());
        System.out.println(evaluate);
    }

    @Test
    public void testBonsai() throws IOException, BonsaiError {
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
    public void testBonsai2() throws IOException, BonsaiError {
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
    public void testMapKnotData() throws IOException, BonsaiError {
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
        Knot icon3 = bonsai.createKnot(ValuedKnotData.builder()
                                                     .value(DataValue.builder()
                                                                     .data("This is some coool icon")
                                                                     .build())
                                                     .build());
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
    public void testBonsaiPreferenceEvaluation() {
        Knot l1 = bonsai.createKnot(ValuedKnotData.dataValue("L-1"));
        bonsai.createMapping("baseKey", l1.getId());

        Knot l21 = bonsai.createKnot(ValuedKnotData.dataValue("L-2-1"));

        bonsai.addVariation(l1.getId(), Variation.builder()
                                                 .filter(new EqualsFilter("$.gender", "female"))
                                                 .knotId(l21.getId())
                                                 .build());

        KeyNode nonPreferencialEval = bonsai.evaluate("baseKey", Context.builder()
                                                                        .documentContext(JsonPath.parse(ImmutableMap.of("gender", "female")))
                                                                        .build());
        Assert.assertEquals(l21.getId(), nonPreferencialEval.getNode().getId());
        Assert.assertEquals("L-2-1", ((DataValue) ((ValueNode) nonPreferencialEval.getNode()).getValue()).getData());
        Knot preferredKnot = Knot.builder()
                                 .id("P1kaID")
                                 .knotData(ValuedKnotData.dataValue("P-1"))
                                 .build();
        KeyNode preferentialEval = bonsai.evaluate
                ("baseKey",
                 Context.builder()
                        .documentContext(JsonPath.parse(ImmutableMap.of("gender", "female")))
                        .preferences(ImmutableMap.of("baseKey", preferredKnot))
                        .build());
        Assert.assertEquals(preferredKnot.getId(), preferentialEval.getNode().getId());
        Assert.assertEquals(((DataValue) ((ValuedKnotData) preferredKnot.getKnotData()).getValue()).getData(),
                            ((DataValue) ((ValueNode) preferentialEval.getNode()).getValue()).getData());

    }

    @Test(expected = BonsaiError.class)
    public void testBonsaiPreferenceEvaluationClassMismatchErrorExpected() {
        Knot l1 = bonsai.createKnot(MultiKnotData.builder().key("w1").key("w2").build());
        bonsai.createMapping("l1", l1.getId());
        Knot l21 = bonsai.createKnot(MultiKnotData.builder().key("l21w3").key("l21w4").build());
        bonsai.addVariation(l1.getId(), Variation.builder()
                                                 .filter(new EqualsFilter("$.gender", "female"))
                                                 .knotId(l21.getId())
                                                 .build());
        Knot preferredKnot = Knot.builder()
                                 .id("P1kaID")
                                 .knotData(ValuedKnotData.dataValue("P-1"))
                                 .build();
        bonsai.evaluate("l1",
                        Context.builder()
                               .documentContext(JsonPath.parse(ImmutableMap.of("gender", "female")))
                               .preferences(ImmutableMap.of("l1", preferredKnot))
                               .build());
    }


    @Test
    public void testBonsaiPreferenceEvaluationRecursive() {
        Knot l1 = bonsai.createKnot(MultiKnotData.builder().key("w1").key("w2").key("w3").build());
        bonsai.createMapping("l1", l1.getId());

        Knot l21 = bonsai.createKnot(MultiKnotData.builder().key("w3").key("w2").key("w1").build());
        Knot l22 = bonsai.createKnot(MultiKnotData.builder().key("w1").key("w3").build());

        bonsai.createMapping("l1", l1.getId());

        bonsai.createMapping("w1", ValuedKnotData.dataValue("widget1"));
        bonsai.createMapping("w2", ValuedKnotData.dataValue("widget2"));
        bonsai.createMapping("w3", ValuedKnotData.dataValue("widget3"));

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
        Assert.assertEquals("widget3", ((DataValue) ((ValueNode) ((ListNode) preferentialEval.getNode())
                .getNodes().get(0).getNode()).getValue()).getData());
        Assert.assertEquals("widget1", ((DataValue) ((ValueNode) ((ListNode) preferentialEval.getNode())
                .getNodes().get(1).getNode()).getValue()).getData());

        /* since we are using KnotMergingConflictResolver, which will merge the remaining keys for MultiKnot */
        Assert.assertEquals("widget2", ((DataValue) ((ValueNode) ((ListNode) preferentialEval.getNode())
                .getNodes().get(2).getNode()).getValue()).getData());


        /* now evaluate with a new value for widget1 */
        preferentialEval = bonsai.evaluate
                ("l1",
                 Context.builder()
                        .documentContext(JsonPath.parse(ImmutableMap.of("gender", "female")))
                        .preferences(ImmutableMap.of("l1", preferredKnot,
                                                     "w1", Knot.builder()
                                                               .id("w1kaID")
                                                               .knotData(ValuedKnotData.dataValue("newDataValue"))
                                                               .build()))
                        .build());
        Assert.assertEquals(preferredKnot.getId(), preferentialEval.getNode().getId());
        Assert.assertEquals("widget3", ((DataValue) ((ValueNode) ((ListNode) preferentialEval.getNode())
                .getNodes().get(0).getNode()).getValue()).getData());

        /* the value should be whatever we have set in preferences */
        Assert.assertEquals("newDataValue", ((DataValue) ((ValueNode) ((ListNode) preferentialEval.getNode())
                .getNodes().get(1).getNode()).getValue()).getData());

        Assert.assertEquals("widget2", ((DataValue) ((ValueNode) ((ListNode) preferentialEval.getNode())
                .getNodes().get(2).getNode()).getValue()).getData());

    }

}