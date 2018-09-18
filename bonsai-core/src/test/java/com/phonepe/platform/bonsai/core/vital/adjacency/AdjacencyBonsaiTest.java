//package com.phonepe.platform.bonsai.core.vital.adjacency;
//
//import com.google.common.collect.ImmutableMap;
//import com.jayway.jsonpath.JsonPath;
//import com.phonepe.platform.bonsai.core.Bonsai;
//import com.phonepe.platform.bonsai.core.Mapper;
//import com.phonepe.platform.bonsai.core.ObjectExtractor;
//import com.phonepe.platform.bonsai.core.data.MapKnotData;
//import com.phonepe.platform.bonsai.core.data.MultiKnotData;
//import com.phonepe.platform.bonsai.core.data.ValuedKnotData;
//import com.phonepe.platform.bonsai.core.exception.BonsaiError;
//import com.phonepe.platform.bonsai.core.query.filter.general.EqualsFilter;
//import com.phonepe.platform.bonsai.core.vital.*;
//import com.phonepe.platform.bonsai.models.KeyNode;
//import com.phonepe.platform.bonsai.models.ListNode;
//import com.phonepe.platform.bonsai.models.MapNode;
//import com.phonepe.platform.bonsai.models.NodeVisitors;
//import com.phonepe.platform.bonsai.models.value.DataValue;
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.util.Map;
//
///**
// * @author tushar.naik
// * @version 1.0  27/07/18 - 2:36 PM
// */
//public class AdjacencyBonsaiTest {
//
//    private Bonsai bonsai = new AdjacencyBonsai(BonsaiProperties
//                                                        .builder()
//                                                        .singleConditionEdgeSettingTurnedOn(true)
//                                                        .mutualExclusivitySettingTurnedOn(true)
//                                                        .build());
//
//    @Test
//    public void testBonsai() throws IOException, BonsaiError {
//        Map userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);
//        Map userContext2 = new ObjectExtractor().getObject("userData2.json", Map.class);
//
//        Knot homePageKnot = bonsai.createMapping("home_page_1", MultiKnotData.builder()
//                                                                             .key("widget_1")
//                                                                             .key("widget_2")
//                                                                             .key("widget_3")
//                                                                             .build());
//        Knot femaleConditionKnot = bonsai.create(MultiKnotData.builder()
//                                                              .key("icon_3")
//                                                              .key("icon_1")
//                                                              .key("icon_4")
//                                                              .build());
//        /* adding multiple times */
//        Assert.assertTrue(bonsai.add(femaleConditionKnot));
//        Assert.assertTrue(bonsai.add(femaleConditionKnot));
//        Assert.assertTrue(bonsai.add(femaleConditionKnot));
//
//        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
//                                                                         .key("icon_1")
//                                                                         .key("icon_4")
//                                                                         .key("icon_2")
//                                                                         .key("icon_3")
//                                                                         .build());
//        Assert.assertTrue(bonsai.addVariation(widgetKnot1.getId(), Edge.builder()
//                                                                       .condition(new EqualsFilter("$.gender", "female"))
//                                                                       .id(BonsaiIdGen.newId())
//                                                                       .knot(femaleConditionKnot)
//                                                                       .build()));
//
//        /* checking multiple additions */
//        bonsai.addVariation(widgetKnot1.getId(), Edge.builder()
//                                                     .condition(new EqualsFilter("$.gender", "female"))
//                                                     .id(BonsaiIdGen.newId())
//                                                     .knot(femaleConditionKnot)
//                                                     .build());
//
//        KeyNode user1HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
//                                                                                .documentContext(JsonPath.parse(userContext1))
//                                                                                .build());
//        System.out.println(Mapper.MAPPER.writeValueAsString(user1HomePageEvaluation));
//
//        Assert.assertEquals(user1HomePageEvaluation.getKey(), "home_page_1");
//        Assert.assertEquals(user1HomePageEvaluation.getNode().getId(), homePageKnot.getId());
//        Assert.assertTrue(NodeVisitors.isList(user1HomePageEvaluation.getNode()));
//
//        Assert.assertEquals(((ListNode) user1HomePageEvaluation.getNode()).getNodes().size(), 3);
//        Assert.assertTrue(NodeVisitors.isList(((ListNode) user1HomePageEvaluation.getNode()).getNodes()
//                                                                                            .get(0)
//                                                                                            .getNode()));
//
//        Assert.assertEquals(((ListNode) (((ListNode) user1HomePageEvaluation.getNode()).getNodes()
//                                                                                       .get(0)
//                                                                                       .getNode())).getNodes()
//                                                                                                   .size(), 4);
//
//        /* evaluate with context 2 */
//        KeyNode user2HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
//                                                                                .documentContext(JsonPath.parse(userContext2))
//                                                                                .build());
//
//
//        Assert.assertEquals(user2HomePageEvaluation.getKey(), "home_page_1");
//        Assert.assertEquals(user2HomePageEvaluation.getNode().getId(), homePageKnot.getId());
//        Assert.assertTrue(NodeVisitors.isList(user2HomePageEvaluation.getNode()));
//
//        Assert.assertEquals(((ListNode) user2HomePageEvaluation.getNode()).getNodes().size(), 3);
//        Assert.assertTrue(NodeVisitors.isList(((ListNode) user2HomePageEvaluation.getNode()).getNodes()
//                                                                                            .get(0)
//                                                                                            .getNode()));
//
//        Assert.assertEquals(((ListNode) (((ListNode) user2HomePageEvaluation.getNode()).getNodes()
//                                                                                       .get(0)
//                                                                                       .getNode())).getNodes()
//                                                                                                   .size(), 3);
//
//        Assert.assertEquals((((ListNode) user2HomePageEvaluation.getNode()).getNodes()
//                                                                           .get(0)
//                                                                           .getNode()).getId(), femaleConditionKnot.getId());
//
//        System.out.println(Mapper.MAPPER.writeValueAsString(user2HomePageEvaluation));
//    }
//
//    @Test
//    public void testMapKnotData() throws IOException, BonsaiError {
//        Map userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);
//
//        Knot homePageKnot = bonsai.createMapping("home_page_1", MapKnotData.builder()
//                                                                           .mapKeys(ImmutableMap.of("w1", "widget_1",
//                                                                                                    "w2", "widget_2",
//                                                                                                    "w3", "widget_3",
//                                                                                                    "w4", "widget_4"))
//                                                                           .build());
//        Knot femaleConditionKnot = bonsai.create(MultiKnotData.builder()
//                                                              .key("icon_3")
//                                                              .key("icon_1")
//                                                              .key("icon_4")
//                                                              .build());
//        Assert.assertTrue(bonsai.add(femaleConditionKnot));
//
//        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
//                                                                         .key("icon_1")
//                                                                         .key("icon_4")
//                                                                         .key("icon_2")
//                                                                         .key("icon_3")
//                                                                         .build());
//        Assert.assertNotNull(bonsai.createMapping("icon_3", ValuedKnotData.builder()
//                                                                          .value(DataValue.builder()
//                                                                                          .data("This is some coool icon")
//                                                                                          .build())
//                                                                          .build()));
//        Assert.assertTrue(bonsai.addVariation(widgetKnot1.getId(), Edge.builder()
//                                                                       .condition(new EqualsFilter("$.gender", "female"))
//                                                                       .id(BonsaiIdGen.newId())
//                                                                       .knot(femaleConditionKnot)
//                                                                       .build()));
//
//        /* checking multiple additions */
//        bonsai.addVariation(widgetKnot1.getId(), Edge.builder()
//                                                     .condition(new EqualsFilter("$.gender", "female"))
//                                                     .id(BonsaiIdGen.newId())
//                                                     .knot(femaleConditionKnot)
//                                                     .build());
//
//        KeyNode user1HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
//                                                                                .documentContext(JsonPath.parse(userContext1))
//                                                                                .build());
//        System.out.println(Mapper.MAPPER.writeValueAsString(user1HomePageEvaluation));
//
//        Assert.assertEquals(user1HomePageEvaluation.getKey(), "home_page_1");
//        Assert.assertEquals(user1HomePageEvaluation.getNode().getId(), homePageKnot.getId());
//        Assert.assertTrue(NodeVisitors.isMap(user1HomePageEvaluation.getNode()));
//
//        Assert.assertEquals(((MapNode) user1HomePageEvaluation.getNode()).getNodeMap().size(), 4);
//        Assert.assertTrue(NodeVisitors.isList(((MapNode) user1HomePageEvaluation.getNode()).getNodeMap()
//                                                                                           .get("w1")
//                                                                                           .getNode()));
//
//        Assert.assertEquals(((ListNode) (((MapNode) user1HomePageEvaluation.getNode()).getNodeMap()
//                                                                                      .get("w1")
//                                                                                      .getNode())).getNodes()
//                                                                                                  .size(), 4);
//    }
//
//    @Test(expected = BonsaiError.class)
//    public void testInvalidInput() {
//        bonsai.createMapping("home_page_1", MultiKnotData.builder()
//                                                         .key("widget_1")
//                                                         .key("widget_2")
//                                                         .key("widget_3")
//                                                         .build());
//        Knot femaleConditionKnot = bonsai.create(MultiKnotData.builder()
//                                                              .key("icon_3")
//                                                              .key("icon_1")
//                                                              .key("icon_4")
//                                                              .build());
//        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
//                                                                         .key("icon_1")
//                                                                         .key("icon_4")
//                                                                         .key("icon_2")
//                                                                         .key("icon_3")
//                                                                         .build());
//        Assert.assertTrue(bonsai.addVariation(widgetKnot1.getId(), Edge.builder()
//                                                                       .condition(new EqualsFilter("$.gender", "female"))
//                                                                       .knot(femaleConditionKnot)
//                                                                       .build()));
//    }
//
//    @Test(expected = BonsaiError.class)
//    public void testPivotCheck() {
//        bonsai.createMapping("home_page_1", MultiKnotData.builder()
//                                                         .key("widget_1")
//                                                         .key("widget_2")
//                                                         .key("widget_3")
//                                                         .build());
//        Knot femaleConditionKnot = bonsai.create(MultiKnotData.builder()
//                                                              .key("icon_3")
//                                                              .key("icon_1")
//                                                              .key("icon_4")
//                                                              .build());
//        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
//                                                                         .key("icon_1")
//                                                                         .key("icon_4")
//                                                                         .key("icon_2")
//                                                                         .key("icon_3")
//                                                                         .build());
//        bonsai.addVariation(widgetKnot1.getId(), Edge.builder()
//                                                     .condition(new EqualsFilter("$.gender", "female"))
//                                                     .id(BonsaiIdGen.newId())
//                                                     .knot(femaleConditionKnot)
//                                                     .build());
//        bonsai.addVariation(widgetKnot1.getId(), Edge.builder()
//                                                     .condition(new EqualsFilter("$.someOtherPivot", "female"))
//                                                     .id(BonsaiIdGen.newId())
//                                                     .knot(femaleConditionKnot)
//                                                     .build());
//    }
//
//    @Test(expected = BonsaiError.class)
//    public void testCycleDependencyCheck() throws IOException {
//        Map userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);
//        Map userContext2 = new ObjectExtractor().getObject("userData2.json", Map.class);
//        Knot homePageKnot = bonsai.createMapping("home_page_1", MultiKnotData.builder()
//                                                                             .key("widget_1")
//                                                                             .key("widget_2")
//                                                                             .key("widget_3")
//                                                                             .build());
//        Knot femaleConditionKnot = bonsai.create(MultiKnotData.builder()
//                                                              .key("icon_3")
//                                                              .key("icon_1")
//                                                              .key("icon_4")
//                                                              .build());
//        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
//                                                                         .key("icon_1")
//                                                                         .key("icon_4")
//                                                                         .key("icon_2")
//                                                                         .key("icon_3")
//                                                                         .build());
//        Assert.assertTrue(bonsai.addVariation(widgetKnot1.getId(), Edge.builder()
//                                                                       .condition(new EqualsFilter("$.gender", "female"))
//                                                                       .id(BonsaiIdGen.newId())
//                                                                       .knot(femaleConditionKnot)
//                                                                       .build()));
//        Assert.assertTrue(bonsai.addVariation(widgetKnot1.getId(), Edge.builder()
//                                                                       .condition(new EqualsFilter("$.gender", "male"))
//                                                                       .id(BonsaiIdGen.newId())
//                                                                       .knot(homePageKnot)
//                                                                       .build()));
//
//        KeyNode user1HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
//                                                                                .documentContext(JsonPath.parse(userContext1))
//                                                                                .build());
//        System.out.println(Mapper.MAPPER.writeValueAsString(user1HomePageEvaluation));
//    }
//
//
//    @Test(expected = BonsaiError.class)
//    public void testCycleDependencyCheckOnKeys() throws IOException {
//        Map userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);
//        Map userContext2 = new ObjectExtractor().getObject("userData2.json", Map.class);
//        Knot homePageKnot = bonsai.createMapping("home_page_1", MultiKnotData.builder()
//                                                                             .key("widget_1")
//                                                                             .key("widget_2")
//                                                                             .key("widget_3")
//                                                                             .build());
//        Knot femaleConditionKnot = bonsai.create(MultiKnotData.builder()
//                                                              .key("icon_3")
//                                                              .key("icon_1")
//                                                              .key("icon_4")
//                                                              .build());
//        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
//                                                                         .key("icon_1")
//                                                                         .key("icon_4")
//                                                                         .key("icon_2")
//                                                                         .key("home_page_1")
//                                                                         .build());
//        Assert.assertTrue(bonsai.addVariation(widgetKnot1.getId(), Edge.builder()
//                                                                       .condition(new EqualsFilter("$.gender", "female"))
//                                                                       .id(BonsaiIdGen.newId())
//                                                                       .knot(femaleConditionKnot)
//                                                                       .build()));
//        Assert.assertTrue(bonsai.addVariation(widgetKnot1.getId(), Edge.builder()
//                                                                       .condition(new EqualsFilter("$.gender", "male"))
//                                                                       .id(BonsaiIdGen.newId())
//                                                                       .knot(homePageKnot)
//                                                                       .build()));
//
//        KeyNode user1HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
//                                                                                .documentContext(JsonPath.parse(userContext1))
//                                                                                .build());
//        System.out.println(Mapper.MAPPER.writeValueAsString(user1HomePageEvaluation));
//    }
//}