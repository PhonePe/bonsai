package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.Lists;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.TreeGenerationHelper;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class BonsaiTreeEdgeOperationTest {

    private Bonsai<Context> bonsai = BonsaiBuilder.builder()
            .withBonsaiProperties(BonsaiProperties
                    .builder()
                    .maxAllowedConditionsPerEdge(Integer.MAX_VALUE)
                    .mutualExclusivitySettingTurnedOn(false)
                    .build())
            .build();

    @Test
    void testAddingEdgeFilters() throws BonsaiError {
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

        Assertions.assertEquals(2, edge.getFilters().size());
        Assertions.assertEquals(2, bonsai.getEdge(edge1.getEdgeIdentifier().getId()).getFilters().size());
        Assertions.assertEquals("$.gender", bonsai.getEdge(edge1.getEdgeIdentifier().getId()).getFilters().get(0).getField());
        Assertions.assertEquals("$.gender2", bonsai.getEdge(edge1.getEdgeIdentifier().getId()).getFilters().get(1).getField());
    }

    @Test
    void testUpdateEdgeFilters() throws BonsaiError {
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

        Assertions.assertEquals(1, edge.getFilters().size());
        Assertions.assertEquals(1, bonsai.getEdge(edge1.getEdgeIdentifier().getId()).getFilters().size());
    }

    @Test
    void testAddingEdgeFiltersNotAllowed() {
        assertThrows(BonsaiError.class, () -> {
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
        });
    }

    @Test
    void testUpdateEdgeFiltersAllowed() throws BonsaiError {
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

        Assertions.assertEquals(1, edge.getFilters().size());
        Assertions.assertEquals(1, bonsai.getEdge(edge1.getEdgeIdentifier().getId()).getFilters().size());
    }

    @Test
    void testUpdateEdgeFiltersNotAllowed() throws BonsaiError {
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

        Assertions.assertEquals(1, edge.getFilters().size());
        Assertions.assertEquals(1, bonsai.getEdge(edge1.getEdgeIdentifier().getId()).getFilters().size());
    }

}