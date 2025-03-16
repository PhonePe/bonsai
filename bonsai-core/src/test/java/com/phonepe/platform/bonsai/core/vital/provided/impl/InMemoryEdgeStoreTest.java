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

package com.phonepe.platform.bonsai.core.vital.provided.impl;

import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryEdgeStoreTest {

    private InMemoryEdgeStore inMemoryEdgeStore;

    @BeforeEach
    public void setUp() {
        inMemoryEdgeStore = new InMemoryEdgeStore();
    }

    @AfterEach
    public void tearDown() {
        inMemoryEdgeStore = null;
    }

    @Test
    void given_inMemoryKeyTreeStore_when_checkingEdgePresenceInMemory_then_returnKnotId() {
        final Edge edge = getAnEdge("E1");
        inMemoryEdgeStore.mapEdge(edge.getEdgeIdentifier().getId(), edge);
        final boolean isEdgeOnePresent = inMemoryEdgeStore.containsEdge("E1");
        final boolean isEdgeTwoPresent = inMemoryEdgeStore.containsEdge("E2");
        final boolean isEdgeThreePresent = inMemoryEdgeStore.containsEdge(null);

        assertTrue(isEdgeOnePresent);
        assertFalse(isEdgeTwoPresent);
        assertFalse(isEdgeThreePresent);
    }

    @Test
    void given_inMemoryEdgeStore_when_mappingEdgeWithRightEdgeId_then_returnAnEdge() {
        final Edge edge = getAnEdge("E1");
        final Edge firstReturnedEdgeCopy = inMemoryEdgeStore.mapEdge(edge.getEdgeIdentifier().getId(), edge);
        final Edge secondReturnedEdgeCopy = inMemoryEdgeStore.getEdge(edge.getEdgeIdentifier().getId());

        assertNull(firstReturnedEdgeCopy, "Previous copy of an Edge should be null.");
        assertNotNull(secondReturnedEdgeCopy, "Edge should not be null.");
        assertEquals(edge.getEdgeIdentifier().getId(),
                secondReturnedEdgeCopy.getEdgeIdentifier().getId(),
                "Edge identifier should match.");
        assertEquals(edge.getKnotId(), secondReturnedEdgeCopy.getKnotId(), "KnotId should match.");
        assertEquals(edge.getFilters().size(),
                secondReturnedEdgeCopy.getFilters().size(),
                "Both edges should have equal number of filters.");
    }

    @Test
    void given_inMemoryEdgeStore_when_mappingEdgeWithNullEdgeId_then_returnNullObject() {
        final Edge edge = getAnEdge("E1");
        final Edge firstReturnedEdgeCopy = inMemoryEdgeStore.mapEdge(null, edge);
        final Edge secondReturnedEdgeCopy = inMemoryEdgeStore.getEdge(null);

        assertNull(firstReturnedEdgeCopy, "Previous copy of an Edge should be null");
        assertNull(secondReturnedEdgeCopy, "Edge should be null for null edgeId");
    }

    @Test
    void given_inMemoryEdgeStore_when_deletingEdge_then_deleteEdge() {
        final Edge edge = getAnEdge("E1");
        final Edge firstReturnedEdgeCopy = inMemoryEdgeStore.mapEdge(edge.getEdgeIdentifier().getId(), edge);
        final Edge secondReturnedEdgeCopy = inMemoryEdgeStore.deleteEdge(edge.getEdgeIdentifier().getId());
        final Edge thirdReturnedEdgeCopy = inMemoryEdgeStore.getEdge(edge.getEdgeIdentifier().getId());

        assertNull(firstReturnedEdgeCopy, "Previous copy of an Edge should be null.");
        assertNotNull(secondReturnedEdgeCopy, "Previous copy of an Egde should not be null after deleting.");
        assertEquals(edge.getEdgeIdentifier().getId(),
                secondReturnedEdgeCopy.getEdgeIdentifier().getId(),
                "Edge identifier should match.");
        assertEquals(edge.getKnotId(), secondReturnedEdgeCopy.getKnotId(), "KnotId should match.");
        assertEquals(edge.getFilters().size(),
                secondReturnedEdgeCopy.getFilters().size(),
                "Both edges should have equal number of filters.");
        assertNull(thirdReturnedEdgeCopy, "Edge corresponding to given E1 should be null.");
    }

    @Test
    void given_inMemoryEdgeStore_when_deletingNonExistingEdge_then_returnNull() {
        final Edge secondReturnedEdgeCopy = inMemoryEdgeStore.deleteEdge(null);
        assertNull(secondReturnedEdgeCopy, "Edge should be null for null edgeId.");
    }

    @Test
    void given_inMemoryEdgeStore_when_gettingAllEdges_then_returnAllEdges() {
        final Edge edgeOne = getAnEdge("E1");
        final Edge edgeTwo = getAnEdge("E2");
        final Edge firstReturnedEdgeCopy = inMemoryEdgeStore.mapEdge(edgeOne.getEdgeIdentifier().getId(), edgeOne);
        final Edge secondReturnedEdgeCopy = inMemoryEdgeStore.mapEdge(edgeTwo.getEdgeIdentifier().getId(), edgeTwo);
        final List<String> ids = Arrays.asList("E1", "E2", "E3");
        final Map<String, Edge> thirdReturnedEdgeMap = inMemoryEdgeStore.getAllEdges(ids);

        assertNull(firstReturnedEdgeCopy, "Previous copy of an Edge should be null.");
        assertNull(secondReturnedEdgeCopy, "Previous copy of an Edge should be null.");
        assertEquals(3, thirdReturnedEdgeMap.size(), "The size of map should be 3.");
        assertEquals("knotId", thirdReturnedEdgeMap.get("E1").getKnotId(), "KnotId should match : knotId.");
        assertEquals("E1",
                thirdReturnedEdgeMap.get("E1").getEdgeIdentifier().getId(),
                "Edge identifier should match : E1.");
        assertEquals("knotId", thirdReturnedEdgeMap.get("E2").getKnotId(), "KnotId should match : knotId.");
        assertEquals("E2",
                thirdReturnedEdgeMap.get("E2").getEdgeIdentifier().getId(),
                "Edge identifier should match : E2.");
        assertNull(thirdReturnedEdgeMap.get("E3"), "There is no Edge corresponding to EdgeId : E3.");
    }

    private Edge getAnEdge(final String edgeId) {
        return Edge.builder()
                .edgeIdentifier(new EdgeIdentifier(edgeId, 1, 1))
                .filter(new EqualsFilter("field", "value"))
                .knotId("knotId")
                .build();
    }
}