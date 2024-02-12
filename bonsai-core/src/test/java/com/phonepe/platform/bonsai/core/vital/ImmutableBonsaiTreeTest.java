package com.phonepe.platform.bonsai.core.vital;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.phonepe.platform.bonsai.core.Bonsai;
import com.phonepe.platform.bonsai.core.Parsers;
import com.phonepe.platform.bonsai.core.exception.BonsaiError;
import com.phonepe.platform.bonsai.models.KeyNode;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.Variation;
import com.phonepe.platform.bonsai.models.blocks.delta.DeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KnotDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.model.TreeEdge;
import com.phonepe.platform.bonsai.models.blocks.model.TreeKnot;
import com.phonepe.platform.bonsai.models.data.KnotData;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.bonsai.models.model.FlatTreeRepresentation;
import com.phonepe.platform.bonsai.models.value.StringValue;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import com.phonepe.platform.query.dsl.general.NotEqualsFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ImmutableBonsaiTreeTest {

    private final Bonsai<Context> mutableBonsai = BonsaiBuilder.builder()
            .withBonsaiProperties(BonsaiProperties.builder()
                    .maxAllowedConditionsPerEdge(Integer.MAX_VALUE)
                    .mutualExclusivitySettingTurnedOn(false)
                    .build())
            .build();
    private final Bonsai<Context> bonsai = ImmutableBonsaiBuilder
            .builder(mutableBonsai)
            .createKnot(Knot.builder()
                    .id("k1")
                    .knotData(ValuedKnotData.stringValue("1"))
                    .version(123)
                    .build())
            .createMapping("key1", "k1")
            .createKnot(Knot.builder()
                    .id("k2")
                    .knotData(ValuedKnotData.stringValue("1"))
                    .version(123)
                    .build())
            .createMapping("key2", "k2")
            .createMapping("key3", ValuedKnotData.stringValue("d2"), null)
            .removeMapping("key3")
            .build();

    @Test
    void given_immutableBonsaiTree_when_checkingItems_then_returnValues() {
        final Bonsai<Context> bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder().build())
                .build();

        final ImmutableBonsaiBuilder<Context> bonsaiBuilder = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                        .id("k1")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .createKnot(Knot.builder()
                        .id("k2")
                        .knotData(ValuedKnotData.stringValue("d1"))
                        .version(123)
                        .build())
                .createMapping("key1", "k1");
        bonsaiBuilder.createEdge(Edge.builder()
                .version(1)
                .edgeIdentifier(new EdgeIdentifier("e1", 1, 1))
                .filter(new NotEqualsFilter("$.data", "male"))
                .knotId("k2").build());
        final Bonsai<Context> immutable = bonsaiBuilder.build();

        final boolean isKeyPresent = immutable.containsKey("key1");
        final boolean isKnotOnePresent = immutable.containsKnot("k1");
        final boolean isEdgeOnePresent = immutable.containsEdge("e1");
        final boolean isKnotThreePresent = immutable.containsKnot("k3");
        final boolean isEdgeThreePresent = immutable.containsEdge("e3");

        Assertions.assertTrue(isKeyPresent, "key1 should be present.");
        Assertions.assertTrue(isKnotOnePresent, "k1 should be present.");
        Assertions.assertTrue(isEdgeOnePresent, "e1 should be present.");
        Assertions.assertFalse(isKnotThreePresent, "k3 should be present.");
        Assertions.assertFalse(isEdgeThreePresent, "e3 should be present.");
    }

    @Test
    void given_immutableBonsaiTree_when_getEdge_then_returnEdge() {
        final Bonsai<Context> bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder().build())
                .build();

        final ImmutableBonsaiBuilder<Context> bonsaiBuilder = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                        .id("k1")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .createKnot(Knot.builder()
                        .id("k2")
                        .knotData(ValuedKnotData.stringValue("d1"))
                        .version(123)
                        .build());
        bonsaiBuilder.createEdge(Edge.builder()
                .version(1)
                .edgeIdentifier(new EdgeIdentifier("e1", 1, 1))
                .filter(new NotEqualsFilter("$.data", "male"))
                .knotId("k2").build());
        final Bonsai<Context> immutable = bonsaiBuilder.build();
        final Edge edge = immutable.getEdge("e1");

        Assertions.assertNotNull(edge, "Edge should not be null");
        Assertions.assertEquals("k2", edge.getKnotId(), "KnotId should not be null");
        Assertions.assertEquals("e1", edge.getEdgeIdentifier().getId(), "EdgeId should be : e1");
        Assertions.assertEquals(1, edge.getVersion(), "Version should be : 123");
        Assertions.assertEquals("$.data", edge.getFilters().get(0).getField(),
                "Field of Edge Filter should be : $.data");
    }

    @Test
    void given_immutableBonsaiTree_when_getAllEdges_then_returnAllEdges() {
        final Bonsai<Context> bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder().build())
                .build();

        final ImmutableBonsaiBuilder<Context> bonsaiBuilder = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                        .id("k1")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .createKnot(Knot.builder()
                        .id("k2")
                        .knotData(ValuedKnotData.stringValue("d1"))
                        .version(123)
                        .build())
                .createKnot(Knot.builder()
                        .id("k3")
                        .knotData(ValuedKnotData.stringValue("d3"))
                        .version(123)
                        .build())
                .createKnot(ValuedKnotData.stringValue("2"), null);
        bonsaiBuilder.createEdge(Edge.builder()
                .version(1)
                .edgeIdentifier(new EdgeIdentifier("e1", 1, 1))
                .filter(new NotEqualsFilter("$.data", "male"))
                .knotId("k3").build());
        bonsaiBuilder.addVariation("k1", Variation.builder()
                .knotId("k3")
                .filter(new NotEqualsFilter("$.data", "male"))
                .priority(1)
                .build());
        final Bonsai<Context> immutable = bonsaiBuilder.build();
        final List<String> edgeIds = new ArrayList<>();
        edgeIds.add("e1");
        edgeIds.add("e2");
        edgeIds.add("e3");
        final Map<String, Edge> edgeMap = immutable.getAllEdges(edgeIds);

        Assertions.assertEquals(3, edgeMap.size(), "The size of map should be three");
        Assertions.assertTrue("e1".equals(edgeMap.get("e1").getEdgeIdentifier().getId()),
                "The edgeId and edgeId in EdgeIdentifier should match.");
        Assertions.assertNull(edgeMap.get("e2"), "e2 edge should not exist.");
        Assertions.assertNull(edgeMap.get("e3"), "e3 edge should not exist.");
    }

    @Test
    void given_immutableAndMutableBonsaiTree_when_evaluate_then_returnNonNullKnot() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder().build())
                .build();

        Bonsai<Context> immutableBuilder = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                        .id("k1")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .createMapping("key1", "k1")
                .createKnot(Knot.builder()
                        .id("k2")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .createMapping("key2", "k2")
                .build();
        KeyNode k2 = immutableBuilder.evaluate("key1", Context.builder()
                .documentContext(Parsers.parse(Maps.newHashMap()))
                .build());
        Assertions.assertNotNull(k2);

        bonsai.createKnot(Knot.builder()
                .id("k3")
                .knotData(ValuedKnotData.stringValue("1"))
                .version(123)
                .build());
        bonsai.createMapping("key3", "k3");
        KeyNode k3 = immutableBuilder.evaluate("key3", Context.builder()
                .documentContext(Parsers.parse(Maps.newHashMap()))
                .build());
        Assertions.assertNotNull(k3);
    }

    @Test
    void given_immutableBonsaiTree_when_evaluateFlat_then_returnNonNullKnot() {
        final Bonsai<Context> bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder().build())
                .build();

        final Bonsai<Context> immutableBuilder = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                        .id("k1")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .createMapping("key1", "k1")
                .createKnot(Knot.builder()
                        .id("k2")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .createMapping("key2", "k2")
                .build();
        final FlatTreeRepresentation flatTree = immutableBuilder.evaluateFlat("key1", Context.builder()
                .documentContext(Parsers.parse(Maps.newHashMap()))
                .build());

        Assertions.assertNotNull(flatTree, "FlatTreeRepresentation should not be null for : key1 ");
    }

    @Test
    void given_immutableBonsaiTreeWithNonExistingKey_when_evaluateFlat_then_returnNonNullKnot() {
        final FlatTreeRepresentation flatTree = bonsai.evaluateFlat("key3", Context.builder()
                .documentContext(Parsers.parse(Maps.newHashMap()))
                .build());

        Assertions.assertNotNull(flatTree, "FlatTreeRepresentation should not be null for : key3 ");
    }

    @Test
    void given_immutableBonsaiTree_when_getMapping_then_returnKnotId() {
        final String knotId = bonsai.getMapping("key1");
        Assertions.assertEquals("k1", knotId, "Returned knotId should be : k1");
    }

    @Test
    void given_immutableBonsaiTree_when_createKnot_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            Bonsai<Context> bonsai = BonsaiBuilder.builder()
                    .withBonsaiProperties(BonsaiProperties.builder().build())
                    .build();

            Bonsai<Context> build = ImmutableBonsaiBuilder
                    .builder(bonsai)
                    .createKnot(Knot.builder()
                            .id("k1")
                            .knotData(ValuedKnotData.numberValue(1))
                            .version(123)
                            .build())
                    .createKnot(Knot.builder()
                            .id("k1")
                            .knotData(ValuedKnotData.numberValue(1))
                            .version(123)
                            .build())
                    .build();

            build.createKnot(Knot.builder()
                    .id("k2")
                    .knotData(ValuedKnotData.numberValue(1))
                    .version(123)
                    .build());
        });
    }

    @Test
    void given_mutableBonsaiTree_when_createKnot_then_returnNonNullKnot() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder().build())
                .build();

        Bonsai<Context> immutable = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                        .id("k1")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .createKnot(Knot.builder()
                        .id("k1")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .build();

        Knot k2 = bonsai.createKnot(Knot.builder()
                .id("k1")
                .knotData(ValuedKnotData.stringValue("2"))
                .version(123)
                .build());
        Assertions.assertNotNull(k2);
    }

    @Test
    void given_mutableBonsaiTree_when_createKnotWithKnotDataOnly_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            final KnotData knotData = ValuedKnotData.stringValue("knotValue");
            bonsai.createKnot(knotData, null);
        });
    }

    @Test
    void given_immutableBonsaiTree_when_updateKnotData_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            final String knotId = "knotOne";
            final KnotData knotData = ValuedKnotData.stringValue("knotValue");
            bonsai.updateKnotData(knotId, knotData, new HashMap<>());
        });
    }

    @Test
    void given_immutableBonsaiTree_when_deleteKnot_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            bonsai.deleteKnot("e1", false);
        });
    }

    @Test
    void given_immutableBonsaiTree_when_createMapping_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            Bonsai<Context> bonsai = BonsaiBuilder.builder()
                    .withBonsaiProperties(BonsaiProperties.builder().build())
                    .build();

            Bonsai<Context> immutable = ImmutableBonsaiBuilder
                    .builder(bonsai)
                    .createKnot(Knot.builder()
                            .id("k1")
                            .knotData(ValuedKnotData.stringValue("1"))
                            .version(123)
                            .build())
                    .createKnot(Knot.builder()
                            .id("k1")
                            .knotData(ValuedKnotData.stringValue("1"))
                            .version(123)
                            .build())
                    .build();

            immutable.createMapping("k2", "asdf");
        });
    }

    @Test
    void given_immutableBonsaiTree_when_createEdge_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            Bonsai<Context> bonsai = BonsaiBuilder.builder()
                    .withBonsaiProperties(BonsaiProperties.builder().build())
                    .build();

            Bonsai<Context> immutable = ImmutableBonsaiBuilder
                    .builder(bonsai)
                    .createKnot(Knot.builder()
                            .id("k1")
                            .knotData(ValuedKnotData.stringValue("1"))
                            .version(123)
                            .build())
                    .createKnot(Knot.builder()
                            .id("k1")
                            .knotData(ValuedKnotData.stringValue("1"))
                            .version(123)
                            .build())
                    .build();

            immutable.createEdge(null);
        });
    }

    @Test
    void given_immutableBonsaiBuilder_when_performingCRUDOperationOnKnotAndEdge_then_returnMeaningfulTree() {
        Bonsai<Context> bonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder().build())
                .build();

        ImmutableBonsaiBuilder<Context> bonsaiBuilder = ImmutableBonsaiBuilder
                .builder(bonsai)
                .createKnot(Knot.builder()
                        .id("k1")
                        .knotData(ValuedKnotData.stringValue("1"))
                        .version(123)
                        .build())
                .createKnot(Knot.builder()
                        .id("k2")
                        .knotData(ValuedKnotData.stringValue("d1"))
                        .version(123)
                        .build())
                .createKnot(Knot.builder()
                        .id("k3")
                        .knotData(ValuedKnotData.stringValue("d3"))
                        .version(123)
                        .build())
                .createKnot(ValuedKnotData.stringValue("2"), null);
        bonsaiBuilder.updateKnotData("k1", ValuedKnotData.stringValue("3"), new HashMap<>());
        bonsaiBuilder.deleteKnot("k2", false);
        bonsaiBuilder.createEdge(Edge.builder()
                .version(1)
                .edgeIdentifier(new EdgeIdentifier("e1", 1, 1))
                .filter(new NotEqualsFilter("$.data", "male"))
                .knotId("k3").build());
        bonsaiBuilder.addVariation("k1", Variation.builder()
                .knotId("k3")
                .filter(new NotEqualsFilter("$.data", "male"))
                .priority(1)
                .build());
        Bonsai<Context> immutable = bonsaiBuilder.build();


        Assertions.assertNotNull(immutable.getKnot("k1"));
        Assertions.assertEquals("3", ((StringValue) ((ValuedKnotData) immutable.getKnot("k1")
                .getKnotData()).getValue()).getValue());
        Assertions.assertNull(immutable.getKnot("k2"));
        Assertions.assertNotNull(immutable.getEdge("e1"));

    }

    @Test
    void given_immutableBonsaiTree_when_addVariation_then_throwBonsaiError() throws BonsaiError {
        assertThrows(BonsaiError.class, () -> {
            Edge edge1 = bonsai.addVariation("k1",
                    Variation.builder()
                            .filters(Lists.newArrayList(new EqualsFilter("$.gender", "female")))
                            .knotId("k2")
                            .build());
        });
    }

    @Test
    void given_immutableBonsaiTree_when_updateEdgeFilters_then_throwBonsaiError() throws BonsaiError {
        assertThrows(BonsaiError.class, () -> {
            Edge edge = bonsai.updateVariation("k1", "e1",
                    Variation.builder().filters(Lists.newArrayList(new EqualsFilter("$.gender2", "female"))).build());
        });
    }

    @Test
    void given_immutableBonsaiTree_when_unlinkVariation_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            bonsai.unlinkVariation("knotId", "edgeId");
        });
    }

    @Test
    void given_immutableBonsaiTree_when_deleteVariation_then_throwBonsaiError() throws BonsaiError {
        assertThrows(BonsaiError.class, () -> {
            TreeEdge treeEdge = bonsai.deleteVariation("k1", "e1", false);
        });
    }

    @Test
    void given_immutableBonsaiTree_when_createMappingWithKeyE1_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            bonsai.createMapping("e1", ValuedKnotData.stringValue("asdf"), null);
        });
    }

    @Test
    void given_immutableBonsaiTree_when_removeMapping_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            bonsai.removeMapping("e1");
        });
    }

    @Test
    void given_immutableBonsaiTree_when_getCompleteTree_then_returnCompleteTree() {
        final TreeKnot treeKnot = bonsai.getCompleteTree("key1");
        Assertions.assertNotNull(treeKnot, "TreeKnot should not be null for key1");
        Assertions.assertEquals("k1", treeKnot.getId(), "Treeknot id should be : k1");
        Assertions.assertEquals(123, treeKnot.getVersion());
        Assertions.assertNull(treeKnot.getTreeEdges(), "TreeKnot has zero TreeEdge for key1");
        Assertions.assertNotNull(treeKnot.getKnotData(), "TreeKnot's KnotData should not be null");
    }

    @Test
    void given_immutableBonsaiTree_when_getCompleteTreeWithDeltaOperations_then_returnCompleteTree() {

        final List<DeltaOperation> deltaOperationList = Arrays.asList(
                new KnotDeltaOperation(
                        Knot.builder()
                                .edges(null)
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("Value Changed"))
                                .build()
                )
        );

        final List<DeltaOperation> revertDeltaOperationList = new ArrayList<>();
        final TreeKnot treeKnot = bonsai.getCompleteTreeWithDeltaOperations("key1", deltaOperationList).getTreeKnot();
        Assertions.assertNotNull(treeKnot, "TreeKnot should not be null for key1");
        Assertions.assertEquals("k1", treeKnot.getId(), "Treeknot id should be : k1");
        Assertions.assertEquals(0, treeKnot.getVersion());
        Assertions.assertNotNull(treeKnot.getKnotData(), "TreeKnot's KnotData should not be null");
    }

    @Test
    void given_immutableBonsaiTree_when_applyPendingUpdatesOnCompleteTree_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            final List<DeltaOperation> revertDeltaOperationList = new ArrayList<>();
            bonsai.applyDeltaOperations("key1", new ArrayList<>());
        });
    }
}