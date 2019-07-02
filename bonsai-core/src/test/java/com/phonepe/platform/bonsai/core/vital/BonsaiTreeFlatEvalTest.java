package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.JsonPath;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.data.MapKnotData;
import com.phonepe.platform.bonsai.core.data.MultiKnotData;
import com.phonepe.platform.bonsai.core.data.ValuedKnotData;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.core.vital.blocks.Variation;
import com.phonepe.platform.bonsai.models.model.FlatTreeRepresentation;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import com.phonepe.platform.query.dsl.numeric.GreaterEqualFilter;
import com.phonepe.platform.query.dsl.numeric.LessThanFilter;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author tushar.naik
 * @version 1.0  2019-06-15 - 23:51
 */
public class BonsaiTreeFlatEvalTest {

    private Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                                  .withBonsaiProperties(
                                                          BonsaiProperties
                                                                  .builder()
                                                                  .mutualExclusivitySettingTurnedOn(true)
                                                                  .maxAllowedConditionsPerEdge(10)
                                                                  .maxAllowedVariationsPerKnot(10)
                                                                  .build())
                                                  .build();

    @Test
    public void testFlatEval() {

        Knot l1 = bonsai.createKnot(MultiKnotData.builder().key("w1").key("w2").key("w3").build());
        bonsai.createMapping("l1", l1.getId());

        Knot l21 = bonsai.createKnot(MultiKnotData.builder().key("w3").key("w2").key("w1").build());
        Knot l22 = bonsai.createKnot(MultiKnotData.builder().key("w1").key("w3").build());

        bonsai.createMapping("l1", l1.getId());

        bonsai.createMapping("w1", ValuedKnotData.stringValue("widget1"));
        bonsai.createMapping("w2", ValuedKnotData.stringValue("widget2"));
        bonsai.createMapping("w3", MapKnotData.builder().mapKeys(ImmutableMap.of("k1", "v1")).build());
        bonsai.createMapping("v1", ValuedKnotData.stringValue("value1"));

        bonsai.addVariation(l1.getId(), Variation.builder()
                                                 .filter(new EqualsFilter("$.gender", "female"))
                                                 .knotId(l21.getId())
                                                 .build());

        bonsai.addVariation(l1.getId(), Variation.builder()
                                                 .filter(new EqualsFilter("$.gender", "male"))
                                                 .knotId(l22.getId())
                                                 .build());

        FlatTreeRepresentation flatEval = bonsai.evaluateFlat(
                "l1", Context.builder()
                             .documentContext(JsonPath.parse(ImmutableMap.of("gender", "female")))
                             .build());
        Assert.assertEquals(ImmutableList.of(1), flatEval.getFlatNodeMapping().get("l1").getPath());

        flatEval = bonsai.evaluateFlat(
                "l1", Context.builder()
                             .documentContext(JsonPath.parse(ImmutableMap.of("gender", "male")))
                             .build());
        Assert.assertEquals(ImmutableList.of(2), flatEval.getFlatNodeMapping().get("l1").getPath());

        flatEval = bonsai.evaluateFlat(
                "l1", Context.builder()
                             .documentContext(JsonPath.parse(ImmutableMap.of("gender", "xmale")))
                             .build());
        Assert.assertEquals(ImmutableList.of(), flatEval.getFlatNodeMapping().get("l1").getPath());


        /* trying the same with another level */

        Knot l31 = bonsai.createKnot(MultiKnotData.builder().key("w3").key("w2").key("w1").build());
        Knot l41 = bonsai.createKnot(MultiKnotData.builder().key("w1").key("w3").build());

        bonsai.addVariation(l22.getId(), Variation.builder()
                                                  .filter(new GreaterEqualFilter("$.age", 23))
                                                  .knotId(l31.getId())
                                                  .build());

        bonsai.addVariation(l31.getId(), Variation.builder()
                                                  .filter(new LessThanFilter("$.cars", 5))
                                                  .knotId(l41.getId())
                                                  .build());


        flatEval = bonsai.evaluateFlat(
                "l1", Context.builder()
                             .documentContext(JsonPath.parse(ImmutableMap.of("gender", "male",
                                                                             "age", 35,
                                                                             "cars", 1
                                                                            )))
                             .build());
        Assert.assertEquals(ImmutableList.of(2, 1, 1), flatEval.getFlatNodeMapping().get("l1").getPath());
    }
}