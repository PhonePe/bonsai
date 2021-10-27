package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.Lists;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.TreeGenerationHelper;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import org.junit.Assert;
import org.junit.Test;

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
        Knot knot1 = TreeGenerationHelper.createTestKnot(bonsai, "knot1");
        Knot knot2 = TreeGenerationHelper.createTestKnot(bonsai, "knot2");

        bonsai.createMapping("key1", knot1.getId());

        /* checking multiple additions */
        Edge edge1 = bonsai.addVariation(knot1.getId(),
                Variation.builder()
                        .filters(Lists.newArrayList(new EqualsFilter("$.gender", "female")))
                        .knotId(knot2.getId())
                        .build());

        Edge edge = bonsai.updateVariation(knot1.getId(),
                edge1.getEdgeIdentifier().getId(),
                Variation.builder()
                        .filter(new EqualsFilter("$.gender", "female"))
                        .filter(new EqualsFilter("$.gender2", "female"))
                        .build());

        Assert.assertEquals(2, edge.getFilters().size());
        Assert.assertEquals(2, bonsai.getEdge(edge1.getEdgeIdentifier().getId()).getFilters().size());
        Assert.assertEquals("$.gender", bonsai.getEdge(edge1.getEdgeIdentifier().getId()).getFilters().get(0).getField());
        Assert.assertEquals("$.gender2", bonsai.getEdge(edge1.getEdgeIdentifier().getId()).getFilters().get(1).getField());
    }

    @Test
    public void testUpdateEdgeFilters() throws BonsaiError {
        Knot knot1 = TreeGenerationHelper.createTestKnot(bonsai, "knot1");
        Knot knot2 = TreeGenerationHelper.createTestKnot(bonsai, "knot2");

        bonsai.createMapping("key1", knot1.getId());

        /* checking multiple additions */
        Edge edge1 = bonsai.addVariation(knot1.getId(),
                Variation.builder()
                        .filters(Lists.newArrayList(new EqualsFilter("$.gender", "female")))
                        .knotId(knot2.getId())
                        .build());

        Edge edge = bonsai.updateVariation(knot1.getId(), edge1.getEdgeIdentifier().getId(),
                Variation.builder()
                        .filters(Lists.newArrayList(new EqualsFilter("$.gender2", "female")))
                        .build());

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
        Knot knot1 = TreeGenerationHelper.createTestKnot(bonsai, "knot1");
        Knot knot2 = TreeGenerationHelper.createTestKnot(bonsai, "knot2");

        bonsai.createMapping("key1", knot1.getId());

        /* checking multiple additions */
        Edge edge1 = bonsai.addVariation(knot1.getId(),
                Variation.builder()
                        .filters(Lists.newArrayList(new EqualsFilter("$.gender", "female")))
                        .knotId(knot2.getId())
                        .build());

        Edge edge = bonsai.updateVariation(knot1.getId(),
                edge1.getEdgeIdentifier().getId(),
                Variation.builder().filters(Lists.newArrayList(
                                new EqualsFilter("$.gender", "female"),
                                new EqualsFilter("$.gender", "female2")))
                        .build());
    }

    @Test
    public void testUpdateEdgeFiltersAllowed() throws BonsaiError {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties
                        .builder()
                        .mutualExclusivitySettingTurnedOn(false)
                        .build())
                .build();
        Knot knot1 = TreeGenerationHelper.createTestKnot(bonsai, "knot1");
        Knot knot2 = TreeGenerationHelper.createTestKnot(bonsai, "knot2");

        bonsai.createMapping("key1", knot1.getId());

        /* checking multiple additions */
        Edge edge1 = bonsai.addVariation(knot1.getId(),
                Variation.builder()
                        .filters(Lists.newArrayList(new EqualsFilter("$.gender", "female")))
                        .knotId(knot2.getId())
                        .build());

        Edge edge = bonsai.updateVariation(knot1.getId(), edge1.getEdgeIdentifier().getId(),
                Variation.builder().filters(Lists.newArrayList(new EqualsFilter("$.gender2", "female")))
                        .build());

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
        Knot knot1 = TreeGenerationHelper.createTestKnot(bonsai, "knot1");
        Knot knot2 = TreeGenerationHelper.createTestKnot(bonsai, "knot2");

        bonsai.createMapping("key1", knot1.getId());

        /* checking multiple additions */
        Edge edge1 = bonsai.addVariation(knot1.getId(),
                Variation.builder()
                        .filters(Lists.newArrayList(new EqualsFilter("$.gender", "female")))
                        .knotId(knot2.getId())
                        .build());

        Edge edge = bonsai.updateVariation(knot1.getId(), edge1.getEdgeIdentifier().getId(),
                Variation.builder().filters(Lists.newArrayList(new EqualsFilter("$.gender", "female")))
                        .build());

        Assert.assertEquals(1, edge.getFilters().size());
        Assert.assertEquals(1, bonsai.getEdge(edge1.getEdgeIdentifier().getId()).getFilters().size());
    }

}