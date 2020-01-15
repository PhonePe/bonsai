package com.phonepe.platform.bonsai.core.visitor.delta.impl;

import com.phonepe.platform.bonsai.core.vital.provided.EdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KeyTreeStore;
import com.phonepe.platform.bonsai.core.vital.provided.KnotStore;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryEdgeStore;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryKeyTreeStore;
import com.phonepe.platform.bonsai.core.vital.provided.impl.InMemoryKnotStore;
import com.phonepe.platform.bonsai.models.blocks.Edge;
import com.phonepe.platform.bonsai.models.blocks.EdgeIdentifier;
import com.phonepe.platform.bonsai.models.blocks.Knot;
import com.phonepe.platform.bonsai.models.blocks.delta.EdgeDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KeyMappingDeltaOperation;
import com.phonepe.platform.bonsai.models.blocks.delta.KnotDeltaOperation;
import com.phonepe.platform.bonsai.models.data.ValuedKnotData;
import com.phonepe.platform.bonsai.models.structures.OrderedList;
import com.phonepe.platform.query.dsl.general.EqualsFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author - suraj.s
 * @date - 2019-11-25
 */
public class SaveDataOperationIntoStoreVisitorImplTest {

    private KeyTreeStore<String, String> keyTreeStore;
    private KnotStore<String, Knot> knotStore;
    private EdgeStore<String, Edge> edgeStore;
    private SaveDataOperationIntoStoreVisitorImpl storeVisitor;

    @Before
    public void setUp() throws Exception {
        keyTreeStore = new InMemoryKeyTreeStore();
        knotStore = new InMemoryKnotStore();
        edgeStore = new InMemoryEdgeStore();
        storeVisitor = new SaveDataOperationIntoStoreVisitorImpl(keyTreeStore, knotStore, edgeStore);
    }

    @After
    public void tearDown() throws Exception {
        keyTreeStore = null;
        knotStore = null;
        edgeStore = null;
        storeVisitor = null;
    }

    @Test
    public void given_saveDataOperationIntoStoreVisitorImpl_when_savingKeyMappingDeltaOperationIntoKeyStore_then_saveTheMapping() {
        final KeyMappingDeltaOperation keyMappingDeltaData = new KeyMappingDeltaOperation("key", "knotId");
        storeVisitor.visit(keyMappingDeltaData);

        final String fetchedKnotId = keyTreeStore.getKeyTree("key");
        assertEquals("knotId", fetchedKnotId);
    }

    @Test
    public void given_saveDataOperationIntoStoreVisitorImpl_when_savingKnotDeltaOperationIntoKnotStore_then_saveKnot() {
        final OrderedList<EdgeIdentifier> edges = new OrderedList<>();
        edges.add(new EdgeIdentifier("E1", 1, 1));
        edges.add(new EdgeIdentifier("E2", 2, 2));
        final KnotDeltaOperation knotDeltaData = new KnotDeltaOperation(
                Knot.builder()
                        .id("K0")
                        .knotData(ValuedKnotData.stringValue("Top Level Knot"))
                        .edges(edges)
                        .build()
        );

        storeVisitor.visit(knotDeltaData);

        final Knot fetchedKnot = knotStore.getKnot("K0");
        assertNotNull(fetchedKnot);
        assertNotEquals(0, fetchedKnot.getVersion());
        assertEquals("K0", fetchedKnot.getId());
        assertEquals(2, fetchedKnot.getEdges().size());
        assertEquals("VALUED", fetchedKnot.getKnotData().getKnotDataType().toString());
        final List<String> edgeIds = fetchedKnot.getEdges().stream()
                .map(EdgeIdentifier::getId).collect(Collectors.toList());
        final Edge edgeOne = edgeStore.getEdge(edgeIds.get(0));
        assertNotNull(edgeOne);
        assertEquals(0, edgeOne.getVersion());
        final Edge edgeTwo = edgeStore.getEdge(edgeIds.get(1));
        assertNotNull(edgeTwo);
        assertEquals(0, edgeTwo.getVersion());
    }

    @Test
    public void given_saveDataOperationIntoStoreVisitorImpl_when_savingEdgeDeltaOperationIntoEdgeStore_then_saveEdge() {
        final EdgeDeltaOperation edgeDeltaData = new EdgeDeltaOperation(
                Edge.builder()
                        .edgeIdentifier(new EdgeIdentifier("E1", 1, 1))
                        .knotId("K1")
                        .filters(Arrays.asList(EqualsFilter.builder().field("fieldLeaf").value("valueLeaf").build()))
                        .build()
        );

        storeVisitor.visit(edgeDeltaData);

        final Edge fetchedEdge = edgeStore.getEdge(edgeDeltaData.getEdge().getEdgeIdentifier().getId());
        assertNotNull(fetchedEdge);
        assertEquals("E1", fetchedEdge.getEdgeIdentifier().getId());
        assertEquals("K1", fetchedEdge.getKnotId());
        assertNotEquals(0, fetchedEdge.getVersion());
        assertEquals(1, fetchedEdge.getFilters().size());
        final Knot fetchedKnot = knotStore.getKnot(edgeDeltaData.getEdge().getKnotId());
        assertNotNull(fetchedKnot);
        assertEquals(0, fetchedKnot.getVersion());
        assertEquals("K1", fetchedKnot.getId());
        assertNull(fetchedKnot.getEdges());
        assertNull(fetchedKnot.getKnotData());
    }
}