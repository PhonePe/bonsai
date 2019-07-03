package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.Sets;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.TreeGenerationHelper;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * @author tushar.naik
 * @version 1.0  2019-06-14 - 15:48
 */
public class BonsaiTreeEdgeNumberTest {

    private Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                                  .withBonsaiProperties(
                                                          BonsaiProperties
                                                                  .builder()
                                                                  .mutualExclusivitySettingTurnedOn(true)
                                                                  .maxAllowedVariationsPerKnot(10)
                                                                  .build())
                                                  .build();

    @Test
    public void testEdgeNumbering() {
        Knot knot = bonsai.createKnot(ValuedKnotData.stringValue("Data"));
        bonsai.createMapping("mera_data", knot.getId());
        TreeGenerationHelper.generateEdges(knot, bonsai, 9);
        Knot knot1 = bonsai.getKnot(knot.getId());

        /* all edges must have unique, incremental numbers */
        Set<Integer> integerSet = Sets.newHashSet();
        knot1.getEdges().forEach(k -> {
            if (integerSet.contains(k.getNumber())) {
                Assert.fail("Non Unique numbers assigned to edges");
            }
            integerSet.add(k.getNumber());
        });
    }
}