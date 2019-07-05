package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jayway.jsonpath.JsonPath;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.value.StringValue;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import com.phonepe.platform.query.dsl.general.NotEqualsFilter;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author tushar.naik
 * @version 1.0  2018-10-04 - 16:21
 */
public class ImmutableBonsaiTreeTest {

    private Bonsai<Context> mutableBonsai = BonsaiBuilder.builder()
                                                         .withBonsaiProperties(BonsaiProperties
                                                                                       .builder()
                                                                                       .maxAllowedConditionsPerEdge(Integer.MAX_VALUE)
                                                                                       .mutualExclusivitySettingTurnedOn(false)
                                                                                       .build())
                                                         .build();
    private Bonsai<Context> bonsai = ImmutableBonsaiBuilder
            .builder(mutableBonsai)
            .createKnot(Knot.builder()
                            .id("k1")
                            .knotData(ValuedKnotData.stringValue("1"))
                            .version(123)
                            .build())
            .createMapping("key1", "k1")
            .createKnot(Knot.builder()
                            .id("k2")
                            .knotData(ValuedKnotData.stringValue("1"))
                            .version(123)
                            .build())
            .createMapping("key2", "k2")
            .createMapping("key3", ValuedKnotData.stringValue("d2"))
            .removeMapping("key3")
            .build();

    @Test(expected = BonsaiError.class)
    public void testImmutableBonsaiTreesError() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties.builder().build())
                                              .build();

        Bonsai<Context> build = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.numberValue(1))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.numberValue(1))
                                .version(123)
                                .build())
                .build();

        build.createKnot(Knot.builder()
                             .id("k2")
                             .knotData(ValuedKnotData.numberValue(1))
                             .version(123)
                             .build());
    }

    @Test
    public void testImmutableBonsaiTreesOriginalShouldBeMutable() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties.builder().build())
                                              .build();

        Bonsai<Context> immutable = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("1"))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("1"))
                                .version(123)
                                .build())
                .build();

        Knot k2 = bonsai.createKnot(Knot.builder()
                                        .id("k1")
                                        .knotData(ValuedKnotData.stringValue("2"))
                                        .version(123)
                                        .build());
        Assert.assertNotNull(k2);
    }

    @Test(expected = BonsaiError.class)
    public void testImmutableBonsaiTreesError2() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties.builder().build())
                                              .build();

        Bonsai<Context> immutable = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("1"))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("1"))
                                .version(123)
                                .build())
                .build();

        immutable.createMapping("k2", "asdf");
    }

    @Test
    public void testImmutableBonsaiTreesWithEval() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties.builder().build())
                                              .build();

        Bonsai<Context> immutableBuilder = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("1"))
                                .version(123)
                                .build())
                .createMapping("key1", "k1")
                .createKnot(Knot.builder()
                                .id("k2")
                                .knotData(ValuedKnotData.stringValue("1"))
                                .version(123)
                                .build())
                .createMapping("key2", "k2")
                .build();
        KeyNode k2 = immutableBuilder.evaluate("key1", Context.builder()
                                                              .documentContext(JsonPath.parse(Maps.newHashMap()))
                                                              .build());
        Assert.assertNotNull(k2);

        bonsai.createKnot(Knot.builder()
                              .id("k3")
                              .knotData(ValuedKnotData.stringValue("1"))
                              .version(123)
                              .build());
        bonsai.createMapping("key3", "k3");
        KeyNode k3 = immutableBuilder.evaluate("key3", Context.builder()
                                                              .documentContext(JsonPath.parse(Maps.newHashMap()))
                                                              .build());
        Assert.assertNotNull(k3);
    }

    @Test(expected = BonsaiError.class)
    public void testImmutableBonsaiTreesError3() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties.builder().build())
                                              .build();

        Bonsai<Context> immutable = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("1"))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("1"))
                                .version(123)
                                .build())
                .build();

        immutable.createEdge(null);
    }


    @Test
    public void testImmutableBonsaiTreesCreateKnot() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties.builder().build())
                                              .build();

        ImmutableBonsaiBuilder<Context> bonsaiBuilder = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("1"))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k2")
                                .knotData(ValuedKnotData.stringValue("d1"))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k3")
                                .knotData(ValuedKnotData.stringValue("d3"))
                                .version(123)
                                .build())
                .createKnot(ValuedKnotData.stringValue("2"));
        bonsaiBuilder.updateKnotData("k1", ValuedKnotData.stringValue("3"));
        bonsaiBuilder.deleteKnot("k2", false);
        bonsaiBuilder.createEdge(Edge.builder()
                                     .version(1)
                                     .edgeIdentifier(new EdgeIdentifier("e1", 1, 1))
                                     .filter(new NotEqualsFilter("$.data", "male"))
                                     .knotId("k3").build());
        bonsaiBuilder.addVariation("k1", Variation.builder()
                                                  .knotId("k3")
                                                  .filter(new NotEqualsFilter("$.data", "male"))
                                                  .priority(1)
                                                  .build());
        Bonsai<Context> immutable = bonsaiBuilder.build();


        Assert.assertNotNull(immutable.getKnot("k1"));
        Assert.assertEquals("3", ((StringValue) ((ValuedKnotData) immutable.getKnot("k1")
                                                                           .getKnotData()).getValue()).getValue());
        Assert.assertNull(immutable.getKnot("k2"));
        Assert.assertNotNull(immutable.getEdge("e1"));

    }

    @Test(expected = BonsaiError.class)
    public void testAddingEdgeFilters() throws BonsaiError {
        Edge edge1 = bonsai.addVariation("k1",
                                         Variation.builder()
                                                  .filters(Lists.newArrayList(new EqualsFilter("$.gender", "female")))
                                                  .knotId("k2")
                                                  .build());
    }

    @Test(expected = BonsaiError.class)
    public void testUpdateEdgeFilters() throws BonsaiError {
        Edge edge = bonsai.updateEdgeFilters("k1", "e1",
                                             Lists.newArrayList(new EqualsFilter("$.gender2", "female")));
    }

    @Test(expected = BonsaiError.class)
    public void testAddingEdgeFiltersNotAllowed() {
        bonsai.addEdgeFilters("e1",
                              Lists.newArrayList(new EqualsFilter("$.gender", "female2")));
    }

    @Test(expected = BonsaiError.class)
    public void testRemoveMapping() {
        bonsai.removeMapping("e1");
    }

    @Test(expected = BonsaiError.class)
    public void testCreateMappingKnot() {
        bonsai.createMapping("e1", ValuedKnotData.stringValue("asdf"));
    }

    @Test(expected = BonsaiError.class)
    public void testDeleteMappingKnot() {
        bonsai.deleteKnot("e1", false);
    }

    @Test(expected = BonsaiError.class)
    public void testDeleteVariation() {
        bonsai.deleteVariation("k1", "e1", false);
    }
}