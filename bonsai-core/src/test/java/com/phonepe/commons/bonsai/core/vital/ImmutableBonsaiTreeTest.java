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

package com.phonepe.commons.bonsai.core.vital;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.phonepe.commons.bonsai.core.Bonsai;
import com.phonepe.commons.bonsai.core.Parsers;
import com.phonepe.commons.bonsai.core.exception.BonsaiError;
import com.phonepe.commons.bonsai.models.KeyNode;
import com.phonepe.commons.bonsai.models.blocks.Edge;
import com.phonepe.commons.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.commons.bonsai.models.blocks.Knot;
import com.phonepe.commons.bonsai.models.blocks.Variation;
import com.phonepe.commons.bonsai.models.blocks.delta.DeltaOperation;
import com.phonepe.commons.bonsai.models.blocks.delta.KnotDeltaOperation;
import com.phonepe.commons.bonsai.models.blocks.model.TreeKnot;
import com.phonepe.commons.bonsai.models.data.KnotData;
import com.phonepe.commons.bonsai.models.data.ValuedKnotData;
import com.phonepe.commons.bonsai.models.model.FlatTreeRepresentation;
import com.phonepe.commons.bonsai.models.value.StringValue;
import com.phonepe.commons.query.dsl.general.EqualsFilter;
import com.phonepe.commons.query.dsl.general.NotEqualsFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ImmutableBonsaiTreeTest {

    private final Bonsai<Context> mutableBonsai = BonsaiBuilder.builder()
            .withBonsaiProperties(BonsaiProperties.builder()
                    .maxAllowedConditionsPerEdge(Integer.MAX_VALUE)
                    .mutualExclusivitySettingTurnedOn(false)
                    .build())
            .build();
    private final Bonsai<Context> immutableBonsai = ImmutableBonsaiBuilder
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
        final FlatTreeRepresentation flatTree = immutableBonsai.evaluateFlat("key3", Context.builder()
                .documentContext(Parsers.parse(Maps.newHashMap()))
                .build());

        Assertions.assertNotNull(flatTree, "FlatTreeRepresentation should not be null for : key3 ");
    }

    @Test
    void given_immutableBonsaiTree_when_getMapping_then_returnKnotId() {
        final String knotId = immutableBonsai.getMapping("key1");
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
        Bonsai<Context> newBonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder().build())
                .build();

        Knot k2 = newBonsai.createKnot(Knot.builder()
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
            immutableBonsai.createKnot(knotData, null);
        });
    }

    @Test
    void given_immutableBonsaiTree_when_updateKnotData_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            final String knotId = "knotOne";
            final KnotData knotData = ValuedKnotData.stringValue("knotValue");
            immutableBonsai.updateKnotData(knotId, knotData, new HashMap<>());
        });
    }

    @Test
    void given_immutableBonsaiTree_when_deleteKnot_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> immutableBonsai.deleteKnot("e1", false));
    }

    @Test
    void given_immutableBonsaiTree_when_createMapping_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> {
            Bonsai<Context> newBonsai = BonsaiBuilder.builder()
                    .withBonsaiProperties(BonsaiProperties.builder().build())
                    .build();

            Bonsai<Context> immutable = ImmutableBonsaiBuilder
                    .builder(newBonsai)
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
            Bonsai<Context> newBonsai = BonsaiBuilder.builder()
                    .withBonsaiProperties(BonsaiProperties.builder().build())
                    .build();

            Bonsai<Context> immutable = ImmutableBonsaiBuilder
                    .builder(newBonsai)
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
        Bonsai<Context> newBonsai = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder().build())
                .build();

        ImmutableBonsaiBuilder<Context> bonsaiBuilder = ImmutableBonsaiBuilder
                .builder(newBonsai)
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
        assertThrows(BonsaiError.class, () ->
                immutableBonsai.addVariation("k1", Variation.builder()
                        .filters(Lists.newArrayList(new EqualsFilter("$.gender", "female")))
                        .knotId("k2")
                        .build()));
    }

    @Test
    void given_immutableBonsaiTree_when_updateEdgeFilters_then_throwBonsaiError() throws BonsaiError {
        assertThrows(BonsaiError.class, () -> immutableBonsai.updateVariation("k1", "e1",
                                                                              Variation.builder().filters(Lists.newArrayList(new EqualsFilter("$.gender2", "female"))).build()));
    }

    @Test
    void given_immutableBonsaiTree_when_unlinkVariation_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> immutableBonsai.unlinkVariation("knotId", "edgeId"));
    }

    @Test
    void given_immutableBonsaiTree_when_deleteVariation_then_throwBonsaiError() throws BonsaiError {
        assertThrows(BonsaiError.class, () -> immutableBonsai.deleteVariation("k1", "e1", false));
    }

    @Test
    void given_immutableBonsaiTree_when_createMappingWithKeyE1_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> immutableBonsai.createMapping("e1", ValuedKnotData.stringValue("asdf"), null));
    }

    @Test
    void given_immutableBonsaiTree_when_removeMapping_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> immutableBonsai.removeMapping("e1"));
    }

    @Test
    void given_immutableBonsaiTree_when_getCompleteTree_then_returnCompleteTree() {
        final TreeKnot treeKnot = immutableBonsai.getCompleteTree("key1");
        Assertions.assertNotNull(treeKnot, "TreeKnot should not be null for key1");
        Assertions.assertEquals("k1", treeKnot.getId(), "Treeknot id should be : k1");
        Assertions.assertEquals(123, treeKnot.getVersion());
        Assertions.assertNull(treeKnot.getTreeEdges(), "TreeKnot has zero TreeEdge for key1");
        Assertions.assertNotNull(treeKnot.getKnotData(), "TreeKnot's KnotData should not be null");
    }

    @Test
    void given_immutableBonsaiTree_when_getCompleteTreeWithDeltaOperations_then_returnCompleteTree() {

        final List<DeltaOperation> deltaOperationList = List.of(
                new KnotDeltaOperation(
                        Knot.builder()
                                .edges(null)
                                .id("k1")
                                .knotData(ValuedKnotData.stringValue("Value Changed"))
                                .build()
                )
        );

        final TreeKnot treeKnot = immutableBonsai.getCompleteTreeWithDeltaOperations("key1", deltaOperationList).getTreeKnot();
        Assertions.assertNotNull(treeKnot, "TreeKnot should not be null for key1");
        Assertions.assertEquals("k1", treeKnot.getId(), "Treeknot id should be : k1");
        Assertions.assertEquals(0, treeKnot.getVersion());
        Assertions.assertNotNull(treeKnot.getKnotData(), "TreeKnot's KnotData should not be null");
    }

    @Test
    void given_immutableBonsaiTree_when_applyPendingUpdatesOnCompleteTree_then_throwBonsaiError() {
        assertThrows(BonsaiError.class, () -> immutableBonsai.applyDeltaOperations("key1", new ArrayList<>()));
    }
}