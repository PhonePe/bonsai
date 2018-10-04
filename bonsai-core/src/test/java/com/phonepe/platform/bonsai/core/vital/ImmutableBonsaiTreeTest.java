package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.Maps;
import com.jayway.jsonpath.JsonPath;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.data.ValuedKnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.value.DataValue;
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
                                .knotData(ValuedKnotData.builder().value(DataValue.builder().data("1").build()).build())
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.builder().value(DataValue.builder().data("1").build()).build())
                                .version(123)
                                .build())
                .build();

        build.createKnot(Knot.builder()
                             .id("k2")
                             .knotData(ValuedKnotData.builder().value(DataValue.builder().data("1").build()).build())
                             .version(123)
                             .build());
    }

    @Test
    public void testImmutableBonsaiTrees() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties.builder().build())
                                              .build();

        ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.builder().value(DataValue.builder().data("1").build()).build())
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.builder().value(DataValue.builder().data("1").build()).build())
                                .version(123)
                                .build())
                .build();

        bonsai.createKnot(Knot.builder()
                              .id("k2")
                              .knotData(ValuedKnotData.builder().value(DataValue.builder().data("1").build()).build())
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
                                .knotData(ValuedKnotData.builder().value(DataValue.builder().data("1").build()).build())
                                .version(123)
                                .build())
                .createKnot(Knot.builder()
                                .id("k1")
                                .knotData(ValuedKnotData.builder().value(DataValue.builder().data("1").build()).build())
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
                                .knotData(ValuedKnotData.builder().value(DataValue.builder().data("1").build()).build())
                                .version(123)
                                .build())
                .createMapping("key1", "k1")
                .createKnot(Knot.builder()
                                .id("k2")
                                .knotData(ValuedKnotData.builder().value(DataValue.builder().data("1").build()).build())
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
                              .knotData(ValuedKnotData.builder().value(DataValue.builder().data("1").build()).build())
                              .version(123)
                              .build());
        bonsai.createMapping("key3", "k3");
        KeyNode k3 = immutableBuilder.evaluate("key3", Context.builder()
                                                              .documentContext(JsonPath.parse(Maps.newHashMap()))
                                                              .build());
        Assert.assertNotNull(k3);
    }
}