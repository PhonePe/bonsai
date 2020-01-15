package com.phonepe.platform.bonsai.core.vital.provided.impl;

import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

/**
 * @author - suraj.s
 * @date - 2019-11-19
 */
public class InMemoryEdgeStoreTest {

    private InMemoryEdgeStore inMemoryEdgeStore;

    @Before
    public void setUp() throws Exception {
        inMemoryEdgeStore = new InMemoryEdgeStore();
    }

    @After
    public void tearDown() throws Exception {
        inMemoryEdgeStore = null;
    }

    @Test
    public void given_inMemoryEdgeStore_when_mappingEdgeWithRightEdgeId_then_returnAnEdge() {
        final Edge edge = getAnEdge("E1");
        final Edge firstReturnedEdgeCopy = inMemoryEdgeStore.mapEdge(edge.getEdgeIdentifier().getId(), edge);
        final Edge secondReturnedEdgeCopy = inMemoryEdgeStore.getEdge(edge.getEdgeIdentifier().getId());

        assertNull("Previous copy of an Edge should be null.", firstReturnedEdgeCopy);
        assertNotNull("Edge should not be null.", secondReturnedEdgeCopy);
        assertEquals("Edge identifier should match.", edge.getEdgeIdentifier().getId(),
                secondReturnedEdgeCopy.getEdgeIdentifier().getId());
        assertEquals("KnotId should match.", edge.getKnotId(), secondReturnedEdgeCopy.getKnotId());
        assertEquals("Both edges should have equal number of filters.", edge.getFilters().size(),
                secondReturnedEdgeCopy.getFilters().size());
    }

    @Test
    public void given_inMemoryEdgeStore_when_mappingEdgeWithNullEdgeId_then_returnNullObject() {
        final Edge edge = getAnEdge("E1");
        final Edge firstReturnedEdgeCopy = inMemoryEdgeStore.mapEdge(null, edge);
        final Edge secondReturnedEdgeCopy = inMemoryEdgeStore.getEdge(null);

        assertNull("Previous copy of an Edge should be null", firstReturnedEdgeCopy);
        assertNull("Edge should be null for null edgeId", secondReturnedEdgeCopy);
    }

    @Test
    public void given_inMemoryEdgeStore_when_deletingEdge_then_deleteEdge() {
        final Edge edge = getAnEdge("E1");
        final Edge firstReturnedEdgeCopy = inMemoryEdgeStore.mapEdge(edge.getEdgeIdentifier().getId(), edge);
        final Edge secondReturnedEdgeCopy = inMemoryEdgeStore.deleteEdge(edge.getEdgeIdentifier().getId());
        final Edge thirdReturnedEdgeCopy = inMemoryEdgeStore.getEdge(edge.getEdgeIdentifier().getId());

        assertNull("Previous copy of an Edge should be null.", firstReturnedEdgeCopy);
        assertNotNull("Previous copy of an Egde should not be null after deleting.", secondReturnedEdgeCopy);
        assertEquals("Edge identifier should match.", edge.getEdgeIdentifier().getId(),
                secondReturnedEdgeCopy.getEdgeIdentifier().getId());
        assertEquals("KnotId should match.", edge.getKnotId(), secondReturnedEdgeCopy.getKnotId());
        assertEquals("Both edges should have equal number of filters.", edge.getFilters().size(),
                secondReturnedEdgeCopy.getFilters().size());
        assertNull("Edge corresponding to given E1 should be null.", thirdReturnedEdgeCopy);
    }

    @Test
    public void given_inMemoryEdgeStore_when_deletingNonExistingEdge_then_returnNull() {
        final Edge secondReturnedEdgeCopy = inMemoryEdgeStore.deleteEdge(null);
        assertNull("Edge should be null for null edgeId.", secondReturnedEdgeCopy);
    }

    @Test
    public void given_inMemoryEdgeStore_when_gettingAllEdges_then_returnAllEdges() {
        final Edge edgeOne = getAnEdge("E1");
        final Edge edgeTwo = getAnEdge("E2");
        final Edge firstReturnedEdgeCopy = inMemoryEdgeStore.mapEdge(edgeOne.getEdgeIdentifier().getId(), edgeOne);
        final Edge secondReturnedEdgeCopy = inMemoryEdgeStore.mapEdge(edgeTwo.getEdgeIdentifier().getId(), edgeTwo);
        final List<String> ids = Arrays.asList("E1", "E2", "E3");
        final Map<String, Edge> thirdReturnedEdgeMap = inMemoryEdgeStore.getAllEdges(ids);

        assertNull("Previous copy of an Edge should be null.", firstReturnedEdgeCopy);
        assertNull("Previous copy of an Edge should be null.", secondReturnedEdgeCopy);
        assertEquals("The size of map should be 3.", 3, thirdReturnedEdgeMap.size());
        assertEquals("KnotId should match : knotId.", "knotId", thirdReturnedEdgeMap.get("E1").getKnotId());
        assertEquals("Edge identifier should match : E1.", "E1",
                thirdReturnedEdgeMap.get("E1").getEdgeIdentifier().getId());
        assertEquals("KnotId should match : knotId.", "knotId", thirdReturnedEdgeMap.get("E2").getKnotId());
        assertEquals("Edge identifier should match : E2.", "E2",
                thirdReturnedEdgeMap.get("E2").getEdgeIdentifier().getId());
        assertNull("There is no Edge corresponding to EdgeId : E3.", thirdReturnedEdgeMap.get("E3"));
    }

    private Edge getAnEdge(final String edgeId) {
        return Edge.builder()
                .edgeIdentifier(new EdgeIdentifier(edgeId, 1, 1))
                .filter(new EqualsFilter("field", "value"))
                .knotId("knotId")
                .build();
    }
}