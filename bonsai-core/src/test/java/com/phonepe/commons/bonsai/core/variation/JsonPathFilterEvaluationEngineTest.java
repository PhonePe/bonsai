/*
 *  Copyright (c) 2025 Original Author(s), PhonePe India Pvt. Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.phonepe.commons.bonsai.core.variation;

import com.codahale.metrics.Timer;
import com.google.common.collect.ImmutableMap;
import com.phonepe.commons.bonsai.core.Bonsai;
import com.phonepe.commons.bonsai.core.Parsers;
import com.phonepe.commons.bonsai.core.PerformanceEvaluator;
import com.phonepe.commons.bonsai.core.TreeGenerationHelper;
import com.phonepe.commons.bonsai.core.vital.BonsaiBuilder;
import com.phonepe.commons.bonsai.core.vital.BonsaiProperties;
import com.phonepe.commons.bonsai.core.vital.Context;
import com.phonepe.commons.bonsai.models.KeyNode;
import com.phonepe.commons.bonsai.models.ValueNode;
import com.phonepe.commons.bonsai.models.blocks.Knot;
import com.phonepe.commons.bonsai.models.data.ValuedKnotData;
import com.phonepe.commons.bonsai.models.value.StringValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JsonPathFilterEvaluationEngineTest {
    private final Bonsai<Context> bonsai = BonsaiBuilder.builder()
            .withBonsaiProperties(BonsaiProperties
                    .builder()
                    .mutualExclusivitySettingTurnedOn(true)
                    .maxAllowedVariationsPerKnot(Integer.MAX_VALUE)
                    .build())
            .build();

    @Test
    void simpleTestingOfBonsai() {
        Knot knot = bonsai.createKnot(ValuedKnotData.stringValue("Data"), null);
        bonsai.createMapping("mera_data", knot.getId());
        TreeGenerationHelper.generateEdges(knot, bonsai, 10000);
        KeyNode evaluate = bonsai.evaluate("mera_data", Context.builder()
                .documentContext(Parsers.parse(ImmutableMap.of("E", 9333)))
                .build());
        Assertions.assertInstanceOf(ValueNode.class, evaluate.getNode());
        Assertions.assertEquals("Data9333",
                                ((StringValue) ((ValueNode) evaluate.getNode()).getValue()).getValue());
        System.out.println(evaluate);
    }


    @Test
    void perfTestingOfBonsai() {
        Knot knot = bonsai.createKnot(ValuedKnotData.stringValue("Data"), null);
        bonsai.createMapping("tera_data", knot.getId());
        Timer performanceTreeCreation =
                new PerformanceEvaluator().evaluate(1, () -> TreeGenerationHelper.generateEdges(knot, bonsai, 1000));
        System.out.println("time for treeCreation = " + performanceTreeCreation.getSnapshot().get99thPercentile());

        long start = System.currentTimeMillis();
        KeyNode evaluate1 = bonsai.evaluate("tera_data", Context.builder()
                .documentContext(Parsers.parse(ImmutableMap
                        .of("E", Integer.MAX_VALUE)))
                .build());
        System.out.println("evaluate1 = " + evaluate1);
        System.out.println("elapse:" + (System.currentTimeMillis() - start));

        Timer evaluate = new PerformanceEvaluator().evaluate(1000, () -> bonsai.evaluate("tera_data", Context.builder()
                .documentContext(Parsers.parse(ImmutableMap
                        .of("E", Integer.MAX_VALUE)))
                .build()));
        if (evaluate.getSnapshot().getMean() / 1_000_000 > 100) {
            Assertions.fail("Evaluation is taking more than 100ms");
        }


    }
}