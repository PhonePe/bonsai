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

import com.phonepe.commons.bonsai.core.Bonsai;
import com.phonepe.commons.bonsai.models.blocks.Edge;
import com.phonepe.commons.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.commons.bonsai.models.blocks.Knot;
import com.phonepe.commons.bonsai.models.blocks.Variation;
import com.phonepe.commons.bonsai.models.data.KnotData;
import com.phonepe.commons.bonsai.models.data.ValuedKnotData;
import com.phonepe.commons.bonsai.models.structures.OrderedList;
import com.phonepe.commons.query.dsl.Filter;
import com.phonepe.commons.query.dsl.general.EqualsFilter;
import com.phonepe.commons.query.dsl.general.NotEqualsFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ImmutableBonsaiBuilderTest {

    private Bonsai<Context> mutableBonsaiTree;
    private ImmutableBonsaiBuilder<Context> immutableBonsaiBuilder;

    @BeforeEach
    public void setUp() {
        mutableBonsaiTree = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder()
                        .maxAllowedConditionsPerEdge(10)
                        .maxAllowedVariationsPerKnot(10)
                        .mutualExclusivitySettingTurnedOn(true)
                        .build())
                .build();
        immutableBonsaiBuilder = ImmutableBonsaiBuilder.builder(mutableBonsaiTree);
    }

    @AfterEach
    public void tearDown() {
        mutableBonsaiTree = null;
        immutableBonsaiBuilder = null;
    }

    @Test
    void given_immutableBonsaiBuilder_when_buildingImmutableBonsaiTree_then_buildImmutableBonsaiTree() {
        final Bonsai<Context> mutableBonsaiTree = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder()
                        .maxAllowedConditionsPerEdge(10)
                        .mutualExclusivitySettingTurnedOn(false)
                        .build())
                .build();
        final Bonsai<Context> immutableBonsaiTree = ImmutableBonsaiBuilder
                .builder(mutableBonsaiTree)
                .build();

        assertNotNull(immutableBonsaiTree);
    }

    @Test
    void given_immutableBonsaiBuilder_when_addingKnotsIntoImmutableBonsaiBuilder_then_doThoseOperations() {
        final Bonsai<Context> mutableBonsaiTree = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder()
                        .maxAllowedConditionsPerEdge(10)
                        .maxAllowedVariationsPerKnot(10)
                        .mutualExclusivitySettingTurnedOn(true)
                        .build())
                .build();

        ImmutableBonsaiBuilder<Context> immutableBonsaiTreeBuilder = ImmutableBonsaiBuilder
                .builder(mutableBonsaiTree);

        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.createKnot(
                Knot.builder()
                        .id("K1")
                        .knotData(ValuedKnotData.stringValue("K1 Data"))
                        .version(123)
                        .build());
        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.createMapping("Key1", "K1");
        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.updateKnotData(
                "K1", ValuedKnotData.stringValue("New K1 Data"), new HashMap<>());

        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.createKnot(
                Knot.builder()
                        .id("K2")
                        .knotData(ValuedKnotData.stringValue("K2 Data"))
                        .version(234)
                        .build());
        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.createMapping("Key2", "K2");
        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.updateKnotData(
                "K2", ValuedKnotData.stringValue("New K2 Data"), new HashMap<>());

        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.addVariation("K1",
                Variation.builder()
                        .knotId("K2")
                        .priority(1)
                        .filter(EqualsFilter.builder().field("Field").value("value").build())
                        .build());

        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.deleteKnot("K1", false);

        final Bonsai<Context> immutableBonsaiTree = immutableBonsaiTreeBuilder.build();

        assertNotNull(immutableBonsaiTree);
        assertNotNull(immutableBonsaiTree.getKnot("K2"));
        assertNull(immutableBonsaiTree.getKnot("K1"));
    }

    @Test
    void given_immutableBonsaiBuilder_when_addingEdgeIntoImmutableBonsaiBuilder_then_doThoseOperations() {
        final Bonsai<Context> mutableBonsaiTree = BonsaiBuilder.builder()
                .withBonsaiProperties(BonsaiProperties.builder()
                        .maxAllowedConditionsPerEdge(10)
                        .maxAllowedVariationsPerKnot(10)
                        .mutualExclusivitySettingTurnedOn(true)
                        .build())
                .build();

        ImmutableBonsaiBuilder<Context> immutableBonsaiTreeBuilder = ImmutableBonsaiBuilder
                .builder(mutableBonsaiTree);

        OrderedList<EdgeIdentifier> edges = new OrderedList<>();
        Edge e1Edge = Edge.builder()
                .edgeIdentifier(new EdgeIdentifier("E1", 1, 1))
                .filter(EqualsFilter.builder().field("fieldOne").value("valueOne").build())
                .version(1234)
                .knotId("K2")
                .build();
        edges.add(e1Edge.getEdgeIdentifier());

        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.createKnot(
                Knot.builder()
                        .id("K1")
                        .knotData(ValuedKnotData.stringValue("K1 Data"))
                        .edges(edges)
                        .version(123)
                        .build());

        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.createEdge(e1Edge);

        final List<Filter> edgeFilters =
                Arrays.asList(EqualsFilter.builder().field("fieldOne").value("valueOne").build(),
                        NotEqualsFilter.builder().field("fieldOne").value("valueTwo").build());
        final Variation updateVariation = Variation.builder().filters(edgeFilters).build();
        immutableBonsaiTreeBuilder = immutableBonsaiTreeBuilder.updateVariation("K1", "E1", updateVariation);

        final Bonsai<Context> immutableBonsaiTree = immutableBonsaiTreeBuilder.build();

        assertNotNull(immutableBonsaiTree);
        assertNotNull(immutableBonsaiTree.getKnot("K1"));
        assertNull(immutableBonsaiTree.getKnot("K2"));
        assertNotNull(immutableBonsaiTree.getEdge("E1"));
        assertEquals(2, immutableBonsaiTree.getEdge("E1").getFilters().size());
    }

    @Test
    void given_immutableBonsaiTree_when_creatingKnotAndCapture_then_saveKnotAndReturnPreviousValueCorrespondingToKnotId() {
        final Knot knot = Knot.builder()
                .id("K1")
                .knotData(ValuedKnotData.stringValue("K1 Data"))
                .version(123)
                .build();

        final Knot capturedPreviousKnot = immutableBonsaiBuilder.createKnotAndCapture(knot);
        final Bonsai<Context> immutableBonsaiTree = immutableBonsaiBuilder.build();
        final Knot fetchedKnot = immutableBonsaiTree.getKnot("K1");

        assertNull(capturedPreviousKnot);
        assertNotNull(fetchedKnot);
    }

    @Test
    void given_immutableBonsaiTree_when_creatingKnotAndCaptureWithKnotData_then_saveKnotAndReturnValueCorrespondingToKnotId() {
        final KnotData knotData = ValuedKnotData.stringValue("K1 Data");
        final Knot capturedPreviousKnot = immutableBonsaiBuilder.createKnotAndCapture(knotData, null);
        final Bonsai<Context> immutableBonsaiTree = immutableBonsaiBuilder.build();
        final Knot fetchedKnot = immutableBonsaiTree.getKnot(capturedPreviousKnot.getId());

        assertNotNull(capturedPreviousKnot);
        assertNotNull(fetchedKnot);
    }

    @Test
    void given_immutableBonsaiTree_when_creatingMappingAndCaptureWithKnotData_then_saveKnotAndPreviousValueCorrespondingToKnotId() {
        final KnotData knotData = ValuedKnotData.stringValue("K1 Data");
        final Knot capturedPreviousKnot = immutableBonsaiBuilder.createMappingAndCapture("key", knotData, null);
        final Bonsai<Context> immutableBonsaiTree = immutableBonsaiBuilder.build();
        final Knot fetchedKnot = immutableBonsaiTree.getKnot(capturedPreviousKnot.getId());

        assertNotNull(capturedPreviousKnot);
        assertNotNull(fetchedKnot);
    }

    @Test
    void given_immutableBonsaiTree_when_addingVariationAndCapture_then_saveVariation() {
        final Knot knotOne = Knot.builder()
                .id("K1")
                .knotData(ValuedKnotData.stringValue("K1 Data"))
                .version(123)
                .build();
        final Knot knotTwo = Knot.builder()
                .id("K2")
                .knotData(ValuedKnotData.stringValue("K2 Data"))
                .version(234)
                .build();

        immutableBonsaiBuilder.createKnotAndCapture(knotOne);
        immutableBonsaiBuilder.createKnotAndCapture(knotTwo);
        final Variation variation = Variation.builder()
                .priority(1)
                .filter(EqualsFilter.builder().field("fieldOne").value("valueOne").build())
                .knotId("K2")
                .build();
        final Edge capturedEdge = immutableBonsaiBuilder.addVariationAndCapture("K1", variation);
        final Bonsai<Context> immutableBonsaiTree = immutableBonsaiBuilder.build();
        final Edge fetchedEdge = immutableBonsaiTree.getEdge(capturedEdge.getEdgeIdentifier().getId());

        assertNotNull(capturedEdge);
        assertNotNull(capturedEdge.getEdgeIdentifier().getId());
        assertEquals("K2", capturedEdge.getKnotId());
        assertNotNull(fetchedEdge);
        assertEquals("K2", fetchedEdge.getKnotId());
    }
}