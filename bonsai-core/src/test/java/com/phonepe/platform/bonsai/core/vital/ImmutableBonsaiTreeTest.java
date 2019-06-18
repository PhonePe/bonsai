package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.Maps;
import com.jayway.jsonpath.JsonPath;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.data.ValuedKnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import com.phonepe.platform.bonsai.core.vital.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.core.vital.blocks.Variation;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.value.DataValue;
import com.phonepe.platform.query.dsl.general.NotEqualsFilter;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author tushar.naik
 * @version 1.0  2018-10-04 - 16:21
 */
public class ImmutableBonsaiTreeTest {

    @Test(expected = BonsaiError.class)
    public void testImmutableBonsaiTreesError() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties.builder().build())
                                              .build();

        Bonsai<Context> build = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.dataValue("1"))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.dataValue("1"))
                                .version(123)
                                .build())
                .build();

        build.createKnot(Knot.builder()
                             .id("k2")
                             .knotData(ValuedKnotData.dataValue("1"))
                             .version(123)
                             .build());
    }

    @Test
    public void testImmutableBonsaiTrees() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties.builder().build())
                                              .build();

        Bonsai<Context> immutable = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.dataValue("1"))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.dataValue("1"))
                                .version(123)
                                .build())
                .build();

        immutable.createKnot(Knot.builder()
                                 .id("k2")
                                 .knotData(ValuedKnotData.dataValue("1"))
                                 .version(123)
                                 .build());
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
                                .knotData(ValuedKnotData.dataValue("1"))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.dataValue("1"))
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
                                .knotData(ValuedKnotData.dataValue("1"))
                                .version(123)
                                .build())
                .createMapping("key1", "k1")
                .createKnot(Knot.builder()
                                .id("k2")
                                .knotData(ValuedKnotData.dataValue("1"))
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
                              .knotData(ValuedKnotData.dataValue("1"))
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
                                .knotData(ValuedKnotData.dataValue("1"))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.dataValue("1"))
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
                                .knotData(ValuedKnotData.dataValue("1"))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k2")
                                .knotData(ValuedKnotData.dataValue("d1"))
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k3")
                                .knotData(ValuedKnotData.dataValue("d3"))
                                .version(123)
                                .build())
                .createKnot(ValuedKnotData.dataValue("2"));
        bonsaiBuilder.updateKnotData("k1", ValuedKnotData.dataValue("3"));
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
        Assert.assertEquals("3", ((DataValue) ((ValuedKnotData) immutable.getKnot("k1")
                                                                        .getKnotData()).getValue()).getData());
        Assert.assertNull(immutable.getKnot("k2"));
        Assert.assertNotNull(immutable.getEdge("e1"));

    }
}