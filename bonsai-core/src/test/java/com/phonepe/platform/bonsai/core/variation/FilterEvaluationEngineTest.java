package com.phonepe.platform.bonsai.core.variation;

import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.JsonPath;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.BonsaiBuilder;
import com.phonepe.platform.bonsai.core.PerformanceEvaluator;
import com.phonepe.platform.bonsai.core.TreeUtils;
import com.phonepe.platform.bonsai.core.data.ValuedKnotData;
import com.phonepe.platform.bonsai.core.vital.BonsaiProperties;
import com.phonepe.platform.bonsai.core.vital.Context;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.ValueNode;
import com.phonepe.platform.bonsai.models.value.DataValue;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author tushar.naik
 * @version 1.0  19/09/18 - 2:49 PM
 */
public class FilterEvaluationEngineTest {
    private Bonsai bonsai = BonsaiBuilder.builder().withBonsaiProperties(BonsaiProperties
                                                                                 .builder()
                                                                                 .singleConditionEdgeSettingTurnedOn(true)
                                                                                 .mutualExclusivitySettingTurnedOn(true)
                                                                                 .build())
                                         .build();

    @Test
    public void simpleTestingOfBonsai() {
        Knot knot = bonsai.createKnot(ValuedKnotData.builder()
                                                    .value(DataValue.builder().data("Data").build())
                                                    .build());
        bonsai.createMapping("mera_data", knot.getId());
        TreeUtils.generateEdges(knot, bonsai, 10000);
        KeyNode evaluate = bonsai.evaluate("mera_data", Context.builder()
                                                               .documentContext(JsonPath.parse(ImmutableMap.of("E", 9333)))
                                                               .build());
        Assert.assertTrue(evaluate.getNode() instanceof ValueNode);
        Assert.assertEquals(((DataValue) ((ValueNode) evaluate.getNode()).getValue()).getData().toString(), "Data9333");
        System.out.println(evaluate);

    }

    @Test
    public void perfTestingOfBonsai() {
        Knot knot = bonsai.createKnot(ValuedKnotData.builder()
                                                    .value(DataValue.builder().data("Data").build())
                                                    .build());
        bonsai.createMapping("tera_data", knot.getId());
        long performanceTreeCreation = PerformanceEvaluator.evaluate(1, () -> TreeUtils.generateEdges(knot, bonsai, 10000));
        System.out.println("time for treeCreation = " + performanceTreeCreation);

        long start = System.currentTimeMillis();
        KeyNode evaluate1 = bonsai.evaluate("tera_data", Context.builder()
                                                                .documentContext(JsonPath.parse(ImmutableMap
                                                                                                        .of("E", Integer.MAX_VALUE)))
                                                                .build());
        System.out.println("elapse:" + (System.currentTimeMillis() - start));

        float evaluate = PerformanceEvaluator.evaluateAndAvg(100, () -> bonsai.evaluate("tera_data", Context.builder()
                                                                                                             .documentContext(JsonPath.parse(ImmutableMap
                                                                                                                                                     .of("E", Integer.MAX_VALUE)))
                                                                                                             .build()));
        System.out.println("evaluate = " + evaluate);
    }
}