package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.Lists;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.TreeUtils;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.core.vital.blocks.Variation;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author tushar.naik
 * @version 1.0  23/08/18 - 12:57 PM
 */
public class BonsaiTreeEdgeOperationTest {

    private Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                                  .withBonsaiProperties(BonsaiProperties
                                                                                .builder()
                                                                                .maxAllowedConditionsPerEdge(Integer.MAX_VALUE)
                                                                                .mutualExclusivitySettingTurnedOn(false)
                                                                                .build())
                                                  .build();

    @Test
    public void testAddingEdgeFilters() throws BonsaiError {
        Knot knot1 = TreeUtils.createTestKnot(bonsai, "knot1");
        Knot knot2 = TreeUtils.createTestKnot(bonsai, "knot2");

        bonsai.createMapping("key1", knot1.getId());

        /* checking multiple additions */
        Edge edge1 = bonsai.addVariation(knot1.getId(),
                                         Variation.builder()
                                                  .filters(Lists.newArrayList(new EqualsFilter("$.gender", "female")))
                                                  .knotId(knot2.getId())
                                                  .build());

        Edge edge = bonsai.addEdgeFilters(edge1.getEdgeIdentifier().getId(),
                                          Lists.newArrayList(new EqualsFilter("$.gender2", "female")));

        Assert.assertEquals(2, edge.getFilters().size());
        Assert.assertEquals(2, bonsai.getEdge(edge1.getEdgeIdentifier().getId()).getFilters().size());
    }

    @Test
    public void testUpdateEdgeFilters() throws BonsaiError {
        Knot knot1 = TreeUtils.createTestKnot(bonsai, "knot1");
        Knot knot2 = TreeUtils.createTestKnot(bonsai, "knot2");

        bonsai.createMapping("key1", knot1.getId());

        /* checking multiple additions */
        Edge edge1 = bonsai.addVariation(knot1.getId(),
                                         Variation.builder()
                                                  .filters(Lists.newArrayList(new EqualsFilter("$.gender", "female")))
                                                  .knotId(knot2.getId())
                                                  .build());

        Edge edge = bonsai.updateEdgeFilters(knot1.getId(), edge1.getEdgeIdentifier().getId(),
                                             Lists.newArrayList(new EqualsFilter("$.gender2", "female")));

        Assert.assertEquals(1, edge.getFilters().size());
        Assert.assertEquals(1, bonsai.getEdge(edge1.getEdgeIdentifier().getId()).getFilters().size());
    }

    @Test(expected = BonsaiError.class)
    public void testAddingEdgeFiltersNotAllowed() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties
                                                                            .builder()
                                                                            .mutualExclusivitySettingTurnedOn(false)
                                                                            .build())
                                              .build();
        Knot knot1 = TreeUtils.createTestKnot(bonsai, "knot1");
        Knot knot2 = TreeUtils.createTestKnot(bonsai, "knot2");

        bonsai.createMapping("key1", knot1.getId());

        /* checking multiple additions */
        Edge edge1 = bonsai.addVariation(knot1.getId(),
                                         Variation.builder()
                                                  .filters(Lists.newArrayList(new EqualsFilter("$.gender", "female")))
                                                  .knotId(knot2.getId())
                                                  .build());

        Edge edge = bonsai.addEdgeFilters(edge1.getEdgeIdentifier().getId(),
                                          Lists.newArrayList(new EqualsFilter("$.gender", "female2")));
    }

    @Test
    public void testUpdateEdgeFiltersAllowed() throws BonsaiError {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties
                                                                            .builder()
                                                                            .mutualExclusivitySettingTurnedOn(false)
                                                                            .build())
                                              .build();
        Knot knot1 = TreeUtils.createTestKnot(bonsai, "knot1");
        Knot knot2 = TreeUtils.createTestKnot(bonsai, "knot2");

        bonsai.createMapping("key1", knot1.getId());

        /* checking multiple additions */
        Edge edge1 = bonsai.addVariation(knot1.getId(),
                                         Variation.builder()
                                                  .filters(Lists.newArrayList(new EqualsFilter("$.gender", "female")))
                                                  .knotId(knot2.getId())
                                                  .build());

        Edge edge = bonsai.updateEdgeFilters(knot1.getId(), edge1.getEdgeIdentifier().getId(),
                                             Lists.newArrayList(new EqualsFilter("$.gender2", "female")));

        Assert.assertEquals(1, edge.getFilters().size());
        Assert.assertEquals(1, bonsai.getEdge(edge1.getEdgeIdentifier().getId()).getFilters().size());
    }

    @Test
    public void testUpdateEdgeFiltersNotAllowed() throws BonsaiError {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                              .withBonsaiProperties(BonsaiProperties
                                                                            .builder()
                                                                            .maxAllowedConditionsPerEdge(Integer.MAX_VALUE)
                                                                            .mutualExclusivitySettingTurnedOn(true)
                                                                            .build())
                                              .build();
        Knot knot1 = TreeUtils.createTestKnot(bonsai, "knot1");
        Knot knot2 = TreeUtils.createTestKnot(bonsai, "knot2");

        bonsai.createMapping("key1", knot1.getId());

        /* checking multiple additions */
        Edge edge1 = bonsai.addVariation(knot1.getId(),
                                         Variation.builder()
                                                  .filters(Lists.newArrayList(new EqualsFilter("$.gender", "female")))
                                                  .knotId(knot2.getId())
                                                  .build());

        Edge edge = bonsai.updateEdgeFilters(knot1.getId(), edge1.getEdgeIdentifier().getId(),
                                             Lists.newArrayList(new EqualsFilter("$.gender", "female")));

        Assert.assertEquals(1, edge.getFilters().size());
        Assert.assertEquals(1, bonsai.getEdge(edge1.getEdgeIdentifier().getId()).getFilters().size());
    }

}