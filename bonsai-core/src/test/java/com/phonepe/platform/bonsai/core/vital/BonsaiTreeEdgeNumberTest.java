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

package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.Sets;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.TreeGenerationHelper;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class BonsaiTreeEdgeNumberTest {

    private final Bonsai<Context> bonsai = BonsaiBuilder.builder()
            .withBonsaiProperties(
                    BonsaiProperties
                            .builder()
                            .mutualExclusivitySettingTurnedOn(true)
                            .maxAllowedVariationsPerKnot(10)
                            .build())
            .build();

    @Test
    void testEdgeNumbering() {
        Knot knot = bonsai.createKnot(ValuedKnotData.stringValue("Data"), null);
        bonsai.createMapping("mera_data", knot.getId());
        TreeGenerationHelper.generateEdges(knot, bonsai, 9);
        Knot knot1 = bonsai.getKnot(knot.getId());

        /* all edges must have unique, incremental numbers */
        Set<Integer> integerSet = Sets.newHashSet();
        knot1.getEdges().forEach(k -> {
            if (integerSet.contains(k.getNumber())) {
                Assertions.fail("Non Unique numbers assigned to edges");
            }
            integerSet.add(k.getNumber());
        });
    }
}