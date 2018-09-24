package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jayway.jsonpath.JsonPath;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.BonsaiBuilder;
import com.phonepe.platform.bonsai.core.Mapper;
import com.phonepe.platform.bonsai.core.ObjectExtractor;
import com.phonepe.platform.bonsai.core.data.MapKnotData;
import com.phonepe.platform.bonsai.core.data.MultiKnotData;
import com.phonepe.platform.bonsai.core.data.ValuedKnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.variation.filter.general.EqualsFilter;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.core.vital.blocks.Variation;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.ListNode;
import com.phonepe.platform.bonsai.models.MapNode;
import com.phonepe.platform.bonsai.models.NodeVisitors;
import com.phonepe.platform.bonsai.models.value.DataValue;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  23/08/18 - 12:57 PM
 */
public class BonsaiTreeTest {

    private Bonsai bonsai = BonsaiBuilder.builder().withBonsaiProperties(BonsaiProperties
                                                                                 .builder()
                                                                                 .singleConditionEdgeSettingTurnedOn(true)
                                                                                 .mutualExclusivitySettingTurnedOn(true)
                                                                                 .build())
                                         .build();

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

        /* checking multiple additions */
        bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                          .filter(new EqualsFilter("$.gender", "female"))
                                                          .knotId(femaleConditionKnot.getId())
                                                          .build());

        KeyNode user1HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                                                                                .documentContext(JsonPath.parse(userContext1))
                                                                                .build());
        System.out.println(Mapper.MAPPER.writeValueAsString(user1HomePageEvaluation));

        Assert.assertEquals(user1HomePageEvaluation.getKey(), "home_page_1");
        Assert.assertEquals(user1HomePageEvaluation.getNode().getId(), hpKnot.getId());
        Assert.assertTrue(NodeVisitors.isList(user1HomePageEvaluation.getNode()));

        Assert.assertEquals(((ListNode) user1HomePageEvaluation.getNode()).getNodes().size(), 3);
        Assert.assertTrue(NodeVisitors.isList(((ListNode) user1HomePageEvaluation.getNode()).getNodes()
                                                                                            .get(0)
                                                                                            .getNode()));

        Assert.assertEquals(((ListNode) (((ListNode) user1HomePageEvaluation.getNode()).getNodes()
                                                                                       .get(0)
                                                                                       .getNode())).getNodes()
                                                                                                   .size(), 4);

        /* evaluate with context 2 */
        KeyNode user2HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                                                                                .documentContext(JsonPath.parse(userContext2))
                                                                                .build());


        Assert.assertEquals(user2HomePageEvaluation.getKey(), "home_page_1");
        Assert.assertEquals(user2HomePageEvaluation.getNode().getId(), hpKnot.getId());
        Assert.assertTrue(NodeVisitors.isList(user2HomePageEvaluation.getNode()));

        Assert.assertEquals(((ListNode) user2HomePageEvaluation.getNode()).getNodes().size(), 3);
        Assert.assertTrue(NodeVisitors.isList(((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                                                                                            .get(0)
                                                                                            .getNode()));

        Assert.assertEquals(((ListNode) (((ListNode) user2HomePageEvaluation.getNode()).getNodes()
                                                                                       .get(0)
                                                                                       .getNode())).getNodes()
                                                                                                   .size(), 3);

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
        Assert.assertNull(bonsai.createMapping("icon_3", icon3.getId()));

        /* now mappings exist, hence it will return the older knot */
        Assert.assertNotNull(bonsai.createMapping("icon_3", icon3.getId()));

        Assert.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                                               .filter(new EqualsFilter("$.gender", "female"))
                                                                               .knotId(femaleConditionKnot.getId())
                                                                               .build()));

        /* checking multiple additions */
        bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                          .filter(new EqualsFilter("$.gender", "female"))
                                                          .knotId(femaleConditionKnot.getId())
                                                          .build());

        KeyNode user1HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                                                                                .documentContext(JsonPath.parse(userContext1))
                                                                                .build());
        System.out.println(Mapper.MAPPER.writeValueAsString(user1HomePageEvaluation));

        Assert.assertEquals(user1HomePageEvaluation.getKey(), "home_page_1");
        Assert.assertEquals(user1HomePageEvaluation.getNode().getId(), homePageKnot.getId());
        Assert.assertTrue(NodeVisitors.isMap(user1HomePageEvaluation.getNode()));

        Assert.assertEquals(((MapNode) user1HomePageEvaluation.getNode()).getNodeMap().size(), 4);
        Assert.assertTrue(NodeVisitors.isList(((MapNode) user1HomePageEvaluation.getNode()).getNodeMap()
                                                                                           .get("w1")
                                                                                           .getNode()));

        Assert.assertEquals(((ListNode) (((MapNode) user1HomePageEvaluation.getNode()).getNodeMap()
                                                                                      .get("w1")
                                                                                      .getNode())).getNodes()
                                                                                                  .size(), 4);
    }

    @Test(expected = BonsaiError.class)
    public void testInvalidInput() {
        bonsai.createMapping("home_page_1", MultiKnotData.builder()
                                                         .key("widget_1")
                                                         .key("widget_2")
                                                         .key("widget_3")
                                                         .build());
        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                                                                  .key("icon_3")
                                                                  .key("icon_1")
                                                                  .key("icon_4")
                                                                  .build());
        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
                                                                         .key("icon_1")
                                                                         .key("icon_4")
                                                                         .key("icon_2")
                                                                         .key("icon_3")
                                                                         .build());
        Assert.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                                               .knotId(femaleConditionKnot.getId())
                                                                               .build()));
    }

    @Test(expected = BonsaiError.class)
    public void testInvalidInputNegativePriority() {
        bonsai.createMapping("home_page_1", MultiKnotData.builder()
                                                         .key("widget_1")
                                                         .key("widget_2")
                                                         .key("widget_3")
                                                         .build());
        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                                                                  .key("icon_3")
                                                                  .key("icon_1")
                                                                  .key("icon_4")
                                                                  .build());
        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
                                                                         .key("icon_1")
                                                                         .key("icon_4")
                                                                         .key("icon_2")
                                                                         .key("icon_3")
                                                                         .build());
        Assert.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                                               .knotId(femaleConditionKnot.getId())
                                                                               .filter(new EqualsFilter("$.gender", "female"))
                                                                               .priority(-2)
                                                                               .build()));
    }

    @Test(expected = BonsaiError.class)
    public void testPivotCheck() {
        bonsai.createMapping("home_page_1", MultiKnotData.builder()
                                                         .key("widget_1")
                                                         .key("widget_2")
                                                         .key("widget_3")
                                                         .build());
        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                                                                  .key("icon_3")
                                                                  .key("icon_1")
                                                                  .key("icon_4")
                                                                  .build());
        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
                                                                         .key("icon_1")
                                                                         .key("icon_4")
                                                                         .key("icon_2")
                                                                         .key("icon_3")
                                                                         .build());
        bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                          .filter(new EqualsFilter("$.gender", "female"))
                                                          .knotId(femaleConditionKnot.getId())
                                                          .build());
        bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                          .filter(new EqualsFilter("$.someOtherPivot", "female"))
                                                          .knotId(femaleConditionKnot.getId())
                                                          .build());
    }

    @Test(expected = BonsaiError.class)
    public void testCycleDependencyCheck() throws IOException {
        Map userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);
        Map userContext2 = new ObjectExtractor().getObject("userData2.json", Map.class);
        Knot homePageKnot = bonsai.createMapping("home_page_1", MultiKnotData.builder()
                                                                             .key("widget_1")
                                                                             .key("widget_2")
                                                                             .key("widget_3")
                                                                             .build());
        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                                                                  .key("icon_3")
                                                                  .key("icon_1")
                                                                  .key("icon_4")
                                                                  .build());
        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
                                                                         .key("icon_1")
                                                                         .key("icon_4")
                                                                         .key("icon_2")
                                                                         .key("icon_3")
                                                                         .build());
        Assert.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                                               .filter(new EqualsFilter("$.gender", "female"))
                                                                               .knotId(femaleConditionKnot.getId())
                                                                               .build()));
        Assert.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                                               .filter(new EqualsFilter("$.gender", "male"))
                                                                               .knotId(homePageKnot.getId())
                                                                               .build()));

        KeyNode user1HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                                                                                .documentContext(JsonPath.parse(userContext1))
                                                                                .build());
        System.out.println(Mapper.MAPPER.writeValueAsString(user1HomePageEvaluation));
    }

    @Test(expected = BonsaiError.class)
    public void testCycleDependencyCheckOnKeys() throws IOException {
        Map userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);
        Map userContext2 = new ObjectExtractor().getObject("userData2.json", Map.class);
        Knot homePageKnot = bonsai.createMapping("home_page_1", MultiKnotData.builder()
                                                                             .key("widget_1")
                                                                             .key("widget_2")
                                                                             .key("widget_3")
                                                                             .build());
        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                                                                  .key("icon_3")
                                                                  .key("icon_1")
                                                                  .key("icon_4")
                                                                  .build());
        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
                                                                         .key("icon_1")
                                                                         .key("icon_4")
                                                                         .key("icon_2")
                                                                         .key("home_page_1")
                                                                         .build());
        Assert.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                                               .filter(new EqualsFilter("$.gender", "female"))
                                                                               .knotId(femaleConditionKnot.getId())
                                                                               .build()));
        Assert.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                                               .filter(new EqualsFilter("$.gender", "male"))
                                                                               .knotId(homePageKnot.getId())
                                                                               .build()));

        KeyNode user1HomePageEvaluation = bonsai.evaluate("home_page_1", Context.builder()
                                                                                .documentContext(JsonPath.parse(userContext1))
                                                                                .build());
        System.out.println(Mapper.MAPPER.writeValueAsString(user1HomePageEvaluation));
    }

    @Test
    public void testSome() {
        MultiKnotData build = MultiKnotData.builder()
                                           .key("icon_1")
                                           .key("icon_4")
                                           .key("icon_2")
                                           .key("home_page_1")
                                           .build();
        Knot widgetKnot1 = bonsai.createMapping("widget_1", build);
        KeyNode widget_1 = bonsai.evaluate("widget_1", Context.builder()
                                                              .documentContext(JsonPath.parse(Maps.newHashMap()))
                                                              .build());
        build.setKeys(Lists.newArrayList("ads"));
        System.out.println("widget_1 = " + widget_1);
        widget_1 = bonsai.evaluate("widget_1", Context.builder()
                                                      .documentContext(JsonPath.parse(Maps.newHashMap()))
                                                      .build());
        System.out.println("widget_1 = " + widget_1);
    }
}