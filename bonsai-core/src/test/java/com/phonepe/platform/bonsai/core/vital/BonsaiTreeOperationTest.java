package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jayway.jsonpath.JsonPath;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.Mapper;
import com.phonepe.platform.bonsai.core.ObjectExtractor;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import com.phonepe.platform.bonsai.models.data.KnotDataVisitor;
import com.phonepe.platform.bonsai.models.data.MapKnotData;
import com.phonepe.platform.bonsai.models.data.MultiKnotData;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.bonsai.models.model.FlatTreeRepresentation;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import com.phonepe.platform.query.dsl.general.GenericFilter;
import com.phonepe.platform.query.dsl.logical.OrFilter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tushar.naik
 * @version 1.0  23/08/18 - 12:57 PM
 */
public class BonsaiTreeOperationTest {

    private Bonsai<Context> bonsai;

    @Before
    public void setUp() {
        this.bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties
                        .builder()
                        .maxAllowedConditionsPerEdge(1)
                        .maxAllowedVariationsPerKnot(10)
                        .mutualExclusivitySettingTurnedOn(true)
                        .build())
                .build();

    }

    @After
    public void tearDown() {
        this.bonsai = null;
    }

    @Test
    public void testCreateMapping() {
        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"));
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"));
        bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"));
        Knot widgetKnot1 = bonsai.createKnot(MultiKnotData.builder()
                                                          .key("icon_1")
                                                          .key("icon_4")
                                                          .key("icon_2")
                                                          .build());

        bonsai.createMapping("widget_1", widgetKnot1.getId());
        Knot icon3 = bonsai.createKnot(ValuedKnotData.stringValue("This is some coool icon"));
        /* there is no older mapping, hence it will return null */
        Knot icon_3 = bonsai.createMapping("icon_3", icon3.getId());
        Assert.assertNull(icon_3);

        /* now mappings exist, hence it will return the older knot */
        Knot icon_3_1 = bonsai.createMapping("icon_3", icon3.getId());
        Assert.assertNotNull(icon_3_1);
        Assert.assertEquals(icon3, icon_3_1);

        /* now mappings exist, hence it will return the older knot */
        icon_3_1 = bonsai.createMapping("icon_3", widgetKnot1.getId());
        Assert.assertNotNull(icon_3_1);
        Assert.assertEquals(icon3, icon_3_1);


        /* now mappings exist, hence it will return the older knot */
        icon_3_1 = bonsai.createMapping("icon_3", icon3.getId());
        Assert.assertNotNull(icon_3_1);
        Assert.assertEquals(widgetKnot1, icon_3_1);

        String knotId = bonsai.getMapping("icon_3");
        Assert.assertEquals(icon3.getId(), knotId);

        bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"));
        bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"));
        bonsai.createMapping("widget_4", ValuedKnotData.stringValue("widget_4"));
        Knot homePageKnot = bonsai.createKnot(MapKnotData.builder()
                .mapKeys(ImmutableMap.of("w1", "widget_1",
                        "w2", "widget_2",
                        "w3", "widget_3",
                        "w4", "widget_4"))
                .build());
        Assert.assertNull(bonsai.createMapping("home_page_1", homePageKnot.getId()));
    }

    @Test(expected = BonsaiError.class)
    public void testCreateMappingError() {
        bonsai.createMapping("widget_1", "randomUnknownId");
    }

    @Test
    public void testUpdateKnotData() {
        bonsai.createMapping("widget_1", ValuedKnotData.stringValue("widget_1"));
        bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"));
        bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"));
        Knot homePageKnot = bonsai.createKnot(MapKnotData.builder()
                                                         .mapKeys(ImmutableMap.of("w1", "widget_1",
                                                                                  "w2", "widget_2"))
                                                         .build());
        MapKnotData newMapKnotData = MapKnotData.builder()
                                                .mapKeys(ImmutableMap.of("w3", "widget_3"))
                                                .build();
        Knot previousKnot = bonsai.updateKnotData(homePageKnot.getId(), newMapKnotData, new HashMap<>());
        Assert.assertEquals(homePageKnot, previousKnot);
        Knot updatedKnot = bonsai.getKnot(homePageKnot.getId());
        Assert.assertEquals(newMapKnotData, updatedKnot.getKnotData());
    }


    @Test(expected = BonsaiError.class)
    public void testAddVariationNoEdge() {
        bonsai.addVariation("someInvalidKnotId", Variation.builder()
                                                          .filter(new EqualsFilter("$.gender", "female"))
                                                          .knotId("asdf")
                                                          .build());
    }


    @Test(expected = BonsaiError.class)
    public void testInvalidInput() {
        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"));
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"));
        bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"));
        bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"));
        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
                .key("icon_1")
                .key("icon_4")
                .key("icon_2")
                .key("icon_3")
                .build());
        bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"));
        bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"));
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
        Assert.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                                               .knotId(femaleConditionKnot.getId())
                                                                               .build()));
    }

    @Test(expected = BonsaiError.class)
    public void testInvalidInputNegativePriority() {
        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"));
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"));
        bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"));
        bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"));
        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
                .key("icon_1")
                .key("icon_4")
                .key("icon_2")
                .key("icon_3")
                .build());
        bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"));
        bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"));
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
        Assert.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                                               .knotId(femaleConditionKnot.getId())
                                                                               .filter(new EqualsFilter("$.gender", "female"))
                                                                               .priority(-2)
                                                                               .build()));
    }

    @Test(expected = BonsaiError.class)
    public void testPivotCheck() {
        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"));
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"));
        bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"));
        bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"));
        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
                .key("icon_1")
                .key("icon_4")
                .key("icon_2")
                .key("icon_3")
                .build());
        bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"));
        bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"));
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
        bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                          .filter(new EqualsFilter("$.gender", "female"))
                                                          .knotId(femaleConditionKnot.getId())
                                                          .build());
        bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                          .filter(new EqualsFilter("$.someOtherPivot", "female"))
                                                          .knotId(femaleConditionKnot.getId())
                                                          .build());
    }

    @Test
    public void testPivotCheck2() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties
                                                                            .builder()
                                                                            .maxAllowedConditionsPerEdge(10)
                                                                            .maxAllowedVariationsPerKnot(10)
                                                                            .mutualExclusivitySettingTurnedOn(true)
                                                                            .build())
                                              .build();
        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"));
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"));
        bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"));
        bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"));
        bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"));
        bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"));
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

        bonsai.createMapping("home_page_1", MultiKnotData.builder()
                                                         .key("widget_1")
                                                         .key("widget_2")
                                                         .key("widget_3")
                                                         .build());
        Edge female = bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                                        .filter(new EqualsFilter("$.gender", "female"))
                                                                        .knotId(femaleConditionKnot.getId())
                                                                        .build());
        Edge edge = bonsai.updateVariation(widgetKnot1.getId(), female.getEdgeIdentifier().getId(),
                                           Variation.builder().filters(Lists.newArrayList(OrFilter.builder()
                                                                        .filter(new EqualsFilter("$.gender", "female"))
                                                                        .filter(GenericFilter.builder()
                                                                                             .field("$.gender")
                                                                                             .value("SDf")
                                                                                             .build()).build())).build());
        Assert.assertNotNull(edge);
    }

    @Test(expected = BonsaiError.class)
    public void testCycleDependencyCheck() throws IOException {
        Map userContext1 = new ObjectExtractor().getObject("userData1.json", Map.class);
        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"));
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"));
        bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"));
        bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"));
        Knot widgetKnot1 = bonsai.createMapping("widget_1", MultiKnotData.builder()
                .key("icon_1")
                .key("icon_4")
                .key("icon_2")
                .key("icon_3")
                .build());
        bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"));
        bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"));
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
        Assert.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                                               .filter(new EqualsFilter("$.gender", "female"))
                                                                               .knotId(femaleConditionKnot.getId())
                                                                               .build()));
        Assert.assertNotNull(bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                                               .filter(new EqualsFilter("$.gender", "male"))
                                                                               .knotId(homePageKnot.getId())
                                                                               .build()));
    }

    @Test
    public void testFlatEval() {
        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"));
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"));
        bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"));
        bonsai.createMapping("home_page_1", ValuedKnotData.stringValue("4"));
        MultiKnotData build = MultiKnotData.builder()
                                           .key("icon_1")
                                           .key("icon_4")
                                           .key("icon_2")
                                           .key("home_page_1")
                                           .build();
        build.setKeys(Lists.newArrayList("ads"));
        FlatTreeRepresentation widget = bonsai.evaluateFlat("widget_1", Context
                .builder()
                .documentContext(JsonPath.parse(Maps.newHashMap()))
                .build());
        Assert.assertEquals("widget_1", widget.getRoot());
    }

    @Test
    public void testGetCompleteTree() throws IOException {
        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"));
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"));
        bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"));
        bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"));
        bonsai.createMapping("widget_1", MultiKnotData.builder()
                .key("icon_1")
                .key("icon_4")
                .key("icon_2")
                .key("icon_3")
                .build());
        bonsai.createMapping("widget_2", ValuedKnotData.stringValue("widget_2"));
        bonsai.createMapping("widget_3", ValuedKnotData.stringValue("widget_3"));
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

        Knot innerKnot = bonsai.createKnot(MultiKnotData.builder()
                                                        .key("icon_3")
                                                        .key("icon_1")
                                                        .key("icon_4")
                                                        .build());

        bonsai.addVariation(femaleConditionKnot.getId(), Variation.builder().knotId(innerKnot.getId())
                                                                  .filter(new EqualsFilter("$.gender", "female"))
                                                                  .build());

        Knot widgetKnot1 = bonsai.createKnot(MultiKnotData.builder()
                                                          .key("icon_1")
                                                          .key("icon_4")
                                                          .key("icon_2")
                                                          .key("icon_3")
                                                          .build());
        bonsai.createMapping("widget_1", widgetKnot1.getId());
        Assert.assertNotNull(femaleConditionKnot);
        bonsai.addVariation(widgetKnot1.getId(), Variation.builder()
                                                          .filter(new EqualsFilter("$.gender", "female"))
                                                          .knotId(femaleConditionKnot.getId())
                                                          .build());
        TreeKnot widget_1 = bonsai.getCompleteTree("widget_1");

        System.out.println(Mapper.MAPPER.writeValueAsString(widget_1));
        Assert.assertEquals(widget_1.getId(), widgetKnot1.getId());
        Assert.assertEquals(widget_1.getTreeEdges().get(0).getTreeKnot().getId(), femaleConditionKnot.getId());
        Assert.assertEquals(widget_1.getTreeEdges().get(0).getTreeKnot().getTreeEdges().get(0)
                                    .getTreeKnot().getId(), innerKnot.getId());
    }

    @Test
    public void testBonsaiDeleteVariationRecursive() throws IOException, BonsaiError {
        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"));
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"));
        bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"));
        bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"));
        bonsai.createMapping("icon_5", ValuedKnotData.stringValue("4"));
        bonsai.createMapping("female2", ValuedKnotData.stringValue("female2"));
        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                                                                  .key("icon_3")
                                                                  .key("icon_1")
                                                                  .key("icon_5")
                                                                  .build());

        Knot leafKnot = bonsai.createKnot(MultiKnotData.builder()
                                                       .key("female2")
                                                       .build());


        Knot leafKnot2 = bonsai.createKnot(MultiKnotData.builder()
                                                        .key("female2")
                                                        .build());

        Knot widgetKnot1 = bonsai.createKnot(MultiKnotData.builder()
                                                          .key("icon_1")
                                                          .key("icon_4")
                                                          .key("icon_2")
                                                          .key("icon_3")
                                                          .build());
        bonsai.createMapping("widget_1", widgetKnot1.getId());
        Assert.assertNotNull(femaleConditionKnot);

        /* checking multiple additions */
        Edge femaleVariationEdge = bonsai.addVariation(widgetKnot1.getId(),
                                                       Variation.builder()
                                                                .filter(new EqualsFilter("$.gender", "female"))
                                                                .knotId(femaleConditionKnot.getId())
                                                                .build());

        Edge leafKnotEdge = bonsai.addVariation(femaleConditionKnot.getId(),
                                                Variation.builder()
                                                         .filter(new EqualsFilter("$.gender2", "female"))
                                                         .knotId(leafKnot.getId())
                                                         .build());

        Edge leaf2KnotEdge = bonsai.addVariation(femaleConditionKnot.getId(),
                                                 Variation.builder()
                                                          .filter(new EqualsFilter("$.gender2", "male"))
                                                          .knotId(leafKnot2.getId())
                                                          .build());


        /* Tree should have both knots and 1 edge */
        TreeKnot widget_1 = bonsai.getCompleteTree("widget_1");
        Assert.assertFalse(widget_1.getTreeEdges().isEmpty());
        widget_1.getTreeEdges().get(0).getTreeKnot().getKnotData().accept(new KnotDataVisitor<Object>() {
            @Override
            public Object visit(ValuedKnotData valuedKnotData) {
                return null;
            }

            @Override
            public Object visit(MultiKnotData multiKnotData) {
                Assert.assertTrue(multiKnotData.getKeys().contains("icon_5"));
                return null;
            }

            @Override
            public Object visit(MapKnotData mapKnotData) {
                return null;
            }
        });
        Assert.assertNotNull(bonsai.getKnot(femaleConditionKnot.getId()));
        Assert.assertNotNull(bonsai.getEdge(femaleVariationEdge.getEdgeIdentifier().getId()));
        Assert.assertNotNull(bonsai.getEdge(leafKnotEdge.getEdgeIdentifier().getId()));

        System.out.println(Mapper.MAPPER.writeValueAsString(widget_1));
        TreeEdge treeEdge = bonsai.deleteVariation(widgetKnot1.getId(), femaleVariationEdge.getEdgeIdentifier()
                                                                                           .getId(), true);
        System.out.println(Mapper.MAPPER.writeValueAsString(treeEdge));
        widget_1 = bonsai.getCompleteTree("widget_1");
        System.out.println(Mapper.MAPPER.writeValueAsString(widget_1));
        Assert.assertNotNull(widget_1.getKnotData());
        Assert.assertTrue(widget_1.getTreeEdges().isEmpty());
        Assert.assertNull(bonsai.getKnot(femaleConditionKnot.getId()));
        Assert.assertNull(bonsai.getEdge(femaleVariationEdge.getEdgeIdentifier().getId()));
        Assert.assertNull(bonsai.getEdge(leafKnotEdge.getEdgeIdentifier().getId()));
        Assert.assertNull(bonsai.getEdge(leaf2KnotEdge.getEdgeIdentifier().getId()));
    }

    @Test
    public void testBonsaiDeleteVariationNonRecursive() throws IOException, BonsaiError {
        bonsai.createMapping("icon_1", ValuedKnotData.stringValue("1"));
        bonsai.createMapping("icon_2", ValuedKnotData.stringValue("2"));
        bonsai.createMapping("icon_3", ValuedKnotData.stringValue("3"));
        bonsai.createMapping("icon_4", ValuedKnotData.stringValue("4"));
        bonsai.createMapping("icon_5", ValuedKnotData.stringValue("5"));
        bonsai.createMapping("female2", ValuedKnotData.stringValue("female 2"));
        bonsai.createMapping("female3", ValuedKnotData.stringValue("female 3"));
        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                                                                  .key("icon_3")
                                                                  .key("icon_1")
                                                                  .key("icon_5")
                                                                  .build());

        Knot widgetKnot1 = bonsai.createKnot(MultiKnotData.builder()
                                                          .key("icon_1")
                                                          .key("icon_4")
                                                          .key("icon_2")
                                                          .key("icon_3")
                                                          .build());


        Knot leafKnot = bonsai.createKnot(MultiKnotData.builder()
                                                       .key("female2")
                                                       .build());


        Knot leafKnot2 = bonsai.createKnot(MultiKnotData.builder()
                                                        .key("female3")
                                                        .build());

        bonsai.createMapping("widget_1", widgetKnot1.getId());
        Assert.assertNotNull(femaleConditionKnot);

        /* checking multiple additions */
        Edge femaleVariationEdge = bonsai.addVariation(widgetKnot1.getId(),
                                                       Variation.builder()
                                                                .filter(new EqualsFilter("$.gender", "female"))
                                                                .knotId(femaleConditionKnot.getId())
                                                                .build());

        Edge leafKnotEdge = bonsai.addVariation(femaleConditionKnot.getId(),
                                                Variation.builder()
                                                         .filter(new EqualsFilter("$.gender2", "female"))
                                                         .knotId(leafKnot.getId())
                                                         .build());

        Edge leaf2KnotEdge = bonsai.addVariation(femaleConditionKnot.getId(),
                                                 Variation.builder()
                                                          .filter(new EqualsFilter("$.gender2", "male"))
                                                          .knotId(leafKnot2.getId())
                                                          .build());

        /* Tree should have both knots and 1 edge */
        TreeKnot widget_1 = bonsai.getCompleteTree("widget_1");
        Assert.assertFalse(widget_1.getTreeEdges().isEmpty());
        widget_1.getTreeEdges().get(0).getTreeKnot().getKnotData().accept(new KnotDataVisitor<Object>() {
            @Override
            public Object visit(ValuedKnotData valuedKnotData) {
                return null;
            }

            @Override
            public Object visit(MultiKnotData multiKnotData) {
                Assert.assertTrue(multiKnotData.getKeys().contains("icon_5"));
                return null;
            }

            @Override
            public Object visit(MapKnotData mapKnotData) {
                return null;
            }
        });
        Assert.assertNotNull(bonsai.getKnot(femaleConditionKnot.getId()));
        Assert.assertNotNull(bonsai.getEdge(femaleVariationEdge.getEdgeIdentifier().getId()));

        System.out.println(Mapper.MAPPER.writeValueAsString(widget_1));

        /* deleting variation non recursively */
        TreeEdge treeEdge = bonsai.deleteVariation(widgetKnot1.getId(), femaleVariationEdge.getEdgeIdentifier()
                                                                                           .getId(), false);


        System.out.println(Mapper.MAPPER.writeValueAsString(treeEdge));
        widget_1 = bonsai.getCompleteTree("widget_1");
        System.out.println(Mapper.MAPPER.writeValueAsString(widget_1));
        Assert.assertNotNull(widget_1.getKnotData());
        /* edge must be deleted */
        Assert.assertTrue(widget_1.getTreeEdges().isEmpty());
        Assert.assertNull(bonsai.getEdge(femaleVariationEdge.getEdgeIdentifier().getId()));

        /* other edges must exist */
        Assert.assertNotNull(bonsai.getEdge(leafKnotEdge.getEdgeIdentifier().getId()));
        Assert.assertNotNull(bonsai.getEdge(leaf2KnotEdge.getEdgeIdentifier().getId()));

        /* knot should still exist */
        Assert.assertNotNull(bonsai.getKnot(femaleConditionKnot.getId()));
    }
}