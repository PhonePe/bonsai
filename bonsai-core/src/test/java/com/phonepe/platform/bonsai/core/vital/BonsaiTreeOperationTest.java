package com.phonepe.platform.bonsai.core.vital;

import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.Mapper;
import com.phonepe.platform.bonsai.core.data.KnotDataVisitor;
import com.phonepe.platform.bonsai.core.data.MapKnotData;
import com.phonepe.platform.bonsai.core.data.MultiKnotData;
import com.phonepe.platform.bonsai.core.data.ValuedKnotData;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.core.vital.blocks.Edge;
import com.phonepe.platform.bonsai.core.vital.blocks.Knot;
import com.phonepe.platform.bonsai.core.vital.blocks.Variation;
import com.phonepe.platform.bonsai.core.vital.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.core.vital.blocks.model.TreeKnot;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author tushar.naik
 * @version 1.0  23/08/18 - 12:57 PM
 */
public class BonsaiTreeOperationTest {

    private Bonsai<Context> bonsai = BonsaiBuilder.builder()
                                                  .withBonsaiProperties(BonsaiProperties
                                                                                .builder()
                                                                                .maxAllowedConditionsPerEdge(1)
                                                                                .mutualExclusivitySettingTurnedOn(true)
                                                                                .build())
                                                  .build();

    @Test
    public void testBonsaiDeleteVariationRecursive() throws IOException, BonsaiError {
        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                                                                  .key("icon_3")
                                                                  .key("icon_1")
                                                                  .key("icon_5")
                                                                  .build());

        Knot leafKnot = bonsai.createKnot(MultiKnotData.builder()
                                                       .key("female2")
                                                       .build());


        Knot leafKnot2 = bonsai.createKnot(MultiKnotData.builder()
                                                        .key("female2")
                                                        .build());

        Knot widgetKnot1 = bonsai.createKnot(MultiKnotData.builder()
                                                          .key("icon_1")
                                                          .key("icon_4")
                                                          .key("icon_2")
                                                          .key("icon_3")
                                                          .build());
        bonsai.createMapping("widget_1", widgetKnot1.getId());
        Assert.assertNotNull(femaleConditionKnot);

        /* checking multiple additions */
        Edge femaleVariationEdge = bonsai.addVariation(widgetKnot1.getId(),
                                                       Variation.builder()
                                                                .filter(new EqualsFilter("$.gender", "female"))
                                                                .knotId(femaleConditionKnot.getId())
                                                                .build());

        Edge leafKnotEdge = bonsai.addVariation(femaleConditionKnot.getId(),
                                                Variation.builder()
                                                         .filter(new EqualsFilter("$.gender2", "female"))
                                                         .knotId(leafKnot.getId())
                                                         .build());

        Edge leaf2KnotEdge = bonsai.addVariation(femaleConditionKnot.getId(),
                                                 Variation.builder()
                                                          .filter(new EqualsFilter("$.gender2", "male"))
                                                          .knotId(leafKnot2.getId())
                                                          .build());


        /* Tree should have both knots and 1 edge */
        TreeKnot widget_1 = bonsai.getCompleteTree("widget_1");
        Assert.assertFalse(widget_1.getTreeEdges().isEmpty());
        widget_1.getTreeEdges().get(0).getTreeKnot().getKnotData().accept(new KnotDataVisitor<Object>() {
            @Override
            public Object visit(ValuedKnotData valuedKnotData) {
                return null;
            }

            @Override
            public Object visit(MultiKnotData multiKnotData) {
                Assert.assertTrue(multiKnotData.getKeys().contains("icon_5"));
                return null;
            }

            @Override
            public Object visit(MapKnotData mapKnotData) {
                return null;
            }
        });
        Assert.assertNotNull(bonsai.getKnot(femaleConditionKnot.getId()));
        Assert.assertNotNull(bonsai.getEdge(femaleVariationEdge.getEdgeIdentifier().getId()));
        Assert.assertNotNull(bonsai.getEdge(leafKnotEdge.getEdgeIdentifier().getId()));

        System.out.println(Mapper.MAPPER.writeValueAsString(widget_1));
        TreeEdge treeEdge = bonsai.deleteVariation(widgetKnot1.getId(), femaleVariationEdge.getEdgeIdentifier()
                                                                                           .getId(), true);
        System.out.println(Mapper.MAPPER.writeValueAsString(treeEdge));
        widget_1 = bonsai.getCompleteTree("widget_1");
        System.out.println(Mapper.MAPPER.writeValueAsString(widget_1));
        Assert.assertNotNull(widget_1.getKnotData());
        Assert.assertTrue(widget_1.getTreeEdges().isEmpty());
        Assert.assertNull(bonsai.getKnot(femaleConditionKnot.getId()));
        Assert.assertNull(bonsai.getEdge(femaleVariationEdge.getEdgeIdentifier().getId()));
        Assert.assertNull(bonsai.getEdge(leafKnotEdge.getEdgeIdentifier().getId()));
        Assert.assertNull(bonsai.getEdge(leaf2KnotEdge.getEdgeIdentifier().getId()));
    }

    @Test
    public void testBonsaiDeleteVariationNonRecursive() throws IOException, BonsaiError {
        Knot femaleConditionKnot = bonsai.createKnot(MultiKnotData.builder()
                                                                  .key("icon_3")
                                                                  .key("icon_1")
                                                                  .key("icon_5")
                                                                  .build());

        Knot widgetKnot1 = bonsai.createKnot(MultiKnotData.builder()
                                                          .key("icon_1")
                                                          .key("icon_4")
                                                          .key("icon_2")
                                                          .key("icon_3")
                                                          .build());


        Knot leafKnot = bonsai.createKnot(MultiKnotData.builder()
                                                       .key("female2")
                                                       .build());


        Knot leafKnot2 = bonsai.createKnot(MultiKnotData.builder()
                                                        .key("female3")
                                                        .build());

        bonsai.createMapping("widget_1", widgetKnot1.getId());
        Assert.assertNotNull(femaleConditionKnot);

        /* checking multiple additions */
        Edge femaleVariationEdge = bonsai.addVariation(widgetKnot1.getId(),
                                                       Variation.builder()
                                                                .filter(new EqualsFilter("$.gender", "female"))
                                                                .knotId(femaleConditionKnot.getId())
                                                                .build());

        Edge leafKnotEdge = bonsai.addVariation(femaleConditionKnot.getId(),
                                                Variation.builder()
                                                         .filter(new EqualsFilter("$.gender2", "female"))
                                                         .knotId(leafKnot.getId())
                                                         .build());

        Edge leaf2KnotEdge = bonsai.addVariation(femaleConditionKnot.getId(),
                                                 Variation.builder()
                                                          .filter(new EqualsFilter("$.gender2", "male"))
                                                          .knotId(leafKnot2.getId())
                                                          .build());

        /* Tree should have both knots and 1 edge */
        TreeKnot widget_1 = bonsai.getCompleteTree("widget_1");
        Assert.assertFalse(widget_1.getTreeEdges().isEmpty());
        widget_1.getTreeEdges().get(0).getTreeKnot().getKnotData().accept(new KnotDataVisitor<Object>() {
            @Override
            public Object visit(ValuedKnotData valuedKnotData) {
                return null;
            }

            @Override
            public Object visit(MultiKnotData multiKnotData) {
                Assert.assertTrue(multiKnotData.getKeys().contains("icon_5"));
                return null;
            }

            @Override
            public Object visit(MapKnotData mapKnotData) {
                return null;
            }
        });
        Assert.assertNotNull(bonsai.getKnot(femaleConditionKnot.getId()));
        Assert.assertNotNull(bonsai.getEdge(femaleVariationEdge.getEdgeIdentifier().getId()));

        System.out.println(Mapper.MAPPER.writeValueAsString(widget_1));

        /* deleting variation non recursively */
        TreeEdge treeEdge = bonsai.deleteVariation(widgetKnot1.getId(), femaleVariationEdge.getEdgeIdentifier()
                                                                                           .getId(), false);


        System.out.println(Mapper.MAPPER.writeValueAsString(treeEdge));
        widget_1 = bonsai.getCompleteTree("widget_1");
        System.out.println(Mapper.MAPPER.writeValueAsString(widget_1));
        Assert.assertNotNull(widget_1.getKnotData());
        /* edge must be deleted */
        Assert.assertTrue(widget_1.getTreeEdges().isEmpty());
        Assert.assertNull(bonsai.getEdge(femaleVariationEdge.getEdgeIdentifier().getId()));

        /* other edges must exist */
        Assert.assertNotNull(bonsai.getEdge(leafKnotEdge.getEdgeIdentifier().getId()));
        Assert.assertNotNull(bonsai.getEdge(leaf2KnotEdge.getEdgeIdentifier().getId()));

        /* knot should still exist */
        Assert.assertNotNull(bonsai.getKnot(femaleConditionKnot.getId()));
    }
}