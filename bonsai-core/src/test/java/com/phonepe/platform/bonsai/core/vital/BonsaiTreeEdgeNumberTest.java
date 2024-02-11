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